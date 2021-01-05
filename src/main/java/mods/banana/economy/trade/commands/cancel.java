package mods.banana.economy.trade.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.trade.Trade;
import mods.banana.economy.trade.TradePlayer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class cancel {
    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
        if(Trade.trades.contains(tradePlayer.parent)) {
            tradePlayer.parent.sendBothMessage("Trade cancelled...", Style.EMPTY.withFormatting(Formatting.RED));

            tradePlayer.parent.sender.clear();
            tradePlayer.parent.receiver.clear();

            Trade.trades.remove(tradePlayer.parent);
            return 1;
        } else return 0;
    }
}
