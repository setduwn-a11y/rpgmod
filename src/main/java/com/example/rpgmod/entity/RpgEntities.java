package com.example.rpgmod.entity;

import com.example.rpgmod.RpgMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Todos los enemigos nuevos del mod. */
public final class RpgEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RpgMod.MOD_ID);

    public static final RegistryObject<EntityType<RuneSkeleton>> RUNE_SKELETON = ENTITIES.register(
        "esqueleto_runico",
        () -> EntityType.Builder.of(RuneSkeleton::new, MobCategory.MONSTER)
            .sized(0.6F, 1.99F)
            .clientTrackingRange(8)
            .build("esqueleto_runico"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        bus.addListener(RpgEntities::onAttributes);
        bus.addListener(RpgEntities::onSpawnPlacements);
    }

    /** Vida, daño y velocidad del enemigo. */
    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(RUNE_SKELETON.get(), RuneSkeleton.createAttributes().build());
    }

    /** Dónde puede aparecer: en el suelo y con poca luz, como los zombis. */
    private static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(RUNE_SKELETON.get(), SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
            SpawnPlacementRegisterEvent.Operation.OR);
    }

    private RpgEntities() {}
}
