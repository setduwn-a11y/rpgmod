package com.example.rpgmod.skill;

import com.example.rpgmod.data.PlayerRpgData;
import com.example.rpgmod.data.RpgClass;
import com.example.rpgmod.data.RpgDataStore;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Habilidad activa de cada clase. Se lanza con la tecla R o con /rpg cast. */
public final class SkillManager {
    private static final Map<UUID, Long> READY_AT = new HashMap<>();

    public static Component cast(ServerPlayer p) {
        PlayerRpgData d = RpgDataStore.get(p);
        RpgClass c = d.rpgClassEnum();
        if (c == RpgClass.NINGUNA) {
            return Component.literal("Primero elige una clase con /rpg class");
        }

        long now = p.level().getGameTime();
        long ready = READY_AT.getOrDefault(p.getUUID(), 0L);
        if (now < ready) {
            long secs = (ready - now + 19) / 20;
            return Component.literal("Habilidad en espera: " + secs + " s");
        }

        int cost = (c == RpgClass.MAGO) ? 20 : 25;
        if (!ManaManager.spend(p, cost)) {
            return Component.literal("No tienes suficiente maná (necesitas " + cost + ")");
        }

        String name;
        int cooldownSeconds;
        switch (c) {
            case MAGO -> {
                fireRay(p, d.hasSkill("fuego_intenso") ? 10.0f : 6.0f);
                name = "Rayo de fuego"; cooldownSeconds = 3;
            }
            case GUERRERO -> {
                int ticks = d.hasSkill("furia") ? 300 : 200;
                p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, ticks, 1));
                name = "Grito de guerra"; cooldownSeconds = 20;
            }
            case ARQUERO -> {
                int level = d.hasSkill("viento_veloz") ? 2 : 1;
                p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 160, level));
                name = "Paso del viento"; cooldownSeconds = 15;
            }
            case PICARO -> {
                int ticks = d.hasSkill("sombra_larga") ? 240 : 160;
                p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, ticks, 0));
                name = "Sombra"; cooldownSeconds = 25;
            }
            default -> { // CLERIGO
                p.heal(d.hasSkill("sanacion_mayor") ? 12.0f : 8.0f);
                ((ServerLevel) p.level()).sendParticles(ParticleTypes.HEART,
                    p.getX(), p.getY() + 1.5, p.getZ(), 8, 0.4, 0.4, 0.4, 0.0);
                name = "Sanación"; cooldownSeconds = 15;
            }
        }
        ManaManager.sync(p);
        double factor = d.hasSkill("concentracion") ? 0.75 : 1.0;
        READY_AT.put(p.getUUID(), now + Math.round(cooldownSeconds * 20L * factor));
        return Component.literal(name);
    }

    private static void fireRay(ServerPlayer p, float damage) {
        ServerLevel level = (ServerLevel) p.level();
        Vec3 start = p.getEyePosition();
        Vec3 dir = p.getLookAngle();
        double range = 15.0;
        Vec3 end = start.add(dir.scale(range));

        Entity target = null;
        double best = range;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                p.getBoundingBox().expandTowards(dir.scale(range)).inflate(1.0),
                x -> x != p && x.isAlive())) {
            Optional<Vec3> hit = e.getBoundingBox().inflate(0.3).clip(start, end);
            if (hit.isPresent()) {
                double dist = start.distanceTo(hit.get());
                if (dist < best) { best = dist; target = e; }
            }
        }

        for (double d = 1.0; d <= best; d += 0.5) {
            Vec3 pt = start.add(dir.scale(d));
            level.sendParticles(ParticleTypes.FLAME, pt.x, pt.y, pt.z, 2, 0.05, 0.05, 0.05, 0.01);
        }
        if (target != null) {
            // "indirectMagic" guarda quién disparó, para que cuenten las muertes (XP)
            target.hurt(p.damageSources().indirectMagic(p, p), damage);
            target.setRemainingFireTicks(100);
        }
        level.playSound(null, p.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    private SkillManager() {}
}
