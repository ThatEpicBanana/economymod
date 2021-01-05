package mods.banana.economy.admin.balance;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.balance.Balance;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class reset {
    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity target) throws CommandSyntaxException {
        Balance.resetPlayer(target.getEntityName());
        if(ctx.getSource().getPlayer() != null)
            ctx.getSource().getPlayer().sendSystemMessage(new LiteralText("success").formatted(Formatting.GREEN), UUID.randomUUID());
        else
            System.out.println("success");
        return 1;
    }
}
