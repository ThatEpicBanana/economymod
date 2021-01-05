package mods.banana.economy.mixins.chestShop.limiter;

import mods.banana.economy.EconomyItems;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ScreenHandler.class)
public class setLimit {
    @Shadow
    public List<Slot> slots;

    @Inject(method = {"method_30010"}, at = { @At("HEAD") }, cancellable = true)
    private void onSlotClick(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir) {
        if(i < 0) return;

//        System.out.println(slotActionType + "" + i + slots.get(i).getStack().getName());

        //if item set down is a limiter item, set the chestShop limit to that index
        if(EconomyItems.csLimit.sameTypeAs(playerEntity.inventory.getCursorStack())) {
            ChestShop.setLimit(playerEntity.currentScreenHandler, i);

            //remove limit item from chest and just use normal slot click bc minecraft weird
            playerEntity.currentScreenHandler.setStackInSlot(i, ItemStack.EMPTY);
            playerEntity.inventory.setCursorStack(EconomyItems.csLimit.toItemStack());
        }

        if(slotActionType == SlotActionType.SWAP && EconomyItems.csLimit.sameTypeAs(slots.get(i).getStack())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
