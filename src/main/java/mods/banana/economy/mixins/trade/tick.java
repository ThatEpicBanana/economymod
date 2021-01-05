
package mods.banana.economy.mixins.trade;

import mods.banana.economy.chestShop.ChestShop;
import mods.banana.economy.trade.Trade;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;
import java.util.function.BooleanSupplier;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import mods.banana.economy.balance;

@Mixin(MinecraftServer.class)
public class tick {
    @Shadow
    private int ticks;

    @Inject(method = { "tick" }, at = { @At("HEAD") })
    private void save(BooleanSupplier shouldKeepTicking, CallbackInfo callbackInfo) {
//        if(this.ticks % 20 == 0) {
//            System.out.println(ChestShop.chestShops);
//        }
        for(Trade i : Trade.trades) {
            if(!i.acceptable) {
                i.timer += 1;
                if(i.timer > 60) {
                    i.acceptable = true;
                    i.sendUpdateWithAccept();
                }
            }
        }

        for(Trade i : Trade.tradeOffers) {
            i.timer += 1;
            if(i.timer >= 60*20) {
                i.sender.player.sendSystemMessage(new LiteralText(i.receiver.player.getName().getString() + " has not responded in time!").formatted(Formatting.RED), UUID.randomUUID());
                i.receiver.player.sendSystemMessage(new LiteralText("You did respond to " + i.sender.player.getName().getString() + "'s offer in time!").formatted(Formatting.RED), UUID.randomUUID());
                Trade.tradeOffers.remove(i);
                break;
            }
        }
    }
}
