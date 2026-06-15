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

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.HorarioAtencion;
import io.github.salepartido.api.domain.locales.model.Local;
import io.github.salepartido.api.domain.locales.model.Localidad;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.model.Ubicacion;
import io.github.salepartido.api.domain.locales.repository.DeporteRepository;
import io.github.salepartido.api.domain.locales.repository.LocalidadRepository;
import io.github.salepartido.api.domain.locales.repository.UbicacionRepository;
import io.github.salepartido.api.domain.locales.service.LocalService;
import io.github.salepartido.api.domain.locales.service.TurnoService;
import io.github.salepartido.api.domain.participation.model.EstadoEvento;
import io.github.salepartido.api.domain.participation.model.EstadoParticipacion;
import io.github.salepartido.api.domain.participation.model.Evento;
import io.github.salepartido.api.domain.participation.model.HabilidadJugador;
import io.github.salepartido.api.domain.participation.model.NivelDeporte;
import io.github.salepartido.api.domain.participation.model.Participacion;
import io.github.salepartido.api.domain.participation.model.Rol;
import io.github.salepartido.api.domain.participation.model.TipoEvento;
import io.github.salepartido.api.domain.participation.model.Usuario;
import io.github.salepartido.api.domain.participation.repository.EventoRepository;
import io.github.salepartido.api.domain.participation.repository.NivelDeporteRepository;
import io.github.salepartido.api.domain.participation.repository.UsuarioRepository;
import net.datafaker.Faker;

@Service
@Profile("dev")
public class SeedService {

    private final TurnoService turnoService;
    private final LocalService localService;
    private final Faker faker = new Faker(Locale.of("es"));
    private final DeporteRepository deporteRepository;
    private final LocalidadRepository localidadRepository;
    private final UbicacionRepository ubicacionRepository;
    private final NivelDeporteRepository nivelDeporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;

    public SeedService(
            DeporteRepository deporteRepository,
            TurnoService turnoService,
            LocalService localService,
            LocalidadRepository localidadRepository,
            UbicacionRepository ubicacionRepository,
            NivelDeporteRepository nivelDeporteRepository,
            UsuarioRepository usuarioRepository,
            EventoRepository eventoRepository) {
        this.deporteRepository = deporteRepository;
        this.turnoService = turnoService;
        this.localService = localService;
        this.localidadRepository = localidadRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.nivelDeporteRepository = nivelDeporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
    }

    public void generate() {
        List<Local> localesGuardados = guardarLocales(generarLocales(20, 5));

        List<NivelDeporte> niveles = poblarNivelesDeporte();
        List<Usuario> usuarios = poblarUsuarios(10);

        generarTurnosYEventos(localesGuardados, usuarios, niveles);
    }
    private List<Local> guardarLocales(List<Local> locales) {
        List<Local> localesGuardados = new ArrayList<>();
        for (Local local : locales) {
            localesGuardados.add(localService.guardarLocal(local));
        }
        return localesGuardados;
    }

    /* VALORES POSIBLES =============================== */

    private static final Map<String, Integer> CAPACIDAD_POR_DEPORTE = Map.of(
            "Fútbol", 10,
            "Básquet", 10,
            "Tenis", 4,
            "Paddle", 4, // ← sin tilde, sin é
            "Vóley", 12 // ← con tilde en la o
    );

    private static final String[] NOMBRES_LOCALES = {
            "Complejo", "Club", "Arena", "Zona", "Center", "Sports"
    };

    private static final String[] TEMATICAS = {
            "Gol", "Elite", "Norte", "Sur", "Patagonia", "Fútbol", "Punto", "Master"
    };

    private static final String[] TIPOS_CANCHA = {
            "Sintética", "Techada", "Exterior", "Premium"
    };

    private static final String[] DIRECCIONES = {
            "Avda. Roca 1300", "Juan B. Justo 1200", "Calle 123", "9 de Julio 1234", "San Martín 567", "Libertad 890",
            "Av. Córdoba 456", "Belgrano 789", "Mitre 321", "Sarmiento 654", "Av. San Juan 987", "Pueyrredón 432",
            "Av. Santa Fe 876", "Rivadavia 543", "Corrientes 678", "Entre Ríos 345", "Independencia 901",
            "Belgrano 234", "Corrientes 567"
    };

    /* GENERACIÓN =============================== */

    private String generarNombreLocal(Faker faker) {
        return faker.options().option(NOMBRES_LOCALES)
                + " "
                + faker.options().option(TEMATICAS);
    }

    private String generarNombreCancha(Faker faker, int numero) {
        return "Cancha " + numero + " - " + faker.options().option(TIPOS_CANCHA);
    }

    private List<Local> generarLocales(int cantidad, int canchasPorLocal) {
        List<Local> locales = new ArrayList<>();

        List<String> nombresLocalidades = List.of("Puerto Madryn", "Trelew", "Rawson", "Gaiman");
        List<Localidad> localidadesExistentes = localidadRepository.findAll();
        List<Localidad> localidades = new ArrayList<>();

        for (String nombreLoc : nombresLocalidades) {
            Localidad localidad = localidadesExistentes.stream()
                .filter(l -> l.getNombre() != null && l.getNombre().equalsIgnoreCase(nombreLoc))
                .findFirst()
                .orElseGet(() -> {
                    Localidad newLoc = new Localidad();
                    newLoc.setNombre(nombreLoc);
                    return localidadRepository.save(newLoc);
                });
            localidades.add(localidad);
        }

        for (int i = 0; i < cantidad; i++) {
            Local local = new Local();
            local.setNombre(generarNombreLocal(faker));
            local.setTelefono(faker.phoneNumber().phoneNumber());
            local.setDescripcion("Complejo deportivo con excelentes instalaciones para disfrutar del deporte. " + faker.lorem().paragraph(1));

            Localidad localidadElegida = localidades.get(faker.number().numberBetween(0, localidades.size()));

            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setLocalidad(localidadElegida);
            ubicacion.setDireccion(faker.options().option(DIRECCIONES));
            local.setUbicacion(ubicacion);

            local.setCanchas(generarCanchas(canchasPorLocal));
            // local.setDeportes(generarDeportes( deportesPorLocal));
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
            cancha.setCapacidad(CAPACIDAD_POR_DEPORTE.getOrDefault(deporte.getNombre(), 6));

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
                                turno = turnoService.guardarTurno(turno);

                                Usuario organizador = deportistas.get(faker.random().nextInt(deportistas.size()));
                                NivelDeporte nivelRequerido = niveles.isEmpty() ? null
                                        : niveles.get(faker.random().nextInt(niveles.size()));

                                Evento evento = new Evento();
                                String deporteName = cancha.getDeporte() != null ? cancha.getDeporte().getNombre() : "Fútbol";
                                evento.setNombre("Partido de " + deporteName);
                                evento.setTipo(faker.options().option(TipoEvento.values()));
                                evento.setCupoMinimo(2);
                                evento.setCupoMaximo(cancha.getCapacidad());

                                if (fecha.isBefore(hoy)) {
                                    evento.setEstado(EstadoEvento.FINALIZADO);
                                } else {
                                    evento.setEstado(faker.options().option(EstadoEvento.values()));
                                }

                                evento.setNivelRequerido(nivelRequerido);
                                evento.setTurno(turno);
                                evento.setOrganizador(organizador);
                                evento.setParticipaciones(generarParticipaciones(deportistas, organizador));

                                eventoRepository.save(evento);
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

    private static final String[] DEPORTES_PREDETERMINADOS = {
        "Fútbol", "Tenis", "Paddle", "Vóley", "Básquet"
    };

    private List<Deporte> poblarDeportes() {
        List<Deporte> deportes = new ArrayList<>();
        for (String nombre : DEPORTES_PREDETERMINADOS) {
            Deporte deporte = deporteRepository.findByNombre(nombre)
                    .orElseGet(() -> {
                        Deporte nuevo = new Deporte();
                        nuevo.setNombre(nombre);
                        return deporteRepository.save(nuevo);
                    });
            deportes.add(deporte);
        }
        return deportes;
    }

    private Deporte obtenerDeporteAleatorioPersistido() {
        List<Deporte> deportes = deporteRepository.findAll();
        if (deportes.isEmpty()) {
            deportes = poblarDeportes();
        }
        return deportes.get(faker.random().nextInt(deportes.size()));
    }

    private List<Deporte> generarDeportes(int numOfDeportes) {
        List<Deporte> deportes = new ArrayList<>();
        for (int i = 0; i < numOfDeportes; i++) {
            deportes.add(obtenerDeporteAleatorioPersistido());
        }
        return deportes;
    }

    /* PARTICIPATION (E2-H01) =============================== */



    private static final Map<String, String[][]> NIVELES_POR_DEPORTE = Map.of(
        "Paddle", new String[][]{
            {"8va categoría", "Nivel inicial de pádel"},
            {"7ma categoría", "Jugador en formación"},
            {"6ta categoría", "Conoce los fundamentos básicos"},
            {"5ta categoría", "Maneja bien los golpes básicos"},
            {"4ta categoría", "Juego consistente"},
            {"3ra categoría", "Buen nivel competitivo"},
            {"2da categoría", "Nivel avanzado"},
            {"1ra categoría", "Nivel de alto rendimiento"}
        },
        "Fútbol", new String[][]{
            {"Principiante", "Aprendiendo los conceptos básicos del fútbol"},
            {"Intermedio", "Maneja bien la pelota y conoce las posiciones"},
            {"Avanzado", "Nivel competitivo con buena técnica y táctica"}
        },
        "Tenis", new String[][]{
            {"Principiante", "Aprendiendo los golpes básicos del tenis"},
            {"Intermedio", "Juega rallies con consistencia"},
            {"Avanzado", "Nivel competitivo con saque y volea efectivos"}
        },
        "Vóley", new String[][]{
            {"Principiante", "Aprendiendo las técnicas fundamentales"},
            {"Intermedio", "Maneja saque, recepción y armado básico"},
            {"Avanzado", "Nivel competitivo con sistema de juego definido"}
        },
        "Básquet", new String[][]{
            {"Principiante", "Aprendiendo dribling, pase y tiro básico"},
            {"Intermedio", "Maneja bien los fundamentos y conoce la táctica"},
            {"Avanzado", "Nivel competitivo con lectura de juego avanzada"}
        }
    );

    private List<NivelDeporte> poblarNivelesDeporte() {
        List<NivelDeporte> todos = new ArrayList<>();
        List<Deporte> deportes = deporteRepository.findAll();
        if (deportes.isEmpty()) {
            deportes = poblarDeportes();
        }

        for (Deporte deporte : deportes) {
            String[][] definiciones = NIVELES_POR_DEPORTE.get(deporte.getNombre());
            if (definiciones == null) continue;

            List<NivelDeporte> existentes = nivelDeporteRepository.findByDeporteUuidOrderByOrdenAsc(deporte.getUuid());
            if (!existentes.isEmpty()) {
                todos.addAll(existentes);
                continue;
            }

            for (int i = 0; i < definiciones.length; i++) {
                NivelDeporte nivel = new NivelDeporte();
                nivel.setNombre(definiciones[i][0]);
                nivel.setDescripcion(definiciones[i][1]);
                nivel.setOrden(i + 1);
                nivel.setDeporte(deporte);
                todos.add(nivelDeporteRepository.save(nivel));
            }
        }
        return todos;
    }

    private static final String[] NOMBRES_USUARIOS = {
        "Ana García", "Bruno Martínez", "Carolina López", "Diego Fernández",
        "Elena Rodríguez", "Facundo Gómez", "Gabriela Pérez", "Hernán Díaz",
        "Inés Torres", "Javier Morales", "Karen Ruiz", "Lucas Herrera",
        "Martina Castro", "Nicolás Romero", "Paula Flores"
    };

    private List<Usuario> poblarUsuarios(int cantidad) {
        List<Usuario> usuarios = new ArrayList<>();
        int cantidadReal = Math.min(cantidad, NOMBRES_USUARIOS.length);

        for (int i = 0; i < cantidadReal; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre(NOMBRES_USUARIOS[i]);
            // Los últimos 2 son PROPIETARIO, el resto DEPORTISTA
            usuario.setRol(i < cantidadReal - 2 ? Rol.DEPORTISTA : Rol.PROPIETARIO);
            usuarios.add(usuarioRepository.save(usuario));
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
