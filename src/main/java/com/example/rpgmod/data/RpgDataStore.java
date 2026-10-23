package com.example.rpgmod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

/**
 * Lectura/escritura de los datos RPG en el NBT persistente del jugador.
 * Forge guarda getPersistentData() dentro del archivo del jugador.
 */
public final class RpgDataStore {
    private static final String KEY = "rpgmod_data";

    public static PlayerRpgData get(Player player) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(KEY, Tag.TAG_COMPOUND)) return PlayerRpgData.DEFAULT;
        return PlayerRpgData.CODEC.parse(NbtOps.INSTANCE, root.get(KEY))
            .result().orElse(PlayerRpgData.DEFAULT);
    }

    public static void set(Player player, PlayerRpgData data) {
        PlayerRpgData.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .result().ifPresent(tag -> player.getPersistentData().put(KEY, tag));
    }

    private RpgDataStore() {}
}
