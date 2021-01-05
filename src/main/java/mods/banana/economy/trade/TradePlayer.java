package mods.banana.economy.trade;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;

public class TradePlayer {
    public Trade parent;
    public ServerPlayerEntity player;
    public ArrayList<ItemStack> trades;

    public boolean accepted = false;

    public TradePlayer(Trade parent, ServerPlayerEntity player) {
        this.parent = parent;
        this.player = player;
        this.trades = new ArrayList<ItemStack>();
    }

    public void clear() {
        for(int i = 0; i < trades.size();) {
            player.inventory.insertStack(trades.get(0));
            trades.remove(0);
        }
    }

    public boolean addSlot(int slot) {
        if(player.inventory.size() > slot) {
            //add item
            trades.add(slot < 40 ? player.inventory.main.get(slot) : player.inventory.offHand.get(0));
            //send update message
            parent.sendUpdateMessage();
            //remove item
            player.inventory.removeStack(slot);
            return true;
        } else return false;
    }

    public static TradePlayer getTradePlayer(ServerPlayerEntity player) {
        for (Trade trade : Trade.trades) {
            if (trade.sender.player.getUuid() == player.getUuid())
                return trade.sender;
            else if (trade.receiver.player.getUuid() == player.getUuid())
                return trade.receiver;
        }
        return null;
    }
}
