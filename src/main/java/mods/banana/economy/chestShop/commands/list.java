package mods.banana.economy.chestShop.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class list {
    public static int execute(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();

        player.sendSystemMessage(new LiteralText("Chestshops:").formatted(Formatting.BOLD), UUID.randomUUID());

        for(ChestShop chestShop : ChestShop.chestShops) {
            player.sendSystemMessage(new LiteralText(chestShop.toString()), UUID.randomUUID());
        }
        return 1;
    }
}
