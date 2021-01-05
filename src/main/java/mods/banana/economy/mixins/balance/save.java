
package mods.banana.economy.mixins.balance;

import mods.banana.economy.balance.Balance;
import net.minecraft.server.MinecraftServer;

import java.io.FileWriter;
import java.io.IOException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//import mods.banana.economy.balance;

@Mixin(MinecraftServer.class)
public class save {
	@Inject(method = { "save" }, at = { @At("HEAD") })
	private void save(boolean suppressLogs, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> callbackInfo) {
		System.out.println("saving bal...");
		try {
			//open file
			FileWriter file = new FileWriter(Balance.balFile);

			//write json to file
			file.write(Balance.balJson.toString());

			//close file
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("balFile saved");
	}
}
