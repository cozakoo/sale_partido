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
    private final List<String> localidadesList = SeedValues.LOCALIDADES_LIST;
    private final String[] nombresLocales = SeedValues.NOMBRES_LOCALES;
    private final String[] tematicas = SeedValues.TEMATICAS;
    private final String[] tiposCancha = SeedValues.TIPOS_CANCHA;
    private final String[] direcciones = SeedValues.DIRECCIONES;
    private final String[] deportesPredeterminados = SeedValues.DEPORTES_PREDETERMINADOS;
    private final String[] nombresUsuarios = SeedValues.NOMBRES_USUARIOS;
    private final Map<String, Integer> capacidadPorDeporte = SeedValues.CAPACIDAD_POR_DEPORTE;
    private final String[] nivelesPaddle = SeedValues.NIVELES_PADDLE;
    private final String[] nivelesFutbol = SeedValues.NIVELES_FUTBOL;
    private final String[] nivelesTenis = SeedValues.NIVELES_TENIS;
    private final String[] nivelesVoley = SeedValues.NIVELES_VOLEY;
    private final String[] nivelesBasquet = SeedValues.NIVELES_BASQUET;

    public SeedService(SeedRepository seedRepository) {
        this.seedRepository = seedRepository;
    }

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
        horario.setDuracionTurno(
                faker.options().option(
                        Duration.ofMinutes(30),
                        Duration.ofMinutes(60)));
        horario.setConfiguracionesDias(generarConfiguracionesDias(DayOfWeek.values()));
        return horario;
    }

    private List<ConfiguracionDia> generarConfiguracionesDias(DayOfWeek[] diasSemana) {
        List<ConfiguracionDia> configuracionesDias = new ArrayList<>();

        for (DayOfWeek dayOfWeek : diasSemana) {
            int horaInicio = faker.number().numberBetween(8, 14);
            int horaFin = faker.number().numberBetween(16, 22);
            LocalTime inicio = LocalTime.of(horaInicio, 0);
            LocalTime fin = LocalTime.of(horaFin, 0);

            ConfiguracionDia dia = new ConfiguracionDia();
            dia.setDiaSemana(dayOfWeek);
            dia.setHoraInicio(inicio);
            dia.setHoraFin(fin);

            configuracionesDias.add(dia);
        }
        return configuracionesDias;
    }

    private void generarTurnosYEventos(List<Local> locales, List<Usuario> usuarios, List<NivelDeporte> niveles) {
        List<Usuario> deportistas = usuarios.stream()
                .filter(u -> u.getRol() == Rol.DEPORTISTA)
                .toList();
        if (deportistas.isEmpty()) return;

        LocalDate hoy = LocalDate.now();
        List<LocalDate> fechas = List.of(hoy.minusDays(1), hoy, hoy.plusDays(1), hoy.plusDays(2));

        for (Local local : locales) {
            for (Cancha cancha : local.getCanchas()) {
                ConfiguracionHorario horario = cancha.getConfiguracionesHorarios().stream()
                        .filter(ConfiguracionHorario::isActivo)
                        .findFirst()
                        .orElse(null);

                if (horario != null && !horario.getConfiguracionesDias().isEmpty()) {
                    Duration duration = horario.getDuracionTurno();
                    for (LocalDate fecha : fechas) {
                        DayOfWeek dayOfWeek = fecha.getDayOfWeek();
                        ConfiguracionDia diaConfig = horario.getConfiguracionesDias().stream()
                                .filter(d -> d.getDiaSemana() == dayOfWeek)
                                .findFirst()
                                .orElse(null);

                        if (diaConfig != null) {
                            LocalTime horaInicioLocal = diaConfig.getHoraInicio();
                            LocalTime horaFinLocal = diaConfig.getHoraFin();

                            // Reservamos un turno a las horaInicio + 2 horas
                            LocalTime startReserva = horaInicioLocal.plusHours(2);
                            LocalTime endReserva = startReserva.plus(duration);

                            if (endReserva.isBefore(horaFinLocal) || endReserva.equals(horaFinLocal)) {
                                Turno turno = new Turno();
                                turno.setCancha(cancha);
                                turno.setFecha(fecha);
                                turno.setHoraInicio(startReserva);
                                turno.setHoraFin(endReserva);
                                turno = seedRepository.guardarTurno(turno);

                                Usuario organizador = deportistas.get(faker.random().nextInt(deportistas.size()));

                                List<NivelDeporte> nivelesFiltrados = niveles.stream()
                                    .filter(nivel -> nivel.getDeporte().getUuid().equals(cancha.getDeporte().getUuid()))
                                    .toList();
                                NivelDeporte nivelRequerido = nivelesFiltrados.isEmpty() ? null
                                        : nivelesFiltrados.get(faker.random().nextInt(nivelesFiltrados.size()));

                                Evento evento = new Evento();
                                String deporteName = cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : "Fútbol";
                                evento.setNombre("Partido de " + deporteName);
                                evento.setTipo(faker.options().option(TipoEvento.values()));
                                evento.setCupoMinimo(2);
                                evento.setCupoMaximo(cancha.getCapacidad());

                                if (fecha.isBefore(hoy)) {
                                    evento.setEstado(EstadoEvento.FINALIZADO);
                                } else {
                                    evento.setEstado(EstadoEvento.DISPONIBLE);
                                }

                                evento.setNivelRequerido(nivelRequerido);
                                evento.setTurno(turno);
                                evento.setOrganizador(organizador);
                                evento.setParticipaciones(generarParticipaciones(deportistas, organizador));

                                seedRepository.guardarEvento(evento);
                            }
                        }
                    }
                }
            }
        }
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
        int cantidadReal = Math.min(cantidad, nombresUsuarios.length);

        Map<Deporte, List<NivelDeporte>> nivelesPorDeporte = niveles.stream()
                .filter(n -> n.getDeporte() != null)
                .collect(Collectors.groupingBy(NivelDeporte::getDeporte));

        for (int i = 0; i < cantidadReal; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombresUsuarios[i]);
            // Los últimos 2 son PROPIETARIO, el resto DEPORTISTA
            usuario.setRol(i < cantidadReal - 2 ? Rol.DEPORTISTA : Rol.PROPIETARIO);

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

            usuarios.add(seedRepository.guardarUsuario(usuario));
        }
        return usuarios;
    }

    private List<Participacion> generarParticipaciones(List<Usuario> deportistas, Usuario organizador) {
        List<Participacion> participaciones = new ArrayList<>();

        // El organizador siempre participa como CONFIRMADO
        Participacion participacionOrg = new Participacion();
        participacionOrg.setEsInvitacion(false);
        participacionOrg.setEstado(EstadoParticipacion.CONFIRMADO);
        participacionOrg.setFechaEstado(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 5)));
        participacionOrg.setAsistio(false);
        participacionOrg.setParticipante(organizador);
        participaciones.add(participacionOrg);

        // Agregar entre 1 y 3 participantes adicionales
        int extras = faker.number().numberBetween(1, 4);
        List<Usuario> candidatos = deportistas.stream()
                .filter(u -> !u.getUuid().equals(organizador.getUuid()))
                .toList();

        for (int i = 0; i < extras && i < candidatos.size(); i++) {
            Participacion p = new Participacion();
            p.setEsInvitacion(faker.bool().bool());
            p.setEstado(faker.options().option(EstadoParticipacion.values()));
            p.setFechaEstado(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 3)));
            p.setAsistio(false);
            p.setParticipante(candidatos.get(i));
            participaciones.add(p);
        }

        return participaciones;
    }
}
