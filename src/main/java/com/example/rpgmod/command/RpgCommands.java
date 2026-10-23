package com.example.rpgmod.command;

import com.example.rpgmod.data.PlayerRpgData;
import com.example.rpgmod.data.RpgClass;
import com.example.rpgmod.data.RpgDataStore;
import com.example.rpgmod.data.RpgRace;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import java.util.Arrays;

/** /rpg info | /rpg class <id> | /rpg race <id>  (temporal, hasta tener la GUI) */
public final class RpgCommands {

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rpg")
            .then(Commands.literal("info").executes(ctx -> {
                ServerPlayer p = ctx.getSource().getPlayerOrException();
                PlayerRpgData d = RpgDataStore.get(p);
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "Raza: " + d.raceEnum().displayName
                    + " | Clase: " + d.rpgClassEnum().displayName
                    + " | Nivel: " + d.level()
                    + " | Puntos de habilidad: " + d.skillPoints()), false);
                return 1;
            }))
            .then(Commands.literal("class")
                .then(Commands.argument("id", StringArgumentType.word())
                    .suggests((c, b) -> {
                        Arrays.stream(RpgClass.values()).forEach(x -> b.suggest(x.id()));
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        String id = StringArgumentType.getString(ctx, "id");
                        return RpgClass.byId(id).map(c -> {
                            RpgDataStore.set(p, RpgDataStore.get(p).withClass(c));
                            ctx.getSource().sendSuccess(
                                () -> Component.literal("Ahora eres: " + c.displayName), false);
                            return 1;
                        }).orElseGet(() -> {
                            ctx.getSource().sendFailure(Component.literal("Clase no válida"));
                            return 0;
                        });
                    })))
            .then(Commands.literal("race")
                .then(Commands.argument("id", StringArgumentType.word())
                    .suggests((c, b) -> {
                        Arrays.stream(RpgRace.values()).forEach(x -> b.suggest(x.id()));
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        ServerPlayer p = ctx.getSource().getPlayerOrException();
                        String id = StringArgumentType.getString(ctx, "id");
                        return RpgRace.byId(id).map(r -> {
                            RpgDataStore.set(p, RpgDataStore.get(p).withRace(r));
                            ctx.getSource().sendSuccess(
                                () -> Component.literal("Tu raza ahora es: " + r.displayName), false);
                            return 1;
                        }).orElseGet(() -> {
                            ctx.getSource().sendFailure(Component.literal("Raza no válida"));
                            return 0;
                        });
                    }))));
    }

    private RpgCommands() {}
}
