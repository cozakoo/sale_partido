package io.github.salepartido.api.devtools.canchas;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.github.salepartido.api.domain.locales.model.Cancha;
import io.github.salepartido.api.domain.locales.model.ConfiguracionDia;
import io.github.salepartido.api.domain.locales.model.ConfiguracionHorario;
import io.github.salepartido.api.domain.locales.model.Deporte;
import io.github.salepartido.api.domain.locales.model.HorarioAtencion;
import io.github.salepartido.api.domain.locales.model.Local;

import io.github.salepartido.api.domain.locales.repository.DeporteRepository;
import net.datafaker.Faker;

@Service
@Profile("dev")
public class SeedService {

    private final Faker faker = new Faker(Locale.of("es"));
    private final DeporteRepository deporteRepository;

    public SeedService(DeporteRepository deporteRepository) {
        this.deporteRepository = deporteRepository;
    }

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
        "Avda. Roca 1300", "Juan B. Justo 1200", "Calle 123", "9 de Julio 1234", "San Martín 567", "Libertad 890","Av. Córdoba 456", "Belgrano 789", "Mitre 321", "Sarmiento 654","Av. San Juan 987", "Pueyrredón 432", "Av. Santa Fe 876", "Rivadavia 543", "Corrientes 678", "Entre Ríos 345", "Independencia 901", "Belgrano 234", "Corrientes 567"
    };

    public String generarNombreLocal(Faker faker) {
        return faker.options().option(NOMBRES_LOCALES)
                + " "
                + faker.options().option(TEMATICAS);
    }

    public String generarNombreCancha(Faker faker, int numero) {
        return "Cancha " + numero + " - " + faker.options().option(TIPOS_CANCHA);
    }

    public List<Local> generarLocales(int cantidad, int canchasPorLocal) { 
        List<Local> locales = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Local local = new Local();
            local.setNombre(generarNombreLocal(faker));
            local.setDireccion(faker.options().option(DIRECCIONES));
            local.setCanchas(generarCanchas(canchasPorLocal));
            //local.setDeportes(generarDeportes( deportesPorLocal));
            local.setHorariosAtencion( generarHorariosAtencionSemanal());
            locales.add(local);
        }
        return locales;
    }

    public List<Cancha> generarCanchas(int count) {
        List<Cancha> canchas = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Cancha cancha = new Cancha();
            cancha.setNombre(generarNombreCancha(faker, i+1));
            cancha.setConfiguracionesHorarios(List.of(generarConfiguracionHorario()));
            cancha.setDeporte(obtenerDeporteAleatorioPersistido()); // Asignamos un deporte aleatorio a cada cancha
            canchas.add(cancha);
        }
        return canchas;
    }

    public ConfiguracionHorario generarConfiguracionHorario() {
        ConfiguracionHorario horario = new ConfiguracionHorario();
        horario.setActivo(true);
        horario.setDuracionTurno(
                faker.options().option(
                    Duration.ofMinutes(30),
                    Duration.ofMinutes(60)
                )
        );
        horario.setConfiguracionesDias(generarConfiguracionesDias(DayOfWeek.values()));
        return horario;
    }

    public List<ConfiguracionDia> generarConfiguracionesDias(DayOfWeek[] diasSemana) {
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

    public List<Deporte> generarDeportes(int numOfDeportes) {
        List<Deporte> deportes = new ArrayList<>();
        for (int i = 0; i < numOfDeportes; i++) {
            deportes.add(obtenerDeporteAleatorioPersistido());
        }
        return deportes;
    }

    private Deporte obtenerDeporteAleatorioPersistido() {
        String nombre = faker.options().option("Fútbol", "Tenis", "Paddle", "Vóley", "Básquet");
        return deporteRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Deporte deporte = new Deporte();
                    deporte.setNombre(nombre);
                    return deporteRepository.save(deporte);
                });
    }

    public List<HorarioAtencion> generarHorariosAtencionSemanal() {
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
    
}