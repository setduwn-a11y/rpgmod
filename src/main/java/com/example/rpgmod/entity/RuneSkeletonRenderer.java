package com.example.rpgmod.entity;

import com.example.rpgmod.RpgMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Dibuja al Esqueleto Rúnico con el cuerpo de un humanoide y su propio dibujo. */
public class RuneSkeletonRenderer extends HumanoidMobRenderer<RuneSkeleton, HumanoidModel<RuneSkeleton>> {
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RpgMod.MOD_ID, "textures/entity/esqueleto_runico.png");

    public RuneSkeletonRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(RuneSkeleton entity) {
        return TEXTURE;
    }
}
