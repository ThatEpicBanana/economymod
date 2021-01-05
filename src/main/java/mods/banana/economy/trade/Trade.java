package mods.banana.economy.trade;

import com.google.gson.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class Trade {
    public static ArrayList<Trade> trades = new ArrayList<Trade>();
    public static ArrayList<Trade> tradeOffers = new ArrayList<Trade>();

    public TradePlayer sender;
    public TradePlayer receiver;

    public long timer = 0;
    public boolean acceptable = false;

    enum Side {
        SENDER,
        RECEIVER
    }

    public Trade(ServerPlayerEntity sender, ServerPlayerEntity receiver) {
        this.sender = new TradePlayer(this, sender);
        this.receiver = new TradePlayer(this, receiver);
    }

    public void sendBothMessage(String string, @Nullable Style style) {
        LiteralText text = new LiteralText(string);
        if(style != null) text.fillStyle(style);
        sender.player.sendSystemMessage(text, UUID.randomUUID());
        receiver.player.sendSystemMessage(text, UUID.randomUUID());
    }

    private void sendRawUpdateMessage() {
        sender.player.sendSystemMessage(new LiteralText("Trade with " + receiver.player.getName().asString()).formatted(Formatting.BOLD).formatted(Formatting.YELLOW).formatted(Formatting.UNDERLINE), UUID.randomUUID());
        receiver.player.sendSystemMessage(new LiteralText("Trade with " + sender.player.getName().asString()).formatted(Formatting.BOLD).formatted(Formatting.YELLOW).formatted(Formatting.UNDERLINE), UUID.randomUUID());

        sendBothMessage("\n" + sender.player.getName().getString() + ":", null);
        sendItems(sender.trades, Side.SENDER);
        sendBothMessage(receiver.player.getName().getString() + ":", null);
        sendItems(receiver.trades, Side.RECEIVER);
    }

    public void sendUpdateMessage() {
        sendRawUpdateMessage();

        sendBothMessage("\nCancel", Style.EMPTY.withFormatting(Formatting.DARK_RED).withClickEvent(
                new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/trade cancel"
                )
        ));

        timer = 0;
        sender.accepted = false;
        receiver.accepted = false;
        acceptable = false;
    }

    public void sendUpdateWithAccept() {
        sendRawUpdateMessage();

        JsonArray messageJson = new JsonArray();
        //add text
        messageJson.add(Text.Serializer.toJsonTree(new LiteralText("\nConfirm").fillStyle(Style.EMPTY
                .withFormatting(Formatting.GREEN)
                .withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/trade confirm"
                        )
                )
        )));
        messageJson.add(Text.Serializer.toJsonTree(new LiteralText(" Cancel").fillStyle(Style.EMPTY
                .withFormatting(Formatting.DARK_RED)
                .withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/trade cancel"
                        )
                )
        )));
        //serialize
        Text message = Text.Serializer.fromJson(messageJson);

        sender.player.sendSystemMessage(message, UUID.randomUUID());
        receiver.player.sendSystemMessage(message, UUID.randomUUID());
    }

    private void sendItems(ArrayList<ItemStack> items, Side side) {
        for (int i = 0; i < items.size(); i++) {
            //get item
            ItemStack item = items.get(i);

            JsonArray messageJson = new JsonArray();
            //add text
            messageJson.add(Text.Serializer.toJsonTree(getItemMessage(item)));
            messageJson.add(Text.Serializer.toJsonTree(getClearMessage(i)));
            //serialize
            Text message = Text.Serializer.fromJson(messageJson);
            //send messages
            sender.player.sendSystemMessage(side == Side.SENDER ? message : getItemMessage(item), UUID.randomUUID());
            receiver.player.sendSystemMessage(side == Side.RECEIVER ? message : getItemMessage(item), UUID.randomUUID());
        }
    }

    private static Text getItemMessage(ItemStack item) {
        return getItemMessage(item, "   " + item.getCount() + " " + item.getName().getString());
    }

    public static Text getItemMessage(ItemStack item, String text) {
        Style style = Style.EMPTY
                .withFormatting(Formatting.GRAY)
                .withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_ITEM,
                                new HoverEvent.ItemStackContent(item)
                        )
                );
        return new LiteralText(text).fillStyle(style);
    }

    public static Text getClearMessage(int index) {
        Style style = Style.EMPTY.withFormatting(Formatting.RED)
                .withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/trade remove " + index
                        )
                ).withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new LiteralText("Removes item from current trade").formatted(Formatting.RED)
                        )
        );
        return new LiteralText(" Remove").fillStyle(style);
    }
}
