package com.example.rpgmod.skill;

import com.example.rpgmod.data.RpgDataStore;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Maná de cada jugador. Se recupera solo con el tiempo (1 punto por segundo). */
public final class ManaManager {
    private static final double REGEN_PER_TICK = 1.0 / 20.0;
    // [0] = maná, [1] = último momento calculado
    private static final Map<UUID, double[]> STATE = new HashMap<>();

    public static int max(Player p) {
        return 50 + RpgDataStore.get(p).rpgClassEnum().baseMana * 5;
    }

    public static double get(Player p) {
        long now = p.level().getGameTime();
        double maxMana = max(p);
        double[] s = STATE.get(p.getUUID());
        if (s == null) {
            s = new double[] { maxMana, now };
            STATE.put(p.getUUID(), s);
        }
        double mana = Math.min(maxMana, s[0] + Math.max(0, now - s[1]) * REGEN_PER_TICK);
        s[0] = mana;
        s[1] = now;
        return mana;
    }

    public static boolean spend(Player p, double cost) {
        double mana = get(p);
        if (mana < cost) return false;
        STATE.get(p.getUUID())[0] = mana - cost;
        return true;
    }

    private ManaManager() {}
}
