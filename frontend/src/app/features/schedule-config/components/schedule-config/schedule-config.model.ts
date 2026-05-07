export type ScheduleScope = 'global' | 'individual';
 
export interface DaySchedule {
  day: string;
  enabled: boolean;
  from: string;
  to: string;
}
 
export interface ScheduleConfig {
  scope: ScheduleScope;
  spaceId?: number | null;
  days: DaySchedule[];
  slotDurationMinutes: number;
}
 
export interface Space {
  id: number;
  name: string;
}
 
export const DEFAULT_DAYS: DaySchedule[] = [
  { day: 'Lunes',     enabled: true,  from: '09:00', to: '22:00' },
  { day: 'Martes',    enabled: true,  from: '09:00', to: '22:00' },
  { day: 'Miércoles', enabled: true,  from: '09:00', to: '22:00' },
  { day: 'Jueves',    enabled: true,  from: '09:00', to: '22:00' },
  { day: 'Viernes',   enabled: true,  from: '09:00', to: '23:00' },
  { day: 'Sábado',    enabled: true,  from: '08:00', to: '23:00' },
  { day: 'Domingo',   enabled: false, from: '09:00', to: '18:00' },
];
 
export const SLOT_DURATIONS = [30, 45, 60, 90, 120];