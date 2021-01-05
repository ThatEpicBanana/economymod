package mods.banana.economy.banknote;

import com.google.gson.Gson;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.balance.Balance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class bankNoteCommand {
    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player, long amount) throws CommandSyntaxException {
        Balance.verifyPlayer(player.getEntityName());

        if(Balance.getPlayer(player.getEntityName()).getBal() >= amount) {
            Gson gson = new Gson();

            ItemStack itemStack = new ItemStack(Items.PAPER);

            CompoundTag economyTag = new CompoundTag();
            economyTag.putLong("bal", amount);
            economyTag.putString("type", "banknote");

            ListTag loreTag = new ListTag();
            loreTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(amount + "¥").formatted(Formatting.WHITE))));

            CompoundTag displayTag = new CompoundTag();
            displayTag.put("Lore", loreTag);

            CompoundTag itemTag = new CompoundTag();
            itemTag.put("economy", economyTag);
            itemTag.put("display", displayTag);
            itemTag.putInt("CustomModelData", 1);

            itemStack.setTag(itemTag);

            itemStack.setCustomName(new LiteralText("Banknote").formatted(Formatting.GREEN));

            Balance.balAdd(player.getEntityName(), -amount);

            player.giveItemStack(itemStack);

            return 1;
        } else {
            player.sendSystemMessage(new LiteralText("You do not have enough balance for that!"), UUID.randomUUID());
            return 0;
        }
    }
}
