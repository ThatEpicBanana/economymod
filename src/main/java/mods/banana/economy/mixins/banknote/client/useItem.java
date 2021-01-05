
package mods.banana.economy.mixins.banknote.client;

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


        if(item.getItem().equals(Items.PAPER) && item.getTag() != null && item.getTag().getCompound("economy").getString("type").equals("banknote")) {
//            System.out.println("BANKNOTE AAAAAAAAAAAAAAAAA");

            long amount = item.getTag().getCompound("economy").getLong("bal");

            Balance.balAdd(user.getEntityName(), amount);

            if(hand == Hand.MAIN_HAND) {
                user.inventory.removeStack(user.inventory.selectedSlot);
            } else {
                user.inventory.removeStack(41);
            }

            user.sendSystemMessage(new LiteralText("Deposited " + amount + "¥"), UUID.randomUUID());
        }
    }
}
