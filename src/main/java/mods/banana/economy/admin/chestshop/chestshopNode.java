package mods.banana.economy.admin.chestshop;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class chestshopNode {
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> mainNode = CommandManager
                .literal("chestshop")
                .build();

        LiteralCommandNode<ServerCommandSource> removeAllNode = CommandManager
                .literal("removeAll")
                .executes(removeAll::execute)
                .build();

        mainNode.addChild(removeAllNode);
        return mainNode;
    }
}
