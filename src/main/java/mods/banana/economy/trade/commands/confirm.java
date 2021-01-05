package mods.banana.economy.trade.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.trade.Trade;
import mods.banana.economy.trade.TradePlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class confirm {
    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
        if(tradePlayer != null && tradePlayer.parent.acceptable) {
            tradePlayer.accepted = true;
            tradePlayer.player.sendSystemMessage(new LiteralText("Trade confirmed!").fillStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), UUID.randomUUID());

            if(tradePlayer.parent.sender.accepted && tradePlayer.parent.receiver.accepted) {
                Trade trade = tradePlayer.parent;
                for(ItemStack itemStack : trade.sender.trades) {
                    trade.receiver.player.giveItemStack(itemStack);
                }
                for(ItemStack itemStack : trade.receiver.trades) {
                    trade.sender.player.giveItemStack(itemStack);
                }

                Trade.trades.remove(trade);
            }
        }
        return 0;
    }
}
