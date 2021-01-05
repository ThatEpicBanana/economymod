package mods.banana.economy.mixins.chestShop.limiter;

import mods.banana.economy.EconomyItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class preventLimitEntities {
    @Inject(method = "spawnEntity", at = @At(value = "HEAD"), require = 1, cancellable = true)
    private void onSpawn(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // nuclear way of making sure you can't spawn limit entities
        if(entity instanceof ItemEntity) {
            if(EconomyItems.csLimit.sameTypeAs(((ItemEntity) entity).getStack())) cir.setReturnValue(false);
            if(EconomyItems.csLimited.sameTypeAs(((ItemEntity) entity).getStack())) cir.setReturnValue(false);
        }
    }
}
