package mods.banana.economy.mixins.chestShop;

import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerInteractionManager.class)
public class checkBreak {
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    public ServerWorld world;

    @Inject(method = "processBlockBreakingAction", at = { @At("HEAD") }, cancellable = true)
    private void stopBreak(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci) {
        if(action != PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) return;
        ChestShop shop = ChestShop.getShop(pos);
        if(checkShop(shop, pos, action)) ci.cancel();
        ChestShop signShop = ChestShop.getShopFromSign(pos);
        if(checkShop(signShop, pos, action)) ci.cancel();
    }

    private boolean checkShop(ChestShop shop, BlockPos pos, PlayerActionC2SPacket.Action action) {
        if(shop != null) {
            if(player.getEntityName().equals(shop.getOwner())) {
                shop.destroy();
            } else {
                this.player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(pos, this.world.getBlockState(pos), action, false, "destroyed"));
                return true;
            }
        }
        return false;
    }
}
