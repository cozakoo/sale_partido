import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ConfiguracionHorario } from '../models/configuracion-horario';
import { ScheduleConfigService } from '../services/configuracion-disponibilidad.service';
import { AlcanceConfiguracionHorario } from '../models/alcance-configuracion-horario';
import { Cancha } from '../models/cancha';
import { ConfiguracionDia } from '../models/configuracion-dia';
import { Constantes } from '../../../core/Constantes';
 

@Component({
  selector: 'app-schedule-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './configuracion-disponibilidad.page.html',
  styleUrl: './configuracion-disponibilidad.page.scss',
})
export class ConfiguracionDisponibilidadPage implements OnInit {
  
  scope: AlcanceConfiguracionHorario = 'global';
  selectedSpaceId: number | null = null;
  spaces: Cancha[] = [];
  slotDurations = Constantes.SLOT_DURATIONS_MINUTES;
 
  days: ConfiguracionDia[] = Constantes.DEFAULT_DAYS;
  slotDurationMinutes = 60;
 
  saving = false;
  saveSuccess = false;
  saveError = false;
 
  constructor(private scheduleService: ScheduleConfigService) {}
 
  ngOnInit(): void {
    this.scheduleService.getSpaces().subscribe(s => (this.spaces = s));
  }
 
  onScopeChange(newScope: AlcanceConfiguracionHorario): void {
    this.scope = newScope;
    this.selectedSpaceId = null;
    this.saveSuccess = false;
    this.saveError = false;
  }
 
  onSpaceSelect(spaceId: number): void {
    this.selectedSpaceId = spaceId;
  }
 
  isFormValid(): boolean {
    if (this.scope === 'individual' && !this.selectedSpaceId) return false;
    const hasAtLeastOneDay = this.days.some(d => d.activo);
    if (!hasAtLeastOneDay) return false;
    return this.days.every(d => !d.activo || (!!d.horaInicio && !!d.horaFin && d.horaInicio < d.horaFin));
  }
 
  onSubmit(form: NgForm): void {
    if (!form.valid || !this.isFormValid()) return;
 
    const config: ConfiguracionHorario = {
      alcance: this.scope,
      configuracionesDias: this.days,
      duracionTurno: this.slotDurationMinutes,
    } as ConfiguracionHorario;
 
    this.saving = true;
    this.saveSuccess = false;
    this.saveError = false;
 
    this.scheduleService.saveConfig(config).subscribe({
      next: () => { this.saving = false; this.saveSuccess = true; },
      error: () => { this.saving = false; this.saveError = true; },
    });
  }
}