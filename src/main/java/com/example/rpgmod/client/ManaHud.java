package com.example.rpgmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;

/**
 * Dibuja la barra de maná en la esquina superior izquierda de la pantalla.
 * (En Forge 1.21 ya no existe RenderGuiEvent; usamos el aviso que Forge da
 * cada vez que se dibuja el chat, que ocurre en cada imagen del juego.)
 */
public final class ManaHud {
    private static float mana;
    private static int max;
    private static float regenPerTick;
    private static long syncedAt;

    /** Lo llama el mensaje del servidor cuando cambia tu maná. */
    public static void update(float newMana, int newMax, float newRegen) {
        Minecraft mc = Minecraft.getInstance();
        mana = newMana;
        max = newMax;
        regenPerTick = newRegen;
        syncedAt = mc.level != null ? mc.level.getGameTime() : 0;
    }

    public static void onRenderGui(CustomizeGuiOverlayEvent.Chat event) {
        Minecraft mc = Minecraft.getInstance();
        if (max <= 0 || mc.player == null || mc.level == null || mc.options.hideGui) return;

        long elapsed = Math.max(0, mc.level.getGameTime() - syncedAt);
        double current = Math.min(max, mana + elapsed * regenPerTick);

        GuiGraphics g = event.getGuiGraphics();
        int x = 10, y = 10, w = 100, h = 9;
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xAA000000);          // marco
        int filled = (int) (w * current / max);
        g.fill(x, y, x + filled, y + h, 0xFF3A7BFF);                       // relleno azul
        g.drawString(mc.font, "Maná " + (int) current + "/" + max, x + w + 6, y + 1, 0xFFFFFF);
    }

    private ManaHud() {}
}
