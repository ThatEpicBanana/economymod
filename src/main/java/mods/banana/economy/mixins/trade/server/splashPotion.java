
package mods.banana.economy.mixins.trade.server;

import mods.banana.economy.trade.TradePlayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashPotionItem.class)
public class splashPotion {
    @Inject(method = { "use" }, at = { @At("HEAD") }, cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> callbackInfo) {
//        //disregard client call
//        if(user instanceof ClientPlayerEntity) return;

        //get player's trade class
        TradePlayer player = TradePlayer.getTradePlayer((ServerPlayerEntity) user);
        if(player != null) {
            if (hand == Hand.MAIN_HAND) {
                player.addSlot(user.inventory.selectedSlot);
            } else {
                player.addSlot(40);
            }
            callbackInfo.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
        }
    }
}
