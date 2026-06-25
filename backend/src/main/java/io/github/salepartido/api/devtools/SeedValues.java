package io.github.salepartido.api.devtools;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SeedValues {

    // Constructor privado para evitar instanciación
    private SeedValues() {
        throw new UnsupportedOperationException("Esta es una clase de constantes y no puede ser instanciada.");
    }

    public static final int LOCALES_COUNT = 30;
    public static final int CANCHAS_PER_LOCAL = 5;
    public static final int USUARIOS_COUNT = 100;
    public static final int CAPACIDAD_DEFAULT = 10;

    public static final int EVENTOS_FINALIZADOS_COUNT = 100;
    public static final int EVENTOS_ACTIVOS_COUNT = 50;

    public static final Duration DURACION_TURNO = Duration.ofMinutes(60);
    public static final LocalTime HORA_INICIO_ATENCION = LocalTime.of(12, 0);
    public static final LocalTime HORA_FIN_ATENCION = LocalTime.of(22, 0);

    public static final List<String> LOCALIDADES_LIST = Collections.unmodifiableList(Arrays.asList(
        "Puerto Madryn", "Trelew", "Rawson", "Gaiman"
    ));

    public static final String[] NOMBRES_LOCALES = {
        "Complejo", "Club", "Arena", "Zona", "Center", "Sports"
    };

    public static final String[] TEMATICAS = {
        "Gol", "Elite", "Norte", "Sur", "Patagonia", "Fútbol", "Punto", "Master"
    };

    public static final String[] TIPOS_CANCHA = {
        "Sintética", "Techada", "Exterior", "Premium"
    };

    public static final String[] DIRECCIONES = {
        "Avda. Roca 1300", "Juan B. Justo 1200", "Calle 123", "9 de Julio 1234", "San Martín 567", 
        "Libertad 890", "Av. Córdoba 456", "Belgrano 789", "Mitre 321", "Sarmiento 654", 
        "Av. San Juan 987", "Pueyrredón 432", "Av. Santa Fe 876", "Rivadavia 543", 
        "Corrientes 678", "Entre Ríos 345", "Independencia 901", "Belgrano 234", "Corrientes 567"
    };

    public static final String[] DEPORTES_PREDETERMINADOS = {
        "Fútbol", "Tenis", "Paddle", "Vóley", "Básquet"
    };

    public static final Map<String, Integer> CAPACIDAD_POR_DEPORTE;
    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("Fútbol", 10);
        map.put("Básquet", 10);
        map.put("Tenis", 4);
        map.put("Paddle", 4);
        map.put("Vóley", 12);
        CAPACIDAD_POR_DEPORTE = Collections.unmodifiableMap(map);
    }

    public static final String[] NIVELES_PADDLE = {
        "8va categoría:Nivel inicial de pádel", "7ma categoría:Jugador en formación", 
        "6ta categoría:Conoce los fundamentos básicos", "5ta categoría:Maneja bien los golpes básicos", 
        "4ta categoría:Juego consistente", "3ra categoría:Buen nivel competitivo", 
        "2da categoría:Nivel avanzado", "1ra categoría:Nivel de alto rendimiento"
    };

    public static final String[] NIVELES_FUTBOL = {
        "Principiante:Aprendiendo los conceptos básicos del fútbol", 
        "Intermedio:Maneja bien la pelota y conoce las posiciones", 
        "Avanzado:Nivel competitivo con buena técnica y táctica"
    };

    public static final String[] NIVELES_TENIS = {
        "Principiante:Aprendiendo los golpes básicos del tenis", 
        "Intermedio:Juega rallies con consistencia", 
        "Avanzado:Nivel competitivo con saque y volea efectivos"
    };

    public static final String[] NIVELES_VOLEY = {
        "Principiante:Aprendiendo las técnicas fundamentales", 
        "Intermedio:Maneja recepción y armado básico", 
        "Avanzado:Nivel competitivo con sistema de juego definido"
    };

    public static final String[] NIVELES_BASQUET = {
        "Principiante:Aprendiendo dribling con pase y tiro básico", 
        "Intermedio:Maneja bien los fundamentos y conoce la táctica", 
        "Avanzado:Nivel competitivo con lectura de juego avanzada"
    };
}
