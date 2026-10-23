package com.example.rpgmod.data;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import java.util.Optional;

/** Experiencia, niveles y compra de habilidades. */
public final class RpgProgress {
    public static final int MAX_LEVEL = 30;

    public static int xpNeeded(int level) {
        return 50 + 30 * level;
    }

    public static void addXp(ServerPlayer p, int baseAmount) {
        PlayerRpgData d = RpgDataStore.get(p);
        if (d.level() >= MAX_LEVEL) return;

        int amount = Math.max(1, Math.round(baseAmount * (100 + d.raceEnum().xpPercent) / 100f));
        int level = d.level();
        int xp = d.xp() + amount;
        int points = d.skillPoints();
        boolean leveled = false;

        while (level < MAX_LEVEL && xp >= xpNeeded(level)) {
            xp -= xpNeeded(level);
            level++;
            points++;
            leveled = true;
        }
        if (level >= MAX_LEVEL) xp = 0;

        RpgDataStore.set(p, d.withProgress(level, xp, points));

        if (leveled) {
            p.sendSystemMessage(Component.literal("¡Subiste al nivel " + level
                + "! Tienes " + points + " punto(s) de habilidad. Usa /rpg skills"));
            p.level().playSound(null, p.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS, 1.0f, 1.0f);
        } else {
            p.displayClientMessage(Component.literal("+" + amount + " XP  ("
                + xp + "/" + xpNeeded(level) + ")"), true);
        }
    }

    public static Component learn(ServerPlayer p, String id) {
        Optional<RpgSkill> opt = RpgSkill.byId(id);
        if (opt.isEmpty()) {
            return Component.literal("Esa habilidad no existe. Mira la lista con /rpg skills");
        }
        RpgSkill s = opt.get();
        PlayerRpgData d = RpgDataStore.get(p);

        if (d.hasSkill(s.id)) return Component.literal("Ya tienes " + s.displayName);
        if (s.requiredClass != null && d.rpgClassEnum() != s.requiredClass) {
            return Component.literal(s.displayName + " es solo para la clase " + s.requiredClass.displayName);
        }
        if (s.requires != null && !d.hasSkill(s.requires)) {
            String need = RpgSkill.byId(s.requires).map(x -> x.displayName).orElse(s.requires);
            return Component.literal("Primero necesitas: " + need);
        }
        if (d.skillPoints() < s.cost) {
            return Component.literal("Te faltan puntos: cuesta " + s.cost
                + " y tienes " + d.skillPoints());
        }

        RpgDataStore.set(p, d.withSkill(s.id, s.cost));
        RpgBonuses.apply(p);
        return Component.literal("¡Aprendiste " + s.displayName + "! (" + s.description + ")");
    }

    public static Component respec(ServerPlayer p) {
        PlayerRpgData d = RpgDataStore.get(p);
        int refund = 0;
        for (String id : d.skills()) {
            refund += RpgSkill.byId(id).map(x -> x.cost).orElse(0);
        }
        RpgDataStore.set(p, d.withoutSkills(refund));
        RpgBonuses.apply(p);
        return Component.literal("Habilidades reiniciadas. Recuperaste " + refund + " punto(s).");
    }

    public static Component list(ServerPlayer p) {
        PlayerRpgData d = RpgDataStore.get(p);
        StringBuilder sb = new StringBuilder("=== Árbol de habilidades (puntos: " + d.skillPoints() + ") ===");
        for (RpgSkill s : RpgSkill.values()) {
            if (s.requiredClass != null && s.requiredClass != d.rpgClassEnum()) continue;
            sb.append("\n");
            if (d.hasSkill(s.id)) {
                sb.append("[x] ").append(s.displayName).append(" - ").append(s.description);
            } else {
                sb.append("[ ] ").append(s.id).append(" (").append(s.cost).append(" pt) - ")
                  .append(s.displayName).append(": ").append(s.description);
                if (s.requires != null) {
                    String need = RpgSkill.byId(s.requires).map(x -> x.displayName).orElse(s.requires);
                    sb.append(" [requiere ").append(need).append("]");
                }
            }
        }
        sb.append("\nPara aprender una: /rpg learn <nombre>");
        return Component.literal(sb.toString());
    }

    private RpgProgress() {}
}
