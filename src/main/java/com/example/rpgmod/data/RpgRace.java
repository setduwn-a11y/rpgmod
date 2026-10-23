package com.example.rpgmod.data;

import java.util.Locale;
import java.util.Optional;

/** Razas jugables con pequeños modificadores pasivos. */
public enum RpgRace {
    HUMANO("Humano", 0, 0, 10),   // +10% XP
    ELFO("Elfo", -2, 5, 0),       // más rápido, menos vida
    ENANO("Enano", 4, -3, 0),     // más vida, más lento
    ORCO("Orco", 6, -2, -5);      // mucha vida, menos XP

    public final String displayName;
    public final int bonusHealth, bonusSpeedPercent, xpPercent;

    RpgRace(String displayName, int bonusHealth, int bonusSpeedPercent, int xpPercent) {
        this.displayName = displayName;
        this.bonusHealth = bonusHealth;
        this.bonusSpeedPercent = bonusSpeedPercent;
        this.xpPercent = xpPercent;
    }

    public String id() { return name().toLowerCase(Locale.ROOT); }

    public static Optional<RpgRace> byId(String id) {
        for (RpgRace r : values()) if (r.id().equals(id)) return Optional.of(r);
        return Optional.empty();
    }
}
