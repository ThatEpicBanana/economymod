package mods.banana.economy.trade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.trade.TradePlayer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class remove implements Command<ServerCommandSource> {
    public static int execute(ServerPlayerEntity player, int index) throws CommandSyntaxException {
        TradePlayer playerTrade = TradePlayer.getTradePlayer(player);
        //if index is out of bounds, fail
        if(index > playerTrade.trades.size()) return 0;
        //return stack to player
        playerTrade.player.inventory.insertStack(playerTrade.trades.get(index));
        //remove stack from player's trades
        playerTrade.trades.remove(index);
        //update players
        playerTrade.parent.sendUpdateMessage();
        return 1;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        return 0;
    }
}
