package com.example.rpgmod.skill;

import com.example.rpgmod.data.PlayerRpgData;
import com.example.rpgmod.data.RpgClass;
import com.example.rpgmod.data.RpgDataStore;
import com.example.rpgmod.network.ManaSyncPacket;
import com.example.rpgmod.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Maná de cada jugador. Se recupera solo con el tiempo (1 punto por segundo). */
public final class ManaManager {
    private static final double REGEN_PER_TICK = 1.0 / 20.0;
    // [0] = maná, [1] = último momento calculado
    private static final Map<UUID, double[]> STATE = new HashMap<>();

    public static int max(Player p) {
        PlayerRpgData d = RpgDataStore.get(p);
        int max = 50 + d.rpgClassEnum().baseMana * 5;
        if (d.hasSkill("mente")) max += 20;
        return max;
    }

    public static double regenPerTick(Player p) {
        return RpgDataStore.get(p).hasSkill("meditacion") ? REGEN_PER_TICK * 2 : REGEN_PER_TICK;
    }

    public static double get(Player p) {
        long now = p.level().getGameTime();
        double maxMana = max(p);
        double regen = regenPerTick(p);
        double[] s = STATE.get(p.getUUID());
        if (s == null) {
            s = new double[] { maxMana, now };
            STATE.put(p.getUUID(), s);
        }
        double mana = Math.min(maxMana, s[0] + Math.max(0, now - s[1]) * regen);
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

    /** Envía a tu juego el maná actual para que dibuje la barra. */
    public static void sync(ServerPlayer p) {
        boolean hasClass = RpgDataStore.get(p).rpgClassEnum() != RpgClass.NINGUNA;
        int max = hasClass ? max(p) : 0;
        NetworkHandler.CHANNEL.send(
            new ManaSyncPacket((float) get(p), max, (float) regenPerTick(p)),
            PacketDistributor.PLAYER.with(p));
    }

    private ManaManager() {}
}
