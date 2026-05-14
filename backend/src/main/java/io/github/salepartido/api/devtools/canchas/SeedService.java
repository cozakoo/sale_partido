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
import io.github.salepartido.api.domain.locales.model.Local;
import net.datafaker.Faker;

@Service
@Profile("dev")
public class SeedService {

    public List<Local> generarLocales(int cantidad, int canchasPorLocal) { 
        
        List<Local> locales = new ArrayList<>();

        Faker faker = new Faker(Locale.of("es"));

        for (int i = 0; i < cantidad; i++) {
            Local local = new Local();
            local.setNombre(faker.name().fullName());
            local.setCanchas(generarCanchas(canchasPorLocal));
            locales.add(local);
        }

        return locales;

    }

    public List<Cancha> generarCanchas(int count) {
        
        List<Cancha> canchas = new ArrayList<>();

        Faker faker = new Faker(Locale.of("es"));

        for (int i = 0; i < count; i++) {
            Cancha cancha = new Cancha();
            cancha.setNombre(faker.name().fullName());
            cancha.setConfiguracionesHorarios(List.of(generarConfiguracionHorario()));
            canchas.add(cancha);
        }

        return canchas;
    }

    public ConfiguracionHorario generarConfiguracionHorario() {

        Faker faker = new Faker(Locale.of("es"));

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

        Faker faker = new Faker(Locale.of("es"));

        for (DayOfWeek dayOfWeek : diasSemana) {

            int horaInicio = faker.number().numberBetween(8, 22);

            int horasDisponibles = faker.number().numberBetween(3, 14);

            LocalTime inicio = LocalTime.of(horaInicio, 0);
            LocalTime fin = inicio.plusHours(horasDisponibles);

            ConfiguracionDia dia = new ConfiguracionDia();

            dia.setDiaSemana(dayOfWeek);
            dia.setHoraInicio(inicio);
            dia.setHoraFin(fin);

            configuracionesDias.add(dia);
        }

        return configuracionesDias;


    }


}