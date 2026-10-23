package com.example.rpgmod.network;

import com.example.rpgmod.client.ManaHud;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** Mensaje del servidor a tu juego con tu maná actual, para dibujar la barra. */
public record ManaSyncPacket(float mana, int max, float regenPerTick) {

    public static void encode(ManaSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.mana);
        buf.writeInt(msg.max);
        buf.writeFloat(msg.regenPerTick);
    }

    public static ManaSyncPacket decode(FriendlyByteBuf buf) {
        return new ManaSyncPacket(buf.readFloat(), buf.readInt(), buf.readFloat());
    }

    public static void handle(ManaSyncPacket msg, CustomPayloadEvent.Context ctx) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ManaHud.update(msg.mana, msg.max, msg.regenPerTick);
        }
    }
}
