package com.example.rpgmod.data;

import java.util.Locale;
import java.util.Optional;

/** Clases jugables. Cada una tendrá armas y armaduras propias en la fase 3. */
public enum RpgClass {
    NINGUNA("Sin clase", 0, 0, 0),
    GUERRERO("Guerrero", 6, 2, 0),
    MAGO("Mago", -4, 0, 10),
    ARQUERO("Arquero", 0, 1, 2),
    PICARO("Pícaro", -2, 3, 1),
    CLERIGO("Clérigo", 2, 0, 6);

    public final String displayName;
    /** Bonos base: vida extra, velocidad (en %), maná máximo. */
    public final int bonusHealth, bonusSpeedPercent, baseMana;

    RpgClass(String displayName, int bonusHealth, int bonusSpeedPercent, int baseMana) {
        this.displayName = displayName;
        this.bonusHealth = bonusHealth;
        this.bonusSpeedPercent = bonusSpeedPercent;
        this.baseMana = baseMana;
    }

    public String id() { return name().toLowerCase(Locale.ROOT); }

    public static Optional<RpgClass> byId(String id) {
        for (RpgClass c : values()) if (c.id().equals(id)) return Optional.of(c);
        return Optional.empty();
    }
}
