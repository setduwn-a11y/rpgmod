package com.example.rpgmod.client;

import com.example.rpgmod.entity.RpgEntities;
import com.example.rpgmod.entity.RuneSkeletonRenderer;
import com.example.rpgmod.item.RpgItems;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Cosas que solo existen en el cliente (tu ordenador): las teclas, la barra de maná y el arco. */
public final class ClientSetup {
    public static final KeyMapping CAST_KEY = new KeyMapping(
        "key.rpgmod.cast", InputConstants.KEY_R, "key.categories.rpgmod");

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerKeys);
        modBus.addListener(ClientSetup::clientSetup);
        modBus.addListener(ClientSetup::registerRenderers);
        MinecraftForge.EVENT_BUS.addListener(ClientSetup::onKey);
        MinecraftForge.EVENT_BUS.addListener(ManaHud::onRenderGui);
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(CAST_KEY);
    }

    /** Le dice al juego cómo dibujar a los enemigos nuevos. */
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RpgEntities.RUNE_SKELETON.get(), RuneSkeletonRenderer::new);
    }

    /** Le enseña al arco a "tensarse": cambia de dibujo mientras mantienes pulsado el clic derecho. */
    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(RpgItems.ARCO_ARQUERO.get(),
                ResourceLocation.withDefaultNamespace("pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null || entity.getUseItem() != stack) return 0.0F;
                    // el arco se puede mantener tensado hasta 72000 ticks
                    return (72000 - entity.getUseItemRemainingTicks()) / 20.0F;
                });
            ItemProperties.register(RpgItems.ARCO_ARQUERO.get(),
                ResourceLocation.withDefaultNamespace("pulling"),
                (stack, level, entity, seed) ->
                    entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
        });
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
