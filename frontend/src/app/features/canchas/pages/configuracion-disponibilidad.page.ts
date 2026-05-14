import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { LocalService } from '../services/configuracion-disponibilidad.service';
import { CanchaDetail } from '../models/cancha-detail';
import { ConfiguracionDia } from '../models/configuracion-dia';
import { Constantes } from '../../../core/Constantes';
import { ActivatedRoute } from '@angular/router';
import { SaveCanchaConfiguracionHorarioRequest } from '../models/cancha-configuracion-horario-request';
 

@Component({
  selector: 'app-schedule-config',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgbNavModule],
  templateUrl: './configuracion-disponibilidad.page.html',
  styleUrl: './configuracion-disponibilidad.page.scss',
})
export class ConfiguracionDisponibilidadPage implements OnInit {
  
  localService = inject(LocalService);
  fb = inject(FormBuilder);
  location = inject(Location)
  route = inject(ActivatedRoute)

  localUuid!: string
  localNombre!: string
  canchasDetail: CanchaDetail[] = [];
  duracionesTurno = Constantes.SLOT_DURATIONS_MINUTES;
 
  form: FormGroup = this.fb.group({
    canchas: this.fb.group({})
  });
  activeTabId: any = null;

  saving = false;
  saveSuccess = false;
  saveError = false;
 
  get canchasFormGroup(): FormGroup {
    return this.form.get('canchas') as FormGroup;
  }

  getCanchaFormGroup(canchaId: any): FormGroup | null {
    return this.canchasFormGroup.get(String(canchaId)) as FormGroup | null;
  }

  getDiasFormArray(canchaId: any): FormArray {
    return this.getCanchaFormGroup(canchaId)?.get('configuracionesDias') as FormArray;
  }

  timeRangeValidator = (group: AbstractControl): ValidationErrors | null => {
    const activo = group.get('activo')?.value;
    if (!activo) return null;
    const inicio = group.get('horaInicio')?.value;
    const fin = group.get('horaFin')?.value;
    if (!inicio || !fin) return { required: true };
    if (inicio >= fin) return { invalidTimeRange: true };
    return null;
  }

  ngOnInit(): void {
    let uuid = this.route.snapshot.paramMap.get('uuid');
    if (uuid != null){
      this.localUuid = uuid

      this.localService.getLocalDetail(uuid).subscribe(s => {
        this.localNombre = 'Configuración de Horarios del Local "'+s.nombre+'"';
      });

      this.localService.getCanchasDetailFromLocal(uuid).subscribe(s => {
        this.canchasDetail = s;
        if (s.length > 0) {
          this.activeTabId = this.getCanchaId(s[0]);
          
          s.forEach(cancha => {
            let duracionTurno = 60;
            // Iniciamos todos los días deshabilitados pero con sus horas por defecto
            let configuracionesDias: ConfiguracionDia[] = Constantes.DEFAULT_DAYS.map(d => ({
              ...d,
              horaInicio: d.horaInicio.substring(0, 5),
              horaFin: d.horaFin.substring(0, 5),
              activo: false
            }));

            if (cancha.configuracionesHorarios && cancha.configuracionesHorarios.length > 0) {
              const config = cancha.configuracionesHorarios[0];
              if (config.duracionTurno) {
                duracionTurno = config.duracionTurno;
              }
              if (config.configuracionesDias && config.configuracionesDias.length > 0) {
                configuracionesDias = configuracionesDias.map(defaultDay => {
                  const backendDay = config.configuracionesDias.find(d => d.diaSemana.toUpperCase() === defaultDay.diaSemana.toUpperCase());
                  // Si el backend trajo el día y está activo, lo habilitamos y tomamos sus horas (o el default como fallback)
                  if (backendDay && backendDay.activo) {
                    let hIni: any = backendDay.horaInicio || defaultDay.horaInicio;
                    let hFin: any = backendDay.horaFin || defaultDay.horaFin;

                    if (Array.isArray(hIni)) hIni = `${String(hIni[0]).padStart(2, '0')}:${String(hIni[1] || 0).padStart(2, '0')}`;
                    else if (typeof hIni === 'string') hIni = hIni.substring(0, 5);

                    if (Array.isArray(hFin)) hFin = `${String(hFin[0]).padStart(2, '0')}:${String(hFin[1] || 0).padStart(2, '0')}`;
                    else if (typeof hFin === 'string') hFin = hFin.substring(0, 5);

                    return { 
                      ...defaultDay,
                      horaInicio: hIni,
                      horaFin: hFin,
                      activo: true 
                    };
                  }
                  // Si no viene o no está activo, queda como lo inicializamos (deshabilitado y horas por defecto)
                  return defaultDay;
                });
              }
            }

            const configuracionesDiasArray = configuracionesDias.map(defaultDay => {
              const diaGroup = this.fb.group({
                diaSemana: [defaultDay.diaSemana],
                activo: [defaultDay.activo],
                horaInicio: [{ value: defaultDay.horaInicio, disabled: !defaultDay.activo }],
                horaFin: [{ value: defaultDay.horaFin, disabled: !defaultDay.activo }]
              }, { validators: this.timeRangeValidator });

              diaGroup.get('activo')?.valueChanges.subscribe(activo => {
                if (activo) {
                  diaGroup.get('horaInicio')?.enable();
                  diaGroup.get('horaFin')?.enable();
                } else {
                  diaGroup.get('horaInicio')?.disable();
                  diaGroup.get('horaFin')?.disable();
                }
              });

              return diaGroup;
            });

            const canchaGroup = this.fb.group({
              duracionTurno: [duracionTurno, Validators.required],
              configuracionesDias: this.fb.array(configuracionesDiasArray)
            });

            this.canchasFormGroup.addControl(String(this.getCanchaId(cancha)), canchaGroup);
          });
        }
      });
    }
  }
 
  getCanchaId(cancha: CanchaDetail | any): any {
    return cancha.uuid || cancha.id || cancha.nombre;
  }
 
  getCanchaNombre(cancha: CanchaDetail | any): string {
    return cancha.nombre || 'Cancha sin nombre';
  }

  aplicarATodas(sourceId: any): void {
    const sourceGroup = this.getCanchaFormGroup(sourceId);
    if (!sourceGroup) return;

    const sourceValue = sourceGroup.getRawValue();

    this.canchasDetail.forEach(cancha => {
      const targetId = this.getCanchaId(cancha);
      if (targetId !== sourceId) {
        const targetGroup = this.getCanchaFormGroup(targetId);
        if (targetGroup) {
          targetGroup.patchValue(sourceValue);
          const sourceDias = sourceGroup.get('configuracionesDias') as FormArray;
          const targetDias = targetGroup.get('configuracionesDias') as FormArray;
          // Forzamos disparar los valueChanges de los 'activo' en las otras canchas para que se habiliten/deshabiliten los inputs
          for (let i = 0; i < sourceDias.length; i++) {
            const activo = sourceDias.at(i).get('activo')?.value;
            targetDias.at(i).get('activo')?.setValue(activo);
          }
        }
      }
    });
  }
 
  onSubmit(): void {
    if (this.form.invalid || this.canchasDetail.length === 0) return;

    // getRawValue() extrae todos los valores, incluso de los FormControls que están disabled programáticamente
    const canchasVal = this.canchasFormGroup.getRawValue();
    
    // Formar el arreglo con todas las configuraciones para enviarlas al backend
    const canchasConfig = Object.keys(canchasVal).map(canchaId => ({
      canchaUuid: canchaId,
      duracionTurno: canchasVal[canchaId].duracionTurno,
      configuracionesDias: canchasVal[canchaId].configuracionesDias
    }));

    const request = {
      canchas: canchasConfig
    } as unknown as SaveCanchaConfiguracionHorarioRequest;
 
    this.saving = true;
    this.saveSuccess = false;
    this.saveError = false;
 
    this.localService.saveLocalConfiguracionesHorarios(this.localUuid, request).subscribe({
      next: () => { this.saving = false; this.saveSuccess = true; },
      error: () => { this.saving = false; this.saveError = true; },
    });
  }

  onCancel(): void {
    this.location.back();
  }

}