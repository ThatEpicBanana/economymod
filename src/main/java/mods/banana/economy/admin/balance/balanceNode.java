package mods.banana.economy.admin.balance;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class balanceNode {
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> mainNode = CommandManager
                .literal("balance")
                .build();

        LiteralCommandNode<ServerCommandSource> addNode = CommandManager
                .literal("add")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .then(
                                        CommandManager.argument("amount", LongArgumentType.longArg())
                                                .executes(commandContext -> add.execute(commandContext, EntityArgumentType.getPlayer(commandContext, "player"), LongArgumentType.getLong(commandContext, "amount")))
                                )
                )
                .build();

        LiteralCommandNode<ServerCommandSource> setNode = CommandManager
                .literal("set")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .then(
                                        CommandManager.argument("amount", LongArgumentType.longArg())
                                                .executes(commandContext -> set.execute(commandContext, EntityArgumentType.getPlayer(commandContext, "player"), LongArgumentType.getLong(commandContext, "amount")))
                                )
                )
                .build();

        LiteralCommandNode<ServerCommandSource> resetNode = CommandManager
                .literal("reset")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .executes(commandContext -> reset.execute(commandContext, EntityArgumentType.getPlayer(commandContext, "player")))
                )
                .build();

        mainNode.addChild(addNode);
        mainNode.addChild(setNode);
        mainNode.addChild(resetNode);
        return mainNode;
    }
}
