package mods.banana.economy.balance.commands;

import java.util.UUID;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import mods.banana.economy.balance.Balance;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class balCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource(); 
        ServerPlayerEntity player = source.getPlayer(); 
        String playerName = source.getName(); 
        
        //get command source and name
//        System.out.println(playerName);
        Balance.verifyPlayer(playerName);

//        System.out.println(Balance.getPlayer(playerName).getBal());

        player.sendSystemMessage(new LiteralText("Balance: " + Balance.getPlayer(playerName).bal + "¥"), UUID.randomUUID());
        
        return 1; // positive numbers are success! Negative numbers are failure.
    }

    public static int target(ServerCommandSource source, ServerPlayerEntity target) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer(); 
        String targetName = target.getEntityName();

//        System.out.println("getting " + targetName + "'s balance");
        Balance.verifyPlayer(targetName);
        player.sendSystemMessage(new LiteralText("Balance: " + Balance.getPlayer(targetName).bal + "¥"), UUID.randomUUID());
        
        return 1;
    }
}
