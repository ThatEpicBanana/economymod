package mods.banana.economy.mixins.chestShop.creation;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class tick {
    @Shadow
    private int ticks;

    @Shadow
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Inject(method = { "tick" }, at = { @At("HEAD") })
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo callbackInfo) throws IOException {
        if(ticks == 1) onLoad();

        World overworld = worlds.get(worlds.keySet().stream().iterator().next());
        for(ChestShop chestShop : ChestShop.chestShops) {
            if(overworld.isChunkLoaded(chestShop.getBlockPos().getX()/16, chestShop.getBlockPos().getZ()/16)) {
                BlockState block = overworld.getBlockState(chestShop.getBlockPos());
                if(block == null || !block.isOf(Blocks.CHEST)) {
                    ChestShop.chestShops.remove(chestShop);
                    return;
                }
            }
        }
    }

    private void onLoad() throws IOException {
        JsonParser parser = new JsonParser();
        System.out.println("chest shops loading");

        //create file
        File chestShopsFile = new File("economy/chestShops.json");
        if (chestShopsFile.createNewFile()) {
            System.out.println("File created: " + chestShopsFile.getName());
        } else {
            System.out.println("File already exists.");
        }

        //check if file is empty
        if(chestShopsFile.length() == 0) {
            //write basic json
            FileWriter writer = new FileWriter(chestShopsFile);
            writer.write("[]");
            writer.close();
        }

        ChestShop.chestShops = new ArrayList<>();
        for(JsonElement jsonElement : parser.parse(new BufferedReader(new FileReader(chestShopsFile))).getAsJsonArray()) {
            ChestShop.chestShops.add(ChestShop.Serializer.deserialize(jsonElement, worlds));
        }
    }
}
