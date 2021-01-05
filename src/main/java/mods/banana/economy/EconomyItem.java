package mods.banana.economy;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class EconomyItem {
    private final ItemConvertible item;
    private final String type;
    private final Text name;
    private final CompoundTag tag;
    private final int customModelData;

    public EconomyItem(ItemConvertible item, String type, Text name) {
        this(item, type, name, -1, null);
    }

    public EconomyItem(ItemConvertible item, String type, Text name, int customModelData) {
        this(item, type, name, customModelData, null);
    }
    public EconomyItem(ItemConvertible item, String type, Text name, int customModelData, @Nullable CompoundTag tag) {
        this.item = item;
        this.type = type;
        this.name = name;
        this.tag = tag;
        this.customModelData = customModelData;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(item);

        CompoundTag economyTag = new CompoundTag();
        economyTag.putString("type", type);

        CompoundTag displayTag = new CompoundTag();
        displayTag.putString("Name", Text.Serializer.toJson(name));

        CompoundTag itemTag = new CompoundTag();
        itemTag.put("economy", economyTag);
        itemTag.put("display", displayTag);
        if(customModelData != -1) itemTag.putInt("CustomModelData", customModelData);

        if(tag != null) itemTag.put("tag", tag);

        itemStack.setTag(itemTag);

        return itemStack;
    }
    
    public boolean sameTypeAs(ItemStack itemStack) {
        return
                itemStack.hasTag() &&
                        itemStack.getTag().contains("economy") &&
                        itemStack.getTag().getCompound("economy").getString("type").equals(this.type);
    }
}
