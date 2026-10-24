package com.example.rpgmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;

/**
 * Barra de maná en la esquina superior izquierda.
 * (En Forge 1.21 ya no existe RenderGuiEvent; usamos el aviso que Forge da
 * cada vez que se dibuja el chat, que ocurre en cada imagen del juego.)
 *
 * Detalles visuales:
 *  - el relleno se desliza suavemente al subir o bajar
 *  - al gastar maná queda una estela clara que se va encogiendo
 *  - degradado azul con brillo, rayitas cada 10% y un destello que recorre la barra
 *  - con poco maná la barra palpita en rojo
 */
public final class ManaHud {
    private static float mana;
    private static int max;
    private static float regenPerTick;
    private static long syncedAt;

    // Estado de la animación
    private static boolean init;
    private static double shown;   // valor que se ve en la barra (suavizado)
    private static double trail;   // estela clara al gastar
    private static long lastNanos;

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
        if (max <= 0 || mc.player == null || mc.level == null || mc.options.hideGui) {
            init = false;
            return;
        }

        // --- Valores y animación ---
        long nowNanos = System.nanoTime();
        double dt = init ? Math.min(0.1, (nowNanos - lastNanos) / 1.0e9) : 0.0;
        lastNanos = nowNanos;

        long elapsed = Math.max(0, mc.level.getGameTime() - syncedAt);
        double current = Math.min(max, mana + elapsed * regenPerTick);

        if (!init) {
            shown = current;
            trail = current;
            init = true;
        }
        shown += (current - shown) * Math.min(1.0, dt * 10.0);
        if (shown > trail) trail = shown;
        else trail = Math.max(shown, trail - max * 0.35 * dt);

        double ratio = Math.max(0.0, Math.min(1.0, shown / max));
        long ms = System.currentTimeMillis();

        // --- Dibujo ---
        GuiGraphics g = event.getGuiGraphics();
        final int x = 10, y = 10, w = 110, h = 10;
        int fillW = (int) (w * ratio);
        int trailW = (int) (w * Math.max(0.0, Math.min(1.0, trail / max)));

        // Marco y fondo
        g.fill(x - 2, y - 2, x + w + 2, y + h + 2, 0xFF2B4A8C);
        g.fillGradient(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF1B2335, 0xFF0B0F1A);

        // Estela clara al gastar
        if (trailW > fillW) {
            g.fill(x + fillW, y, x + trailW, y + h, 0xCCCFE6FF);
        }

        // Relleno con degradado
        if (fillW > 0) {
            g.fillGradient(x, y, x + fillW, y + h, 0xFF7FD0FF, 0xFF1F4FD8);
            g.fill(x, y, x + fillW, y + 1, 0x77FFFFFF);          // brillo superior

            // Destello que recorre la barra
            double sweep = (ms % 2200L) / 2200.0;
            int sx = x + (int) (sweep * (w + 14)) - 14;
            int x1 = Math.max(x, sx);
            int x2 = Math.min(x + fillW, sx + 8);
            if (x2 > x1) g.fill(x1, y, x2, y + h, 0x33FFFFFF);

            // Palpita en rojo con poco maná
            if (ratio < 0.2) {
                int alpha = (int) (50 + 50 * Math.sin(ms / 140.0));
                g.fill(x, y, x + fillW, y + h, (alpha << 24) | 0xFF2020);
            }
        }

        // Rayitas cada 10%
        for (int i = 1; i < 10; i++) {
            int px = x + (w * i) / 10;
            g.fill(px, y, px + 1, y + h, 0x55000000);
        }

        // Texto
        String text = (int) current + " / " + max;
        int color = ratio < 0.2 ? 0xFFFF8080 : 0xFFBFE3FF;
        g.drawString(mc.font, "Maná  " + text, x + w + 8, y + 1, color);
    }

    private ManaHud() {}
}
