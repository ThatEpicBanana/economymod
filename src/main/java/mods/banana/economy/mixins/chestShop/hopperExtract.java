package mods.banana.economy.mixins.chestShop;

import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class hopperExtract {
    @Inject(method = {"transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;"}, at = { @At("HEAD") }, cancellable = true)
    private static void onExtract(Inventory from, Inventory to, ItemStack stack, int slot, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        for(ChestShop chestShop : ChestShop.chestShops) {
            if(from == chestShop.getInventory()) cir.setReturnValue(stack);
        }
    }
}
