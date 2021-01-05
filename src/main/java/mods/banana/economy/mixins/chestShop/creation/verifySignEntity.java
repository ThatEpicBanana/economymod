package mods.banana.economy.mixins.chestShop.creation;

import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class verifySignEntity {
    @Shadow
    public ServerPlayerEntity player;

    //this only took far too long
    @ModifyVariable(method = "method_31282", at = @At(value = "HEAD"), require = 1)
    private List<String> verifySign(List<String> list) {
        if(ChestShop.validSign(list, player) && list.get(0) == "") list.set(0, player.getEntityName());
        return list;
    }
}
