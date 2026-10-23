package com.example.rpgmod.data;

import java.util.Optional;

/** Todas las habilidades del árbol. Se compran con puntos de habilidad. */
public enum RpgSkill {
    // --- Para todas las clases ---
    VITALIDAD("vitalidad", "Vitalidad", "+1 corazón de vida", 1, null, null, 2, 0),
    VITALIDAD_2("vitalidad_2", "Vitalidad II", "+2 corazones más", 2, null, "vitalidad", 4, 0),
    AGILIDAD("agilidad", "Agilidad", "+5% de velocidad", 1, null, null, 0, 5),
    MENTE("mente", "Mente", "+20 de maná máximo", 1, null, null, 0, 0),
    MEDITACION("meditacion", "Meditación", "el maná se recupera el doble de rápido", 2, null, "mente", 0, 0),
    CONCENTRACION("concentracion", "Concentración", "tu habilidad se recarga 25% más rápido", 2, null, "mente", 0, 0),
    // --- Una mejora especial para cada clase ---
    FUEGO_INTENSO("fuego_intenso", "Fuego intenso", "el rayo de fuego hace más daño", 2, RpgClass.MAGO, null, 0, 0),
    FURIA("furia", "Furia", "el grito de guerra dura más", 2, RpgClass.GUERRERO, null, 0, 0),
    VIENTO_VELOZ("viento_veloz", "Viento veloz", "el paso del viento te hace aún más rápido", 2, RpgClass.ARQUERO, null, 0, 0),
    SOMBRA_LARGA("sombra_larga", "Sombra larga", "la invisibilidad dura más", 2, RpgClass.PICARO, null, 0, 0),
    SANACION_MAYOR("sanacion_mayor", "Sanación mayor", "la sanación cura más", 2, RpgClass.CLERIGO, null, 0, 0);

    public final String id;
    public final String displayName;
    public final String description;
    public final int cost;
    /** null = disponible para todas las clases. */
    public final RpgClass requiredClass;
    /** id de la habilidad que hay que tener antes (o null). */
    public final String requires;
    public final int bonusHealth;
    public final int bonusSpeedPercent;

    RpgSkill(String id, String displayName, String description, int cost, RpgClass requiredClass,
             String requires, int bonusHealth, int bonusSpeedPercent) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.cost = cost;
        this.requiredClass = requiredClass;
        this.requires = requires;
        this.bonusHealth = bonusHealth;
        this.bonusSpeedPercent = bonusSpeedPercent;
    }

    public static Optional<RpgSkill> byId(String id) {
        for (RpgSkill s : values()) if (s.id.equals(id)) return Optional.of(s);
        return Optional.empty();
    }
}
