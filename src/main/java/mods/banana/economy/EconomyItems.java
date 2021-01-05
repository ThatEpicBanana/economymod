package mods.banana.economy;

import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public class EconomyItems {
    public static EconomyItem csLimit = new EconomyItem(Items.NETHER_STAR, "csLimit", new LiteralText("limit"), 1);
    public static EconomyItem csLimited = new EconomyItem(Items.GRAY_STAINED_GLASS_PANE, "csLimited", new LiteralText(""), 1);
}
