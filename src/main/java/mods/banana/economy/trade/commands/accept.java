package mods.banana.economy.trade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.trade.Trade;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class accept implements Command<ServerCommandSource> {

    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, ServerPlayerEntity target) throws CommandSyntaxException {
        for(Trade trade : Trade.tradeOffers) {
            if(trade.sender.player == target && trade.receiver.player == player) {
                Trade.trades.add(trade);
                Trade.tradeOffers.remove(trade);
                trade.sendUpdateMessage();
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        return 0;
    }
}
