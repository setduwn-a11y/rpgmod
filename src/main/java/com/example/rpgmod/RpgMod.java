package com.example.rpgmod;

import com.example.rpgmod.client.ClientSetup;
import com.example.rpgmod.command.RpgCommands;
import com.example.rpgmod.data.RpgBonuses;
import com.example.rpgmod.data.RpgDataStore;
import com.example.rpgmod.data.RpgProgress;
import com.example.rpgmod.item.RpgItems;
import com.example.rpgmod.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RpgMod.MOD_ID)
public class RpgMod {
    public static final String MOD_ID = "rpgmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public RpgMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        RpgItems.register(modBus);
        NetworkHandler.init();
        MinecraftForge.EVENT_BUS.addListener(RpgCommands::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onLogin);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onRespawn);
        MinecraftForge.EVENT_BUS.addListener(RpgMod::onDeath);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init(modBus);
        }
        LOGGER.info("RPG Mod (Forge) cargado: fase 6 (armas y armaduras)");
    }

    /** Conserva clase, raza, nivel y habilidades al morir. */
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

    /** Cuando un jugador mata a un mob, gana experiencia según la vida del mob. */
    private static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) return;
        if (event.getSource().getEntity() instanceof ServerPlayer sp) {
            int xp = Math.max(1, (int) (event.getEntity().getMaxHealth() / 2));
            RpgProgress.addXp(sp, xp);
        }
    }
}
