package mods.banana.economy.trade.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import mods.banana.economy.trade.Trade;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class tradeCommand implements Command<ServerCommandSource> {

    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity target) throws CommandSyntaxException {
        ServerCommandSource source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();

        //sendItemAsMessage(player, player.getItemsHand().iterator().next());
        AtomicBoolean inTrade = new AtomicBoolean(false);
        Trade.trades.forEach(trade -> {
            if(trade.sender.player.getUuid() == player.getUuid()) inTrade.set(true);
        });
        if(!inTrade.get() && !player.equals(target)) {
            Trade.tradeOffers.add(new Trade(player, target));

            //serialize
            Text message = new LiteralText(player.getEntityName() + " has asked for a trade. Click here to accept!").fillStyle(Style.EMPTY
                    .withFormatting(Formatting.GREEN)
                    .withClickEvent(
                            new ClickEvent(
                                    ClickEvent.Action.RUN_COMMAND,
                                    "/trade accept " + player.getName().getString()
                            )
                    )
            );
            //send messages

            target.sendSystemMessage(message, UUID.randomUUID());
            return 1;
        } else {
            player.sendSystemMessage(new LiteralText("You are already in a trade!"), UUID.randomUUID());
            return 0;
        }
    }

    public static LiteralCommandNode<ServerCommandSource> build() {
        LiteralCommandNode<ServerCommandSource> tradeNode = CommandManager
                .literal("trade")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .executes(commandContext -> tradeCommand.execute(commandContext, EntityArgumentType.getPlayer(commandContext, "player")))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> acceptArgument = CommandManager
                .literal("accept")
                .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                                .executes(commandContext -> mods.banana.economy.trade.commands.accept.execute(commandContext, commandContext.getSource().getPlayer(), EntityArgumentType.getPlayer(commandContext, "player")))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> removeArgument = CommandManager
                .literal("remove")
                .then(
                        CommandManager.argument("index", IntegerArgumentType.integer(0))
                                .executes(commandContext -> mods.banana.economy.trade.commands.remove.execute(commandContext.getSource().getPlayer(), IntegerArgumentType.getInteger(commandContext, "index")))
                )
                .build();

        LiteralCommandNode<ServerCommandSource> cancelArgument = CommandManager
                .literal("cancel")
                .executes(commandContext -> mods.banana.economy.trade.commands.cancel.execute(commandContext, commandContext.getSource().getPlayer()))
                .build();

        LiteralCommandNode<ServerCommandSource> confirmArgument = CommandManager
                .literal("confirm")
                .executes(commandContext -> mods.banana.economy.trade.commands.confirm.execute(commandContext, commandContext.getSource().getPlayer()))
                .build();

        tradeNode.addChild(acceptArgument);
        tradeNode.addChild(removeArgument);
        tradeNode.addChild(cancelArgument);
        tradeNode.addChild(confirmArgument);

        return tradeNode;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        return 0;
    }
}
