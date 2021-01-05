package mods.banana.economy.mixins.chestShop;

import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class checkChestOpen {

    @Inject(method = {"onUse"}, at = { @At("HEAD") }, cancellable = true)
    private void onOpen(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if(world.isClient) return;

        ChestShop shop = ChestShop.getShop(pos);

        // add player's screen handler to the chest shop
        if(shop != null) {
            // if player isn't the owner of the chest shop, cancel the opening
            if(!shop.getOwner().equals(player.getEntityName())) cir.setReturnValue(ActionResult.FAIL);
//            shop.currentScreenHandlers.add(player.currentScreenHandler);
        }
    }
}
