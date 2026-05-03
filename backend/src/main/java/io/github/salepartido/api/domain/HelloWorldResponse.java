package io.github.salepartido.api.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HelloWorldResponse {

    private UUID uuid;
    private String text;
    private String source; // Indicates if the data came from "DB" or "CACHE"

}
