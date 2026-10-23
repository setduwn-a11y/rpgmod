package com.example.rpgmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

/** Cosas que solo existen en el cliente (tu ordenador): las teclas. */
public final class ClientSetup {
    public static final KeyMapping CAST_KEY = new KeyMapping(
        "key.rpgmod.cast", InputConstants.KEY_R, "key.categories.rpgmod");

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerKeys);
        MinecraftForge.EVENT_BUS.addListener(ClientSetup::onKey);
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(CAST_KEY);
    }

    /** Al pulsar la tecla, el juego ejecuta el comando /rpg cast por ti. */
    private static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        while (CAST_KEY.consumeClick()) {
            mc.player.connection.sendCommand("rpg cast");
        }
    }

    private ClientSetup() {}
}
