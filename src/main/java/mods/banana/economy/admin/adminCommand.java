package mods.banana.economy.admin;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy.admin.balance.add;
import mods.banana.economy.admin.balance.balanceNode;
import mods.banana.economy.admin.balance.reset;
import mods.banana.economy.admin.balance.set;
import mods.banana.economy.admin.chestshop.chestshopNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class adminCommand {
    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> adminNode = CommandManager
                .literal("admin")
                .requires(source -> source.hasPermissionLevel(4))
                .build();

        LiteralCommandNode<ServerCommandSource> balance = balanceNode.build();

        LiteralCommandNode<ServerCommandSource> chestshop = chestshopNode.build();

        adminNode.addChild(balance);
        adminNode.addChild(chestshop);
        return adminNode;
    }
}
