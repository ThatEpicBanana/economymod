package mods.banana.economy.admin.chestshop;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;

public class removeAll {
    public static int execute(CommandContext<ServerCommandSource> commandContext) {
        if(ChestShop.chestShops.size() > 0) {
            ChestShop.chestShops = new ArrayList<>();
            return 1;
        } else return 0;
    }
}
