package io.github.salepartido.api.devtools.canchas;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;

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

    public Collection<ConfiguracionDia> seed() {

        Collection<ConfiguracionDia> configuracionesDias = new ArrayList<>();

        Faker faker = new Faker();
        for (int i = 0; i < 20; i++){

            ConfiguracionHorario horario = new ConfiguracionHorario();
            horario.setCodigo(null);
            horario.setActivo(true);
            horario.setDuracionTurno(null);
            horario.setCancha(null);

            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                ConfiguracionDia dia = new ConfiguracionDia();
                dia.setDiaSemana(dayOfWeek);
                dia.setHoraInicio(null);
                dia.setHoraFin(null);
                dia.setConfiguracionHorario(null);
                dia.setConfiguracionHorario(horario);

                // hacer save en vez de return
                configuracionesDias.add(dia);
            }
        }
        
        return configuracionesDias;
    }
}