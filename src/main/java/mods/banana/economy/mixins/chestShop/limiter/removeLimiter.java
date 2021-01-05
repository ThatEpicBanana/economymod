package mods.banana.economy.mixins.chestShop.limiter;

import mods.banana.economy.EconomyItems;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class removeLimiter {

    @Shadow
    public ClientConnection connection;

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    private MinecraftServer server;

    @Inject(method = { "onCloseHandledScreen" }, at = { @At("HEAD") }, cancellable = true)
    private void onClose(CloseHandledScreenC2SPacket packet, CallbackInfo ci) {
//        System.out.println("closing screen");

        ScreenHandler screenHandler = player.currentScreenHandler;

        //check all chest shop's current screen handler to check if it is this one
        for(ChestShop chestShop : ChestShop.chestShops) {
            for (ScreenHandler screenHandler1 : chestShop.currentScreenHandlers) {
                if (screenHandler == screenHandler1) {
                    // screen handler is a chest shop

//                    System.out.println("hey you closed a chest shop");

                    // remove the limiter from the player inventory if it has it
                    for(int i = 0; i < 41; i++) {
//                        player.inventory.removeStack(i);
                        if(EconomyItems.csLimit.sameTypeAs(player.inventory.getStack(i))) player.inventory.removeStack(i);
                        if(EconomyItems.csLimited.sameTypeAs(player.inventory.getStack(i))) player.inventory.removeStack(i);
                    }

                    // make sure the chest shop has a limiter
                    boolean hasLimiter = false;
                    for(ItemStack i : screenHandler.getStacks()) {
                        if(EconomyItems.csLimit.sameTypeAs(i)) { hasLimiter = true; break;}
                    }

                    // if the inventory doesn't have a limiter, reset it
                    if(!hasLimiter) {ChestShop.setLimit(player.currentScreenHandler, 26); System.out.println("resetting limiter");}

                    // remove this screen handler from the chest shop's list
                    chestShop.currentScreenHandlers.remove(screenHandler1);

                    break;
                }
            }
        }
    }
}
