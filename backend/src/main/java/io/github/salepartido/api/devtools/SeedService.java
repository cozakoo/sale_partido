package io.github.salepartido.api.devtools;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
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
import io.github.salepartido.api.domain.locales.model.Ubicacion;
import io.github.salepartido.api.domain.locales.repository.LocalidadRepository;
import io.github.salepartido.api.domain.locales.repository.UbicacionRepository;
import io.github.salepartido.api.domain.locales.repository.DeporteRepository;
import io.github.salepartido.api.domain.locales.model.Turno;
import io.github.salepartido.api.domain.locales.service.TurnoService;
import io.github.salepartido.api.domain.locales.service.LocalService;
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

    public SeedService(DeporteRepository deporteRepository, TurnoService turnoService, LocalService localService, LocalidadRepository localidadRepository, UbicacionRepository ubicacionRepository) {
        this.deporteRepository = deporteRepository;
        this.turnoService = turnoService;
        this.localService = localService;
        this.localidadRepository = localidadRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    public void generate() {
        List<Local> localesGuardados = guardarLocales(generarLocales(20, 5));

        List<Turno> turnosGenerados = generarTurnos(localesGuardados);
        guardarTurnos(turnosGenerados);
    }

    /* ALMACENAMIENTO =============================== */

    private List<Local> guardarLocales(List<Local> locales) {
        List<Local> localesGuardados = new ArrayList<>();
        for (Local local : locales) {
            localesGuardados.add(localService.guardarLocal(local));
        }
        return localesGuardados;
    }

    private void guardarTurnos(List<Turno> turnos) {
        turnoService.guardarTodos(turnos);
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

    private List<Turno> generarTurnos(List<Local> locales) {
        List<Turno> turnos = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        List<LocalDate> fechas = List.of(hoy.minusDays(1), hoy, hoy.plusDays(1), hoy.plusDays(2));

        String[] deportes = { "Fútbol", "Básquet", "Tenis", "Pádel" };
        String[] organizadores = { "Juan Pérez", "Carlos Gómez", "Martín Rodríguez", "Diego Silva", "María Becerra",
                "Lionel Messi" };
        String[] estados = { "CONFIRMADO", "PENDIENTE", "FINALIZADO" };

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
                                turno.setNombreOrganizador(faker.options().option(organizadores));
                                turno.setDeporte(cancha.getDeporte().getNombre()); // ← el deporte real de la cancha
                                turno.setCantidadParticipantesConfirmados(
                                        faker.number().numberBetween(1, cancha.getCapacidad() + 1));

                                if (fecha.isBefore(hoy)) {
                                    turno.setEstadoEvento("FINALIZADO");
                                } else {
                                    turno.setEstadoEvento(faker.options().option(estados));
                                }

                                turnos.add(turno);
                            }
                        }
                    }
                }
            }
        }
        return turnos;
    }

    private List<Deporte> generarDeportes(int numOfDeportes) {
        List<Deporte> deportes = new ArrayList<>();
        for (int i = 0; i < numOfDeportes; i++) {
            deportes.add(obtenerDeporteAleatorioPersistido());
        }
        return deportes;
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

}