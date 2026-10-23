package com.example.rpgmod;

import com.example.rpgmod.command.RpgCommands;
import com.example.rpgmod.data.RpgDataStore;
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
        LOGGER.info("RPG Mod (Forge) cargado: fase 1 (clases y razas)");
    }

    /** Conserva clase, raza y nivel al morir. */
    private static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            RpgDataStore.set(event.getEntity(), RpgDataStore.get(event.getOriginal()));
        }
    }
}
