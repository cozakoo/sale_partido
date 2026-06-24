package io.github.salepartido.api.devtools;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.salepartido.api.domain.eventos.model.EstadoEvento;
import io.github.salepartido.api.domain.eventos.model.EstadoParticipacion;
import io.github.salepartido.api.domain.eventos.model.Evento;
import io.github.salepartido.api.domain.eventos.model.HabilidadJugador;
import io.github.salepartido.api.domain.eventos.model.NivelDeporte;
import io.github.salepartido.api.domain.eventos.model.Participacion;
import io.github.salepartido.api.domain.eventos.model.Rol;
import io.github.salepartido.api.domain.eventos.model.TipoEvento;
import io.github.salepartido.api.domain.eventos.model.Usuario;
import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.HorarioAtencion;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Localidad;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.model.Ubicacion;
import net.datafaker.Faker;

@Service
@Profile("dev")
public class SeedService {

    private final SeedRepository seedRepository;
    private final Faker faker = new Faker(Locale.of("es"));
    private final int localesCount = SeedValues.LOCALES_COUNT;
    private final int canchasPerLocal = SeedValues.CANCHAS_PER_LOCAL;
    private final int usuariosCount = SeedValues.USUARIOS_COUNT;
    private final int capacidadDefault = SeedValues.CAPACIDAD_DEFAULT;
    private final int eventosFinalizadosCount = SeedValues.EVENTOS_FINALIZADOS_COUNT;
    private final int eventosActivosCount = SeedValues.EVENTOS_ACTIVOS_COUNT;
    private final List<String> localidadesList = SeedValues.LOCALIDADES_LIST;
    private final String[] nombresLocales = SeedValues.NOMBRES_LOCALES;
    private final String[] tematicas = SeedValues.TEMATICAS;
    private final String[] tiposCancha = SeedValues.TIPOS_CANCHA;
    private final String[] direcciones = SeedValues.DIRECCIONES;
    private final String[] deportesPredeterminados = SeedValues.DEPORTES_PREDETERMINADOS;
    private final Map<String, Integer> capacidadPorDeporte = SeedValues.CAPACIDAD_POR_DEPORTE;
    private final String[] nivelesPaddle = SeedValues.NIVELES_PADDLE;
    private final String[] nivelesFutbol = SeedValues.NIVELES_FUTBOL;
    private final String[] nivelesTenis = SeedValues.NIVELES_TENIS;
    private final String[] nivelesVoley = SeedValues.NIVELES_VOLEY;
    private final String[] nivelesBasquet = SeedValues.NIVELES_BASQUET;

    public SeedService(SeedRepository seedRepository) {
        this.seedRepository = seedRepository;
    }

    @Transactional
    public void generate() {
        List<Local> localesGuardados = guardarLocales(generarLocales(localesCount, canchasPerLocal));

        List<NivelDeporte> niveles = poblarNivelesDeporte();
        List<Usuario> usuarios = poblarUsuarios(usuariosCount, niveles);

        generarTurnosYEventos(localesGuardados, usuarios, niveles);
    }

    private List<Local> guardarLocales(List<Local> locales) {
        List<Local> localesGuardados = new ArrayList<>();
        for (Local local : locales) {
            localesGuardados.add(seedRepository.guardarLocal(local));
        }
        return localesGuardados;
    }

    /* GENERACIÓN =============================== */

    private String generarNombreLocal(Faker faker) {
        return faker.options().option(nombresLocales)
                + " "
                + faker.options().option(tematicas);
    }

    private String generarNombreCancha(Faker faker, int numero) {
        return "Cancha " + numero + " - " + faker.options().option(tiposCancha);
    }

    private List<Local> generarLocales(int cantidad, int canchasPorLocal) {
        List<Local> locales = new ArrayList<>();

        List<Localidad> localidadesExistentes = seedRepository.findLocalidadesExistentes();
        List<Localidad> localidades = new ArrayList<>();

        for (String nombreLoc : localidadesList) {
            Localidad localidad = localidadesExistentes.stream()
                .filter(l -> l.getNombre() != null && l.getNombre().equalsIgnoreCase(nombreLoc))
                .findFirst()
                .orElseGet(() -> {
                    Localidad newLoc = new Localidad();
                    newLoc.setNombre(nombreLoc);
                    return seedRepository.guardarLocalidad(newLoc);
                });
            localidades.add(localidad);
        }

        for (int i = 0; i < cantidad; i++) {
            Local local = new Local();
            local.setNombre(generarNombreLocal(faker));
            local.setTelefono(faker.phoneNumber().phoneNumber());
            local.setDescripcion("Complejo deportivo con excelentes instalaciones para disfrutar del deporte.");

            Localidad localidadElegida = localidades.get(faker.number().numberBetween(0, localidades.size()));

            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setLocalidad(localidadElegida);
            ubicacion.setDireccion(faker.options().option(direcciones));
            local.setUbicacion(ubicacion);

            local.setCanchas(generarCanchas(canchasPorLocal));
            local.setHorariosAtencion(generarHorariosAtencionSemanal());
            locales.add(local);
        }
        return locales;
    }

    private List<Cancha> generarCanchas(int count) {
        List<Cancha> canchas = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Cancha cancha = new Cancha();
            cancha.setNombre(generarNombreCancha(faker, i + 1));
            cancha.setConfiguracionesHorarios(List.of(generarConfiguracionHorario()));

            Deporte deporte = obtenerDeporteAleatorioPersistido();
            cancha.setDeporte(deporte);
            cancha.setCapacidad(capacidadPorDeporte.getOrDefault(deporte.getNombre(), capacidadDefault));

            canchas.add(cancha);
        }
        return canchas;
    }

    private ConfiguracionHorario generarConfiguracionHorario() {
        ConfiguracionHorario horario = new ConfiguracionHorario();
        horario.setActivo(true);
        horario.setDuracionTurno(SeedValues.DURACION_TURNO);
        DayOfWeek[] sixDays = {
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        };
        horario.setConfiguracionesDias(generarConfiguracionesDias(sixDays));
        return horario;
    }

    private List<ConfiguracionDia> generarConfiguracionesDias(DayOfWeek[] diasSemana) {
        List<ConfiguracionDia> configuracionesDias = new ArrayList<>();

        for (DayOfWeek dayOfWeek : diasSemana) {
            ConfiguracionDia dia = new ConfiguracionDia();
            dia.setDiaSemana(dayOfWeek);
            dia.setHoraInicio(SeedValues.HORA_INICIO_ATENCION);
            dia.setHoraFin(SeedValues.HORA_FIN_ATENCION);

            configuracionesDias.add(dia);
        }
        return configuracionesDias;
    }

    private ConfiguracionDia obtenerConfiguracionDia(Cancha cancha, DayOfWeek dayOfWeek) {
        if (cancha.getConfiguracionesHorarios() == null) {
            return null;
        }
        ConfiguracionHorario activeConfig = cancha.getConfiguracionesHorarios().stream()
                .filter(ConfiguracionHorario::isActivo)
                .findFirst()
                .orElse(null);
        if (activeConfig == null || activeConfig.getConfiguracionesDias() == null) {
            return null;
        }
        return activeConfig.getConfiguracionesDias().stream()
                .filter(d -> d.getDiaSemana() == dayOfWeek)
                .findFirst()
                .orElse(null);
    }

    private void guardarTurnosYEventosEnLotes(List<Turno> turnos, List<Evento> eventos) {
        int batchSize = 1000;
        for (int i = 0; i < turnos.size(); i += batchSize) {
            int toIndex = Math.min(i + batchSize, turnos.size());
            List<Turno> batchTurnos = turnos.subList(i, toIndex);
            List<Evento> batchEventos = eventos.subList(i, toIndex);
            seedRepository.guardarTodosTurnos(batchTurnos);
            seedRepository.guardarTodosEventos(batchEventos);
        }
    }

    private void generarTurnosYEventos(List<Local> locales, List<Usuario> usuarios, List<NivelDeporte> niveles) {
        List<Cancha> allCanchas = new ArrayList<>();
        for (Local local : locales) {
            if (local.getCanchas() != null) {
                allCanchas.addAll(local.getCanchas());
            }
        }
        if (allCanchas.isEmpty()) return;

        List<Usuario> deportistas = usuarios.stream()
                .filter(u -> u.getRol() == Rol.DEPORTISTA)
                .toList();
        if (deportistas.isEmpty()) return;

        generarEventosFinalizados(allCanchas, deportistas, niveles);
        generarEventosActivos(allCanchas, deportistas, niveles);
    }

    private void generarEventosFinalizados(List<Cancha> allCanchas, List<Usuario> deportistas, List<NivelDeporte> niveles) {
        List<Turno> turnosFinalizados = new ArrayList<>();
        List<Evento> eventosFinalizados = new ArrayList<>();

        int finishedEventsCreated = 0;
        int targetFinishedEvents = eventosFinalizadosCount;
        LocalDate date = LocalDate.now().minusDays(1);

        while (finishedEventsCreated < targetFinishedEvents) {
            for (Cancha cancha : allCanchas) {
                if (finishedEventsCreated >= targetFinishedEvents) {
                    break;
                }

                DayOfWeek dayOfWeek = date.getDayOfWeek();
                ConfiguracionDia diaConfig = obtenerConfiguracionDia(cancha, dayOfWeek);
                if (diaConfig != null) {
                    if (faker.random().nextDouble() < 0.85) {
                        int slotsCount = (int) Duration.between(diaConfig.getHoraInicio(), diaConfig.getHoraFin()).toHours();
                        if (slotsCount > 0) {
                            int slotIndex = faker.number().numberBetween(0, slotsCount);
                            LocalTime start = diaConfig.getHoraInicio().plusHours(slotIndex);
                            LocalTime end = start.plusHours(1);

                            Turno turno = new Turno();
                            turno.setCancha(cancha);
                            turno.setFecha(date);
                            turno.setHoraInicio(start);
                            turno.setHoraFin(end);
                            turnosFinalizados.add(turno);

                            Usuario organizador = deportistas.get(faker.random().nextInt(deportistas.size()));

                            List<NivelDeporte> nivelesFiltrados = niveles.stream()
                                    .filter(nivel -> nivel.getDeporte().getUuid().equals(cancha.getDeporte().getUuid()))
                                    .toList();
                            NivelDeporte nivelRequerido = nivelesFiltrados.isEmpty() ? null
                                    : nivelesFiltrados.get(faker.random().nextInt(nivelesFiltrados.size()));

                            Evento evento = new Evento();
                            evento.setNombre("Partido de " + cancha.getDeporte().getNombre());
                            evento.setTipo(faker.options().option(TipoEvento.values()));
                            evento.setCupoMinimo(2);
                            evento.setCupoMaximo(cancha.getCapacidad());
                            evento.setEstado(EstadoEvento.FINALIZADO);
                            evento.setNivelRequerido(nivelRequerido);
                            evento.setTurno(turno);
                            evento.setOrganizador(organizador);
                            evento.setParticipaciones(generarParticipacionesFinalizadas(deportistas, organizador, cancha.getCapacidad()));

                            eventosFinalizados.add(evento);
                            finishedEventsCreated++;
                        }
                    }
                }
            }
            date = date.minusDays(1);
        }
        guardarTurnosYEventosEnLotes(turnosFinalizados, eventosFinalizados);
    }

    private void generarEventosActivos(List<Cancha> allCanchas, List<Usuario> deportistas, List<NivelDeporte> niveles) {
        List<Turno> turnosActivos = new ArrayList<>();
        List<Evento> eventosActivos = new ArrayList<>();

        int activeEventsCreated = 0;
        int targetActiveEvents = eventosActivosCount;
        LocalDate date = LocalDate.now();

        while (activeEventsCreated < targetActiveEvents) {
            for (Cancha cancha : allCanchas) {
                if (activeEventsCreated >= targetActiveEvents) {
                    break;
                }

                DayOfWeek dayOfWeek = date.getDayOfWeek();
                ConfiguracionDia diaConfig = obtenerConfiguracionDia(cancha, dayOfWeek);
                if (diaConfig != null) {
                    if (faker.random().nextDouble() < 0.35) {
                        int slotsCount = (int) Duration.between(diaConfig.getHoraInicio(), diaConfig.getHoraFin()).toHours();
                        if (slotsCount > 0) {
                            int slotIndex = faker.number().numberBetween(0, slotsCount);
                            LocalTime start = diaConfig.getHoraInicio().plusHours(slotIndex);
                            LocalTime end = start.plusHours(1);

                            final LocalDate currentFecha = date;
                            final LocalTime currentStart = start;
                            boolean overlap = turnosActivos.stream()
                                    .anyMatch(t -> t.getCancha().getUuid().equals(cancha.getUuid())
                                            && t.getFecha().equals(currentFecha)
                                            && t.getHoraInicio().equals(currentStart));

                            if (!overlap) {
                                Turno turno = new Turno();
                                turno.setCancha(cancha);
                                turno.setFecha(date);
                                turno.setHoraInicio(start);
                                turno.setHoraFin(end);
                                turnosActivos.add(turno);

                                Usuario organizador = deportistas.get(faker.random().nextInt(deportistas.size()));

                                List<NivelDeporte> nivelesFiltrados = niveles.stream()
                                        .filter(nivel -> nivel.getDeporte().getUuid().equals(cancha.getDeporte().getUuid()))
                                        .toList();
                                NivelDeporte nivelRequerido = nivelesFiltrados.isEmpty() ? null
                                        : nivelesFiltrados.get(faker.random().nextInt(nivelesFiltrados.size()));

                                String tipoCupo;
                                int mod = activeEventsCreated % 3;
                                if (mod == 0) {
                                    tipoCupo = "LLENO";
                                } else if (mod == 1) {
                                    tipoCupo = "VACIO";
                                } else {
                                    tipoCupo = "PARCIAL";
                                }

                                Evento evento = new Evento();
                                evento.setNombre("Partido de " + cancha.getDeporte().getNombre());
                                evento.setTipo(faker.options().option(TipoEvento.values()));
                                evento.setCupoMinimo(2);
                                evento.setCupoMaximo(cancha.getCapacidad());

                                if ("LLENO".equals(tipoCupo)) {
                                    evento.setEstado(EstadoEvento.COMPLETO);
                                } else {
                                    evento.setEstado(EstadoEvento.DISPONIBLE);
                                }

                                evento.setNivelRequerido(nivelRequerido);
                                evento.setTurno(turno);
                                evento.setOrganizador(organizador);
                                evento.setParticipaciones(generarParticipacionesActivas(deportistas, organizador, cancha.getCapacidad(), tipoCupo));

                                eventosActivos.add(evento);
                                activeEventsCreated++;
                            }
                        }
                    }
                }
            }
            date = date.plusDays(1);
        }
        guardarTurnosYEventosEnLotes(turnosActivos, eventosActivos);
    }

    private List<HorarioAtencion> generarHorariosAtencionSemanal() {
        List<HorarioAtencion> horarios = new ArrayList<>();

        for (DayOfWeek dia : DayOfWeek.values()) {
            int apertura = faker.number().numberBetween(8, 12);
            int cierre = faker.number().numberBetween(22, 24);
            LocalTime horaApertura = LocalTime.of(apertura, 0);
            LocalTime horaCierre = LocalTime.of(cierre, 0);

            HorarioAtencion horario = new HorarioAtencion();
            horario.setDia(dia);
            horario.setHorarioApertura(horaApertura);
            horario.setHorarioCierre(horaCierre);

            horarios.add(horario);
        }
        return horarios;
    }

    private List<Deporte> poblarDeportes() {
        List<Deporte> deportes = new ArrayList<>();
        for (String nombre : deportesPredeterminados) {
            Deporte deporte = seedRepository.findDeporteByNombre(nombre)
                    .orElseGet(() -> {
                        Deporte nuevo = new Deporte();
                        nuevo.setNombre(nombre);
                        return seedRepository.guardarDeporte(nuevo);
                    });
            deportes.add(deporte);
        }
        return deportes;
    }

    private Deporte obtenerDeporteAleatorioPersistido() {
        List<Deporte> deportes = seedRepository.findDeportesExistentes();
        if (deportes.isEmpty()) {
            deportes = poblarDeportes();
        }
        return deportes.get(faker.random().nextInt(deportes.size()));
    }

    /* PARTICIPATION =============================== */

    private String[] obtenerDefinicionesNiveles(String deporteNombre) {
        if (deporteNombre == null) return new String[0];
        return switch (deporteNombre) {
            case "Paddle" -> nivelesPaddle;
            case "Fútbol" -> nivelesFutbol;
            case "Tenis" -> nivelesTenis;
            case "Vóley" -> nivelesVoley;
            case "Básquet" -> nivelesBasquet;
            default -> new String[0];
        };
    }

    private List<NivelDeporte> poblarNivelesDeporte() {
        List<NivelDeporte> todos = new ArrayList<>();
        List<Deporte> deportes = seedRepository.findDeportesExistentes();
        if (deportes.isEmpty()) {
            deportes = poblarDeportes();
        }

        for (Deporte deporte : deportes) {
            String[] rawDefiniciones = obtenerDefinicionesNiveles(deporte.getNombre());
            if (rawDefiniciones == null || rawDefiniciones.length == 0) continue;

            List<NivelDeporte> existentes = seedRepository.findNivelesDeportePorDeporte(deporte.getUuid());
            if (!existentes.isEmpty()) {
                todos.addAll(existentes);
                continue;
            }

            for (int i = 0; i < rawDefiniciones.length; i++) {
                String raw = rawDefiniciones[i];
                String[] parts = raw.split(":", 2);
                String nombreNivel = parts[0];
                String descNivel = parts.length > 1 ? parts[1] : "";

                NivelDeporte nivel = new NivelDeporte();
                nivel.setNombre(nombreNivel);
                nivel.setDescripcion(descNivel);
                nivel.setOrden(i + 1);
                nivel.setDeporte(deporte);
                todos.add(seedRepository.guardarNivelDeporte(nivel));
            }
        }
        return todos;
    }

    private List<Usuario> poblarUsuarios(int cantidad, List<NivelDeporte> niveles) {
        List<Usuario> usuarios = new ArrayList<>();

        Map<Deporte, List<NivelDeporte>> nivelesPorDeporte = niveles.stream()
                .filter(n -> n.getDeporte() != null)
                .collect(Collectors.groupingBy(NivelDeporte::getDeporte));

        for (int i = 0; i < cantidad; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre(faker.name().fullName());
            usuario.setRol(i < 100 ? Rol.PROPIETARIO : Rol.DEPORTISTA);

            if (usuario.getRol() == Rol.DEPORTISTA && !nivelesPorDeporte.isEmpty()) {
                for (Map.Entry<Deporte, List<NivelDeporte>> entry : nivelesPorDeporte.entrySet()) {
                    if (faker.random().nextBoolean()) {
                        List<NivelDeporte> levels = entry.getValue();
                        if (levels != null && !levels.isEmpty()) {
                            NivelDeporte selectedNivel = levels.get(faker.random().nextInt(levels.size()));
                            HabilidadJugador hj = new HabilidadJugador();
                            hj.setDeporte(entry.getKey());
                            hj.setNivel(selectedNivel);
                            usuario.getHabilidades().add(hj);
                        }
                    }
                }
            }

            usuarios.add(usuario);
        }
        return seedRepository.guardarTodosUsuarios(usuarios);
    }

    private List<Participacion> generarParticipacionesFinalizadas(List<Usuario> deportistas, Usuario organizador, int capacidad) {
        List<Participacion> participaciones = new ArrayList<>();

        Participacion participacionOrg = new Participacion();
        participacionOrg.setEsInvitacion(false);
        participacionOrg.setEstado(EstadoParticipacion.CONFIRMADO);
        participacionOrg.setFechaEstado(LocalDateTime.now().minusDays(3));
        participacionOrg.setAsistio(true);
        participacionOrg.setParticipante(organizador);
        participaciones.add(participacionOrg);

        List<Usuario> candidatos = deportistas.stream()
                .filter(u -> !u.getUuid().equals(organizador.getUuid()))
                .toList();

        int targetConfirmed = faker.number().numberBetween(capacidad / 2, capacidad + 1);
        if (targetConfirmed < 2) targetConfirmed = 2;

        int added = 1;
        int candidatesSize = candidatos.size();
        java.util.Set<Integer> selectedIndices = new java.util.HashSet<>();
        while (added < targetConfirmed && selectedIndices.size() < candidatesSize) {
            int idx = faker.random().nextInt(candidatesSize);
            if (selectedIndices.add(idx)) {
                Participacion p = new Participacion();
                p.setEsInvitacion(false);
                p.setEstado(EstadoParticipacion.CONFIRMADO);
                p.setFechaEstado(LocalDateTime.now().minusDays(2));
                p.setAsistio(true);
                p.setParticipante(candidatos.get(idx));
                participaciones.add(p);
                added++;
            }
        }

        return participaciones;
    }

    private List<Participacion> generarParticipacionesActivas(List<Usuario> deportistas, Usuario organizador, int capacidad, String tipoCupo) {
        List<Participacion> participaciones = new ArrayList<>();

        Participacion participacionOrg = new Participacion();
        participacionOrg.setEsInvitacion(false);
        participacionOrg.setEstado(EstadoParticipacion.CONFIRMADO);
        participacionOrg.setFechaEstado(LocalDateTime.now().minusHours(2));
        participacionOrg.setAsistio(false);
        participacionOrg.setParticipante(organizador);
        participaciones.add(participacionOrg);

        List<Usuario> candidatos = deportistas.stream()
                .filter(u -> !u.getUuid().equals(organizador.getUuid()))
                .toList();

        int targetConfirmed;
        if ("LLENO".equals(tipoCupo)) {
            targetConfirmed = capacidad;
        } else if ("VACIO".equals(tipoCupo)) {
            targetConfirmed = 1;
        } else {
            targetConfirmed = faker.number().numberBetween(2, capacidad);
        }

        int added = 1;
        int candidatesSize = candidatos.size();
        java.util.Set<Integer> selectedIndices = new java.util.HashSet<>();
        while (added < targetConfirmed && selectedIndices.size() < candidatesSize) {
            int idx = faker.random().nextInt(candidatesSize);
            if (selectedIndices.add(idx)) {
                Participacion p = new Participacion();
                p.setEsInvitacion(false);
                p.setEstado(EstadoParticipacion.CONFIRMADO);
                p.setFechaEstado(LocalDateTime.now().minusHours(1));
                p.setAsistio(false);
                p.setParticipante(candidatos.get(idx));
                participaciones.add(p);
                added++;
            }
        }

        return participaciones;
    }
}
