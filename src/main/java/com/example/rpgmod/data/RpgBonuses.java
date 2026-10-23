package com.example.rpgmod.data;

import com.example.rpgmod.RpgMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Aplica al jugador los bonos de vida y velocidad de su clase y su raza. */
public final class RpgBonuses {
    private static final ResourceLocation HEALTH_ID =
        ResourceLocation.fromNamespaceAndPath(RpgMod.MOD_ID, "bonus_health");
    private static final ResourceLocation SPEED_ID =
        ResourceLocation.fromNamespaceAndPath(RpgMod.MOD_ID, "bonus_speed");

    public static void apply(ServerPlayer p) {
        PlayerRpgData d = RpgDataStore.get(p);
        RpgClass c = d.rpgClassEnum();
        RpgRace r = d.raceEnum();

        double extraHealth = c.bonusHealth + r.bonusHealth;
        double extraSpeed = (c.bonusSpeedPercent + r.bonusSpeedPercent) / 100.0;

        set(p.getAttribute(Attributes.MAX_HEALTH), HEALTH_ID, extraHealth,
            AttributeModifier.Operation.ADD_VALUE);
        set(p.getAttribute(Attributes.MOVEMENT_SPEED), SPEED_ID, extraSpeed,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
    }

    private static void set(AttributeInstance inst, ResourceLocation id, double amount,
                            AttributeModifier.Operation op) {
        if (inst == null) return;
        inst.removeModifier(id);
        if (amount != 0) inst.addTransientModifier(new AttributeModifier(id, amount, op));
    }

    private RpgBonuses() {}
}
