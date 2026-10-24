package com.example.rpgmod.item;

import com.example.rpgmod.skill.SkillManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Bastón arcano: con clic derecho lanza la habilidad de tu clase (igual que la tecla R). */
public class StaffItem extends Item {

    public StaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.displayClientMessage(SkillManager.cast(sp), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
