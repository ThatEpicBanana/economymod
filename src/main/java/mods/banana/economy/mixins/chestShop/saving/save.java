
package mods.banana.economy.mixins.chestShop.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import mods.banana.economy.balance.Balance;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

//import mods.banana.economy.balance;

@Mixin(MinecraftServer.class)
public class save {
	@Inject(method = { "save" }, at = { @At("HEAD") })
	private void save(boolean suppressLogs, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> callbackInfo) {
		System.out.println("saving chestshops...");
		try {
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(ChestShop.class, new ChestShop.Serializer()).create();
//			Objects.requireNonNull(gson)

			//open file
			FileWriter file = new FileWriter("economy/chestshops.json");

			JsonArray chestShops = new JsonArray();

			for(ChestShop chestShop : ChestShop.chestShops) {
//				System.out.println(gson.toJson(chestShop));
				chestShops.add(parser.parse(gson.toJson(chestShop)));
			}

//			System.out.println(chestShops);

			//write json to file
			file.write(chestShops.toString());

			//close file
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("chestshops saved");
	}
}
