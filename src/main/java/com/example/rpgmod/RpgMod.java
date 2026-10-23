package com.example.rpgmod;

import com.example.rpgmod.command.RpgCommands;
import com.example.rpgmod.data.RpgBonuses;
import com.example.rpgmod.data.RpgDataStore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RpgMod.MOD_ID)
public class RpgMod {
    public static final String MOD_ID = "rpgmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RpgMod() {
        MinecraftForge.EVENT_BUS.addListener(RpgCommands::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onLogin);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onRespawn);
        LOGGER.info("RPG Mod (Forge) cargado: fase 2 (bonos, maná y habilidades)");
    }

    /** Conserva clase, raza y nivel al morir. */
    private static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            RpgDataStore.set(event.getEntity(), RpgDataStore.get(event.getOriginal()));
        }
    }

    private static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) RpgBonuses.apply(sp);
    }

    private static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) RpgBonuses.apply(sp);
    }
}
