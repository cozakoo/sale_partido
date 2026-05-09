package io.github.salepartido.api.devtools.canchas;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import net.datafaker.Faker;

import io.github.salepartido.api.domain.canchas.model.ConfiguracionDia;
import io.github.salepartido.api.domain.canchas.model.ConfiguracionHorario;

@Service
@Profile("dev")
public class SeedService {

    /* 
    private final ConfiguracionDiaRepository repo;
    public SeedService(ConfiguracionDiaRepository repo) {
        this.repo = repo;
    }
    */

    public Collection<ConfiguracionDia> seed(int count) {

        Collection<ConfiguracionDia> configuracionesDias = new ArrayList<>();

        Faker faker = new Faker(Locale.of("es"));

        for (int i = 0; i < count; i++) {

            ConfiguracionHorario horario = new ConfiguracionHorario();
            horario.setCodigo(String.valueOf(i));
            horario.setActivo(true);
            horario.setDuracionTurno(
                    faker.options().option(
                        Duration.ofMinutes(30),
                        Duration.ofMinutes(60),
                        Duration.ofMinutes(90),
                        Duration.ofMinutes(120)
                    )
            );

            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {

                int horaInicio = faker.number().numberBetween(8, 18);

                // duración del horario total
                int horasDisponibles = faker.number().numberBetween(2, 6);

                LocalTime inicio = LocalTime.of(horaInicio, 0);
                LocalTime fin = inicio.plusHours(horasDisponibles);

                ConfiguracionDia dia = new ConfiguracionDia();

                dia.setDiaSemana(dayOfWeek);
                dia.setHoraInicio(inicio);
                dia.setHoraFin(fin);
                dia.setConfiguracionHorario(horario);

                configuracionesDias.add(dia);
            }
        }

        return configuracionesDias;
    }
}