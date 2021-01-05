package mods.banana.economy;

import mods.banana.economy.admin.adminCommand;
import mods.banana.economy.balance.Balance;
import mods.banana.economy.balance.commands.balTopCommand;
import mods.banana.economy.banknote.bankNoteCommand;
import mods.banana.economy.chestShop.ChestShop;
import mods.banana.economy.chestShop.commands.ChestshopCommand;
import mods.banana.economy.trade.commands.tradeCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import mods.banana.economy.balance.commands.balCommand;
import mods.banana.economy.balance.commands.exchangeCommand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;


//this beautiful brigadier code is only possible due to https://gist.github.com/falkreon/f58bb91e45ba558bc7fd827e81c6cb45

public class main implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            //create nodes
            LiteralCommandNode<ServerCommandSource> balNode = CommandManager
	            .literal("bal")
                    .executes(new balCommand())
                    .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                        .executes(commandContext -> balCommand.target(commandContext.getSource(), EntityArgumentType.getPlayer(commandContext, "player")))
                    )
                .build();

            LiteralCommandNode<ServerCommandSource> exchangeNode = CommandManager
                .literal("exchange")
                    .then(
                        CommandManager.argument("player", EntityArgumentType.player())
                        .then(
                            CommandManager.argument("amount", LongArgumentType.longArg(0))
                            .executes(commandContext -> exchangeCommand.execute(commandContext, EntityArgumentType.getPlayer(commandContext, "player"), LongArgumentType.getLong(commandContext, "amount")))
                        )
                    )
                .build();

            LiteralCommandNode<ServerCommandSource> excNode = CommandManager
                    .literal("exc")
                        .redirect(exchangeNode)
                    .build();

            LiteralCommandNode<ServerCommandSource> tradeNode = tradeCommand.build();

            LiteralCommandNode<ServerCommandSource> balTopNode = CommandManager
                    .literal("baltop")
                        .executes(commandContext -> balTopCommand.execute(commandContext, commandContext.getSource().getPlayer()))
                    .build();

            LiteralCommandNode<ServerCommandSource> adminNode = adminCommand.build();

            LiteralCommandNode<ServerCommandSource> bankNoteNode = CommandManager
                    .literal("banknote")
                        .then(
                                CommandManager.argument("amount", LongArgumentType.longArg(0))
                                        .executes(commandContext -> bankNoteCommand.execute(commandContext, commandContext.getSource().getPlayer(), LongArgumentType.getLong(commandContext, "amount")))
                        )
                    .build();

            LiteralCommandNode<ServerCommandSource> chestshopCommandNode = ChestshopCommand.build();

            
            //stitching
            dispatcher.getRoot().addChild(balNode);
            dispatcher.getRoot().addChild(exchangeNode);
            dispatcher.getRoot().addChild(excNode);
            dispatcher.getRoot().addChild(tradeNode);
            dispatcher.getRoot().addChild(balTopNode);
            dispatcher.getRoot().addChild(adminNode);
            dispatcher.getRoot().addChild(bankNoteNode);
            dispatcher.getRoot().addChild(chestshopCommandNode);
        });

        System.out.println("init");

        //create directory
        File directory = new File("economy");
        if(directory.mkdir()) {
            System.out.println("Directory created");
        } else {
            System.out.println("Directory already exists");
        }

        try {
            Balance.setup();
//            ChestShop.onLoad();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
