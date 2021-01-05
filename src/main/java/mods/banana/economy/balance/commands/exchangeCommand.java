package mods.banana.economy.balance.commands;

import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.balance.Balance;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class exchangeCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        return 0;
    }
    
    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity target, long amount) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource(); 
        ServerPlayerEntity player = source.getPlayer(); 
        String playerName = source.getName(); 
        String targetName = target.getEntityName();

        Balance.verifyPlayer(playerName);
        Balance.verifyPlayer(targetName);

        if(Balance.getPlayer(playerName).bal >= amount) {
            //transfer balances
            Balance.balAdd(playerName, -amount);
            Balance.balAdd(targetName, amount);
            player.sendSystemMessage(new LiteralText("Transferred: " + amount + "¥"), UUID.randomUUID());
            return 1; // positive numbers are success! Negative numbers are failure.
        } else {
            player.sendSystemMessage(new LiteralText("You do not have enough Yen for this transaction!"), UUID.randomUUID());
            return 0;
        }
    }
}
