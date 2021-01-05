
package mods.banana.economy.mixins.trade.client;

import mods.banana.economy.balance.Balance;
import mods.banana.economy.trade.TradePlayer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;


import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Item.class)
public class useItem {
    @Inject(method = { "use" }, at = { @At("HEAD") }, cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> callbackInfo) {
        ItemStack item = user.getStackInHand(hand);


        //disregard client call
        if(user instanceof ClientPlayerEntity) return;

        //get player's trade class
        TradePlayer player = TradePlayer.getTradePlayer((ServerPlayerEntity) user);
        if(player != null) {
            if (hand == Hand.MAIN_HAND) {
                player.addSlot(user.inventory.selectedSlot);
            } else {
                player.addSlot(40);
            }
            callbackInfo.setReturnValue(TypedActionResult.success(user.getStackInHand(hand), world.isClient()));
            //return;
        }
    }
}
