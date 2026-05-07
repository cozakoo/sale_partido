import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import {
  ScheduleConfig,
  DaySchedule,
  Space,
  ScheduleScope,
  DEFAULT_DAYS,
  SLOT_DURATIONS,
} from './schedule-config.model';
import { ScheduleConfigService } from '../../services/schedule-config';
 
@Component({
  selector: 'app-schedule-config',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './schedule-config.html',
  styleUrl: './schedule-config.scss',
})
export class ScheduleConfigComponent implements OnInit {
  scope: ScheduleScope = 'global';
  selectedSpaceId: number | null = null;
  spaces: Space[] = [];
  slotDurations = SLOT_DURATIONS;
 
  days: DaySchedule[] = DEFAULT_DAYS.map(d => ({ ...d }));
  slotDurationMinutes = 60;
 
  saving = false;
  saveSuccess = false;
  saveError = false;
 
  constructor(private scheduleService: ScheduleConfigService) {}
 
  ngOnInit(): void {
    this.scheduleService.getSpaces().subscribe(s => (this.spaces = s));
  }
 
  onScopeChange(newScope: ScheduleScope): void {
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
    const hasAtLeastOneDay = this.days.some(d => d.enabled);
    if (!hasAtLeastOneDay) return false;
    return this.days.every(d => !d.enabled || (!!d.from && !!d.to && d.from < d.to));
  }
 
  onSubmit(form: NgForm): void {
    if (!form.valid || !this.isFormValid()) return;
 
    const config: ScheduleConfig = {
      scope: this.scope,
      spaceId: this.scope === 'individual' ? this.selectedSpaceId : null,
      days: this.days,
      slotDurationMinutes: this.slotDurationMinutes,
    };
 
    this.saving = true;
    this.saveSuccess = false;
    this.saveError = false;
 
    this.scheduleService.saveConfig(config).subscribe({
      next: () => { this.saving = false; this.saveSuccess = true; },
      error: () => { this.saving = false; this.saveError = true; },
    });
  }
}