package mods.banana.economy.chestShop;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import mods.banana.economy.EconomyItems;
import mods.banana.economy.balance.Balance;
import mods.banana.economy.balance.Player;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class ChestShop {
    public static ArrayList<ChestShop> chestShops = new ArrayList<>();

    public enum TYPE {
        BUY_ONLY,
        SELL_ONLY,
        BOTH
    }

//    @Expose
//    BlockState blockState;
    @Expose
    BlockPos blockPos;
    @Expose
    World world;
    @Expose
    BlockPos sign;

    @Expose
    String item;

    @Expose
    int amount;
    @Expose
    long buy;
    @Expose
    long sell;

    @Expose
    String owner;
    @Expose
    TYPE type;

    public ArrayList<ScreenHandler> currentScreenHandlers;

    //int limit;

//    ChestShop(World world, BlockPos blockPos, String owner, String item, int amount, long buy, long sell) {
//        this(world.getBlockState(blockPos), blockPos, world, owner, item, amount, buy, sell);
//    }

    ChestShop(World world, BlockPos sign, BlockPos chest, String owner, String item, int amount, long buy, long sell, TYPE type) {
//        this.blockState = blockState;
        this.blockPos = chest;
        this.sign = sign;
        this.world = world;
        this.owner = owner;
        this.item = item;
        this.amount = amount;
        this.buy = buy;
        this.sell = sell;
        this.type = type;
        currentScreenHandlers = new ArrayList<>();
    }

    public World getWorld() { return world; }
//    public BlockState getBlockState() { return blockState; }
    public BlockPos getBlockPos() { return blockPos; }
    public BlockPos getSign() { return sign; }

    public String getItemName() { return item; }

    public String getOwner() { return owner; }

    public int getAmount() { return amount; }
    public long getBuy() { return buy; }
    public long getSell() { return sell; }

    public Inventory getInventory() {
        return (Inventory) world.getChunk(blockPos).getBlockEntity(blockPos);
    }
    public Item getItem() { return Registry.ITEM.get(new Identifier(item)); }
    public TYPE getType() { return type; }

    public void destroy() {
        Inventory inventory = getInventory();
        for(int i = 0; i < inventory.size(); i++) {
            if(EconomyItems.csLimit.sameTypeAs(inventory.getStack(i))) inventory.removeStack(i);
            if(EconomyItems.csLimited.sameTypeAs(inventory.getStack(i))) inventory.removeStack(i);
        }
        chestShops.remove(this);
    }

    public void buy(ServerPlayerEntity player) {
        // get chest shop's inventory
        Inventory inventory = getInventory();

        if(!owner.equals("Admin")) {
            // check if chest shop is sell only
            if(getType() != ChestShop.TYPE.SELL_ONLY) {
                // check if the chest shop has enough items to satisfy the trade
                if (getItemCount() >= getAmount()) {
                    // check if player has enough yen to satisfy the trade
                    if (Balance.balGet(player.getEntityName()) >= getBuy()) {
                        int count = getAmount();
                        Item item = getItem();
                        // iterate through the items until the chest shop's limit or the amount needed to remove goes below zero
                        for (int stack = 0; stack < getLimit() && count > 0; stack++) {
                            // if the stack is the item needed
                            if (inventory.getStack(stack).getItem().equals(item)) {
                                // remove the amount needed from the stack
                                int stackCount = inventory.getStack(stack).getCount();
                                inventory.removeStack(stack, Math.min(count, inventory.getStack(stack).getMaxCount()));
                                count -= stackCount;
                            }
                        }
                        // add yen to owner
                        Balance.balAdd(getOwner(), getBuy());
                        // remove yen from player
                        Balance.balAdd(player.getEntityName(), -getBuy());
                        // give player the items
                        player.giveItemStack(new ItemStack(item, getAmount()));
                    } else player.sendSystemMessage(new LiteralText("You do not have enough yen to do this trade!").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("The shop does not have enough items to do this trade!").formatted(Formatting.RED), UUID.randomUUID());
            } else player.sendSystemMessage(new LiteralText("This shop is sell only!").formatted(Formatting.RED), UUID.randomUUID());
        } else {
            // check if chest shop is sell only
            if(getType() != ChestShop.TYPE.SELL_ONLY) {
                // check if player has enough yen to satisfy the trade
                if (Balance.balGet(player.getEntityName()) >= getBuy()) {
                    player.giveItemStack(new ItemStack(getItem(), amount));
                    Balance.balAdd(player.getEntityName(), -getBuy());
                    Balance.balAdd("Admin", getBuy());
                } else player.sendSystemMessage(new LiteralText("You do not have enough yen to do this trade!").formatted(Formatting.RED), UUID.randomUUID());
            } else player.sendSystemMessage(new LiteralText("This shop is sell only!").formatted(Formatting.RED), UUID.randomUUID());
        }
    }

    public void sell(ServerPlayerEntity player) {
//        System.out.println("selling");
        if(!owner.equals("Admin")) {
            if(getType() != ChestShop.TYPE.BUY_ONLY) {
                if(hasEnoughToSell(player)) {
                    if(hasSpace(getAmount())) {
                        if(Balance.balGet(getOwner()) >= getSell()) {
                            // add sold stack
                            insertItemStack(new ItemStack(getItem(), getAmount()));
                            // remove money from owner
                            Balance.balAdd(getOwner(), -getSell());
                            // add money to seller
                            Balance.balAdd(player.getEntityName(), getSell());
                            // remove stacks from seller
                            int count = getAmount();
                            for(int i = 0; i < player.inventory.size() && count > 0; i++) {
                                ItemStack stack = player.inventory.getStack(i);
                                if(stack.getItem().equals(getItem())) {
                                    int stackCount = stack.getCount();

                                    stack.setCount(stackCount - count);
                                    count -= stackCount;

                                    if(stack.getCount() < 0) player.inventory.removeStack(i);
                                }
                            }
                        } else player.sendSystemMessage(new LiteralText("The chest shop owner does not have enough yen to do this trade!").formatted(Formatting.RED), UUID.randomUUID());
                    } else player.sendSystemMessage(new LiteralText("The chest shop does not have enough space to hold more items!").formatted(Formatting.RED), UUID.randomUUID());
                } else player.sendSystemMessage(new LiteralText("You do not have enough items to sell!").formatted(Formatting.RED), UUID.randomUUID());
            } else player.sendSystemMessage(new LiteralText("This shop is buy only!").formatted(Formatting.RED), UUID.randomUUID());
        } else {
            System.out.println("admin sell");
            if(getType() != ChestShop.TYPE.BUY_ONLY) {
                if(hasEnoughToSell(player)) {
//                    System.out.println("admin sell");
                    // remove money from owner
                    Balance.balAdd("Admin", -getSell());
                    // add money to seller
                    Balance.balAdd(player.getEntityName(), getSell());
                    // remove stacks from seller
                    int count = getAmount();
                    for(int i = 0; i < player.inventory.size() && count > 0; i++) {
                        ItemStack stack = player.inventory.getStack(i);
                        if(stack.getItem().equals(getItem())) {
                            int stackCount = stack.getCount();

                            stack.setCount(stackCount - count);
                            count -= stackCount;

                            if(stack.getCount() < 0) player.inventory.removeStack(i);
                        }
                    }
                } else player.sendSystemMessage(new LiteralText("You do not have enough items to sell!").formatted(Formatting.RED), UUID.randomUUID());
            } else player.sendSystemMessage(new LiteralText("This shop is buy only!").formatted(Formatting.RED), UUID.randomUUID());
        }
    }

    public int getLimit() {
        Inventory inventory = getInventory();
        for(int i = 0; i < inventory.size(); i++) {
            if(EconomyItems.csLimit.sameTypeAs(inventory.getStack(i)))
                return i;
        }
        return -1;
    }

    public int getItemCount() {
        Item item = getItem();
        int i = 0;

        for(int j = 0; j < getLimit(); ++j) {
            ItemStack itemStack = getInventory().getStack(j);
            if (itemStack.getItem().equals(item)) {
                i += itemStack.getCount();
            }
        }

        return i;
    }

    public boolean hasSpace(int amount) {
        Inventory inventory = getInventory();
        int space = 0;
        for(int i = 0; i < getLimit(); i++) {
            if(inventory.getStack(i).isEmpty()) // if stack is empty
                space += getItem().getMaxCount(); // add max stack count
            else if(inventory.getStack(i).getItem().equals(getItem())) // if the item is the same
                space += getItem().getMaxCount() - inventory.getStack(i).getCount(); // add the space left in the stack
        }
        return space >= amount;
    }

    public boolean hasEnoughToSell(ServerPlayerEntity player) {
        int count = 0;
        for(int i = 0; i < player.inventory.size(); i++) {
            if(player.inventory.getStack(i).getItem().equals(getItem())) {
                count += player.inventory.getStack(i).getCount();
                if(count >= amount) return true;
            }
        }
        return false;
    }

    public void setLimit(int index) {
        Inventory inventory = getInventory();

        //return if index is out of bounds
        if(index < 0 || index > 26) return;

        //remove all limited items
        for(int i = 0; i < 26; i++) {
            if(EconomyItems.csLimited.sameTypeAs(inventory.getStack(i))) inventory.setStack(i, ItemStack.EMPTY);
        }

        //if index slot isn't already the limit item, set it to it.
        inventory.setStack(index, EconomyItems.csLimit.toItemStack());

        //add all of the limited item
        for(int i = 26; i > index; i--) {
            if(inventory.getStack(i) != ItemStack.EMPTY && !EconomyItems.csLimited.sameTypeAs(inventory.getStack(i))) insertItemStack(inventory.getStack(i));
            inventory.setStack(i, EconomyItems.csLimited.toItemStack());
        }
    }

    public static void setLimit(ScreenHandler screenHandler, int index) {
        //return if index is out of bounds
        if(index < 0 || index > 26) return;

        //remove all limited items
        for(int i = 0; i < 26; i++) {
            if(EconomyItems.csLimited.sameTypeAs(screenHandler.getStacks().get(i))) screenHandler.slots.get(i).setStack(ItemStack.EMPTY);
        }

        //if index slot isn't already the limit item, set it to it.
        screenHandler.slots.get(index).setStack(EconomyItems.csLimit.toItemStack());

        //add all of the limited item
        for(int i = 26; i > index; i--) {
            if(screenHandler.slots.get(i).getStack() != ItemStack.EMPTY && !EconomyItems.csLimited.sameTypeAs(screenHandler.slots.get(i).getStack())) insertItemStack(screenHandler, screenHandler.slots.get(i).getStack());
            screenHandler.slots.get(i).setStack(EconomyItems.csLimited.toItemStack());
        }
    }

    public void insertItemStack(ItemStack input) {
        Inventory inventory = getInventory();
        Item item = input.getItem();

        // for each slot
        for(int i = 0; i < inventory.size() && input.getCount() > 0; i++) {
            ItemStack stack = inventory.getStack(i);
            if(
                    stack.isEmpty() || // if slot is empty
                            (stack.getItem() == item && stack.getCount() < stack.getMaxCount()) // or the slot is the same item and has space
            ) {
                // get count
                int count = Math.min(input.getCount() + stack.getCount(), input.getMaxCount()) - stack.getCount();
                // add count to item stack
                ItemStack newStack = input.copy();
                newStack.setCount(count + stack.getCount());
                inventory.setStack(i, newStack);
                // remove count from input
                input.setCount(input.getCount() - count);
            }
        }

        if(input.getCount() > 0) world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), input));
    }

    public static void insertItemStack(ScreenHandler screenHandler, ItemStack input) {
        Item item = input.getItem();
        for(int i = 0; i < 27 && input.getCount() > 0; i++) {
            ItemStack currentStack = screenHandler.getSlot(i).getStack();
            if(
                    currentStack.isEmpty() || // if slot is empty
                            (currentStack.getItem() == item && currentStack.getCount() < currentStack.getMaxCount()) // or the slot is the same item and has space
            ) {
                // get count
                int count = Math.min(input.getCount() + currentStack.getCount(), input.getMaxCount()) - currentStack.getCount();
                // add count to item stack
                ItemStack itemStack = input.copy();
                itemStack.setCount(count + currentStack.getCount());
                screenHandler.setStackInSlot(i, itemStack);
                // remove count from input
                input.setCount(input.getCount() - count);
            }
        }
    }

    public static @Nullable ChestShop getTouchedShop(BlockPos blockPos) {
        if(getShop(blockPos.north()) != null) return getShop(blockPos.north());
        if(getShop(blockPos.east()) != null) return getShop(blockPos.east());
        if(getShop(blockPos.south()) != null) return getShop(blockPos.south());
        if(getShop(blockPos.west()) != null) return getShop(blockPos.west());
        if(getShop(blockPos.up()) != null) return getShop(blockPos.up());
        if(getShop(blockPos.down()) != null) return getShop(blockPos.down());
        return null;
    }

    public static ChestShop fromSign(BlockPos sign, BlockState blockState, BlockPos chest, World world, List<String> lines) {
//        TYPE type = getTypeFromString(lines.get(2));
        long buy = -1;
        long sell = -1;

        switch (getTypeFromString(lines.get(2))) {
            case BOTH:
                buy = Long.parseLong(lines.get(2).replaceAll("^B (\\d+) : (\\d+) S$", "$1"));
                sell = Long.parseLong(lines.get(2).replaceAll("^B (\\d+) : (\\d+) S$", "$2"));
                break;
            case BUY_ONLY:
                buy = Long.parseLong(lines.get(2).replaceAll("^B (\\d+)$", "$1"));
                break;
            case SELL_ONLY:
                sell = Long.parseLong(lines.get(2).replaceAll("^S (\\d+)$", "$1"));
                break;
        }

        ChestShop chestShop = new ChestShop(
                world,
                sign,
                chest,
                lines.get(0),
                lines.get(3).toLowerCase(),
                Integer.parseInt(lines.get(1)),
                buy,
                sell,
                getTypeFromString(lines.get(2))
        );

        chestShop.setLimit(26);
        return chestShop;
    }

    public static TYPE getTypeFromString(String string) {
        if(string.matches("^B (\\d+) : (\\d+) S$")) return TYPE.BOTH;
        if(string.matches("^B (\\d+)$")) return TYPE.BUY_ONLY;
        if(string.matches("^S (\\d+)$")) return TYPE.SELL_ONLY;
        return null;
    }

    public static boolean validSign(List<String> lines, ServerPlayerEntity player) {
        return
                (lines.get(0).equals(player.getEntityName()) || (lines.get(0).equals("Admin") && player.hasPermissionLevel(4))) &&
                        lines.get(1).matches("\\d+") &&
                        getTypeFromString(lines.get(2)) != null &&
                        Registry.ITEM.getOrEmpty(new Identifier(lines.get(3).toLowerCase())).isPresent();
    }

    public static ChestShop getShop(int x, int y, int z) {return getShop(new BlockPos(x,y,z));}

    public static @Nullable ChestShop getShop(BlockPos position) {
        for(ChestShop shop : chestShops) {
            if(
                    shop.blockPos.getX() == position.getX() &&
                            shop.blockPos.getY() == position.getY() &&
                            shop.blockPos.getZ() == position.getZ()
            ) return shop;
        }
        return null;
    }

    public static @Nullable ChestShop getShopFromSign(BlockPos pos) {
        for(ChestShop shop : chestShops) {
            if(
                    shop.sign.getX() == pos.getX() &&
                            shop.sign.getY() == pos.getY() &&
                            shop.sign.getZ() == pos.getZ()
            ) return shop;
        }
        return null;
    }

    public String toString() {
        StringBuilder string = new StringBuilder(
                "\nPosition: " + blockPos.getX() + ", " + blockPos.getY() + ", " + blockPos.getZ() +
                "\nSign Position: " + sign.getX() + ", " + sign.getY() + ", " + sign.getZ() +
                "\nType: " + type.toString() +
                "\nOwner: " + owner +
                "\nB " + buy + " : " + sell + " S" +
                "\nItem: " + amount + " " + item);

//        for(ScreenHandler screenHandler : currentScreenHandlers) {
//            string.append("\n").append(screenHandler);
//        }

        return string.toString();
    }

    public static class Serializer implements JsonSerializer<ChestShop> {
        @Override
        public JsonElement serialize(ChestShop src, Type typeOfSrc, JsonSerializationContext context) {
            Gson gson = new Gson();

            JsonObject main = new JsonObject();

            main.addProperty("amount", src.getAmount());
            main.addProperty("buy", src.getBuy());
            main.addProperty("sell", src.getSell());

            main.addProperty("item", src.getItemName());

            main.addProperty("owner", src.getOwner());

            JsonObject position = new JsonObject();

            position.addProperty("x", src.getBlockPos().getX());
            position.addProperty("y", src.getBlockPos().getY());
            position.addProperty("z", src.getBlockPos().getZ());

            main.add("position", position);

            JsonObject signPos = new JsonObject();

            signPos.addProperty("x", src.getSign().getX());
            signPos.addProperty("y", src.getSign().getY());
            signPos.addProperty("z", src.getSign().getZ());

            main.add("sign", signPos);

            MutableRegistry<DimensionType> dimensionTypes = src.getWorld().getRegistryManager().get(Registry.DIMENSION_TYPE_KEY);

            if(dimensionTypes.get(DimensionType.OVERWORLD_REGISTRY_KEY) == src.getWorld().getDimension()) main.addProperty("world", "overworld");
            if(dimensionTypes.get(DimensionType.THE_NETHER_REGISTRY_KEY) == src.getWorld().getDimension()) main.addProperty("world", "nether");
            if(dimensionTypes.get(DimensionType.THE_END_REGISTRY_KEY) == src.getWorld().getDimension()) main.addProperty("world", "end");

            main.addProperty("type", src.getType().toString());

            return main;
        }

//        @Override
        public static ChestShop deserialize(JsonElement json, Map<RegistryKey<World>, ServerWorld> worlds) {
            Gson gson = new Gson();

            JsonObject jsonObject = json.getAsJsonObject();

            // get world
            World world = null;
            switch(jsonObject.get("world").getAsString()) {
                case "overworld":
                    world = worlds.get(World.OVERWORLD);
                    break;
                case "nether":
                    world = worlds.get(DimensionType.THE_NETHER_REGISTRY_KEY);
                    break;
                case "end":
                    world = worlds.get(DimensionType.THE_END_REGISTRY_KEY);
                    break;
                default:
                    System.out.println("chest shop world saved incorrectly");
            }

            TYPE type = null;
            switch(jsonObject.get("type").getAsString()) {
                case "BOTH": type = TYPE.BOTH; break;
                case "BUY_ONLY": type = TYPE.BUY_ONLY; break;
                case "SELL_ONLY": type = TYPE.SELL_ONLY; break;
            }

            return new ChestShop(
                    world,
                    new BlockPos(
                            jsonObject.get("sign").getAsJsonObject().get("x").getAsInt(),
                            jsonObject.get("sign").getAsJsonObject().get("y").getAsInt(),
                            jsonObject.get("sign").getAsJsonObject().get("z").getAsInt()
                    ),
                    new BlockPos(
                            jsonObject.get("position").getAsJsonObject().get("x").getAsInt(),
                            jsonObject.get("position").getAsJsonObject().get("y").getAsInt(),
                            jsonObject.get("position").getAsJsonObject().get("z").getAsInt()
                    ),
                    jsonObject.get("owner").getAsString(),
                    jsonObject.get("item").getAsString(),
                    jsonObject.get("amount").getAsInt(),
                    jsonObject.get("buy").getAsLong(),
                    jsonObject.get("sell").getAsLong(),
                    type
            );
        }
    }

//    private static class Deserializer implements JsonDeserializer<ChestShop> {
//        @Override
//        public ChestShop deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            return new ChestShop(
//                    MinecraftServer.
//            )
//        }
//    }

}
