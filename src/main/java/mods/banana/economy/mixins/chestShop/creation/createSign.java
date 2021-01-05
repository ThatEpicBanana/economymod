package mods.banana.economy.mixins.chestShop.creation;

import mods.banana.economy.chestShop.ChestShop;
import net.minecraft.block.*;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(ServerPlayNetworkHandler.class)
public class createSign {
    @Final
    @Shadow
    private MinecraftServer server;

    @Shadow public ServerPlayerEntity player;

    //random method name but i still found it KEKW
    @Inject(method = { "method_31282" }, at = { @At("HEAD") })
    private void createSign(UpdateSignC2SPacket updateSignC2SPacket, List<String> list, CallbackInfo callbackInfo) {
        BlockPos blockPos = updateSignC2SPacket.getPos();

        if(!(
                list.get(1).matches("\\d+") &&
                        ChestShop.getTypeFromString(list.get(2)) != null
        )) return;

        //get chest position
        @Nullable BlockPos chestPos = checkSign(blockPos);

//        System.out.println("CREATED SIGN AAAAAAAAAAAAAAAAAAAA");

        //validate entity name
        if(list.get(0).equals("")) list.set(0, player.getEntityName());
        //validate item name
        if(list.get(3).equals("")) {
            ChestBlockEntity chest = (ChestBlockEntity) player.getServerWorld().getBlockEntity(chestPos);
            for(int i = 0; i < 27; i++)
                if(!chest.getStack(i).isEmpty()) {
                    list.set(3, Registry.ITEM.getId(chest.getStack(i).getItem()).getPath());
                    break;
                }
        }

        //validate sign
        if(!ChestShop.validSign(list, player)) return;

//        System.out.println("valid sign");

        //add chest shop to list if sign is connected to chest
        if(chestPos != null) ChestShop.chestShops.add(ChestShop.fromSign(blockPos, player.getServerWorld().getBlockState(chestPos), chestPos, player.getServerWorld(), list));
//        System.out.println(ChestShop.fromSign(blockPos, player.getServerWorld().getBlockState(chestPos), chestPos, player.getServerWorld(), list));
//        player.getServerWorld().getBlockState(blockPos)
    }

    private @Nullable BlockPos checkSign(BlockPos blockPos) {
        World overworld = server.getWorlds().iterator().next();

        //check x-axis sides for chests
        for(int i = -1; i < 2; i += 2) {
            if (overworld.getBlockState(blockPos.add(i, 0, 0)).isOf(Blocks.CHEST)) {
                System.out.println("success");
                return blockPos.add(i, 0, 0);
            }
        }

        //check y-axis sides for chests
        for(int i = -1; i < 2; i += 2) {
            if (overworld.getBlockState(blockPos.add(0, i, 0)).isOf(Blocks.CHEST)) {
                System.out.println("success");
                return blockPos.add(0, i, 0);
            }
        }

        //check z-axis sides for chests
        for(int i = -1; i < 2; i += 2) {
            if (overworld.getBlockState(blockPos.add(0, 0, i)).isOf(Blocks.CHEST)) {
                System.out.println("success");
                return blockPos.add(0, 0, i);
            }
        }

        return null;
    }
}
