package mods.banana.economy.mixins.chestShop.limiter;

import mods.banana.economy.EconomyItem;
import mods.banana.economy.EconomyItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class preventDrop {
    @Inject(method = { "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;" }, at = { @At("HEAD") }, cancellable = true)
    private void onDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if(EconomyItems.csLimit.sameTypeAs(stack)) cir.setReturnValue(null);
        if(EconomyItems.csLimited.sameTypeAs(stack)) cir.setReturnValue(null);
    }
}
