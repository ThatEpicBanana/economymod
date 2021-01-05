package mods.banana.economy.chestShop.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ChestshopCommand {
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> mainNode = CommandManager
                .literal("chestshop")
                .build();

        LiteralCommandNode<ServerCommandSource> listNode = CommandManager
                .literal("list")
                .executes(list::execute)
                .build();

//        LiteralCommandNode<ServerCommandSource> openNode = CommandManager
//                .literal("openshop")
//                .executes(openshop::execute)
//                .build();

        mainNode.addChild(listNode);
//        mainNode.addChild(openNode);

        return mainNode;
    }
}
