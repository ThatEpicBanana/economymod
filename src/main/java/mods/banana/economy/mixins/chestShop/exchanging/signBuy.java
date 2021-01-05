package mods.banana.economy.mixins.chestShop.exchanging;

import mods.banana.economy.balance.Balance;
import mods.banana.economy.balance.Player;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(SignBlockEntity.class)
public class signBuy {
    @Inject(method = "onActivate", at = { @At("HEAD") })
    private void onUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        BlockPos pos = new BlockPos(player.raycast(6, player.getServer().getTickTime(), false).getPos());
        for(ChestShop chestShop : ChestShop.chestShops) {
            // get chest shop from sign
            if(chestShop.getSign().equals(pos)) {
                chestShop.buy((ServerPlayerEntity) player);
            }
        }
    }
}
