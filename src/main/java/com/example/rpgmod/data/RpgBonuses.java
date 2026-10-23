package com.example.rpgmod.data;

import com.example.rpgmod.RpgMod;
import com.example.rpgmod.skill.ManaManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Aplica al jugador los bonos de vida y velocidad de su clase, raza y habilidades. */
public final class RpgBonuses {
    private static final ResourceLocation HEALTH_ID =
        ResourceLocation.fromNamespaceAndPath(RpgMod.MOD_ID, "bonus_health");
    private static final ResourceLocation SPEED_ID =
        ResourceLocation.fromNamespaceAndPath(RpgMod.MOD_ID, "bonus_speed");

    public static void apply(ServerPlayer p) {
        PlayerRpgData d = RpgDataStore.get(p);
        RpgClass c = d.rpgClassEnum();
        RpgRace r = d.raceEnum();

        int health = c.bonusHealth + r.bonusHealth;
        int speed = c.bonusSpeedPercent + r.bonusSpeedPercent;
        for (RpgSkill s : RpgSkill.values()) {
            if (d.hasSkill(s.id)) {
                health += s.bonusHealth;
                speed += s.bonusSpeedPercent;
            }
        }

        set(p.getAttribute(Attributes.MAX_HEALTH), HEALTH_ID, health,
            AttributeModifier.Operation.ADD_VALUE);
        set(p.getAttribute(Attributes.MOVEMENT_SPEED), SPEED_ID, speed / 100.0,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
        ManaManager.sync(p);
    }

    private static void set(AttributeInstance inst, ResourceLocation id, double amount,
                            AttributeModifier.Operation op) {
        if (inst == null) return;
        inst.removeModifier(id);
        if (amount != 0) inst.addTransientModifier(new AttributeModifier(id, amount, op));
    }

    private RpgBonuses() {}
}
