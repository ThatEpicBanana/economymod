package mods.banana.economy.balance.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mods.banana.economy.balance.Player;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

import static mods.banana.economy.balance.Balance.balJson;

public class balTopCommand {
    public static int execute(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity target) throws CommandSyntaxException {
        Gson gson = new Gson();

        List<Player> list = new ArrayList<>();

//        balJson.entrySet().iterator();

        for (Map.Entry<String, com.google.gson.JsonElement> stringJsonElementEntry : balJson.entrySet()) {
            String key = stringJsonElementEntry.getKey();
            Player player = gson.fromJson(balJson.get(key), Player.class);
            player.playerName = key;
            list.add(player);
        }

        list.sort(new Player.PlayerComparator());

        target.sendSystemMessage(new LiteralText("        - Baltop -").formatted(Formatting.YELLOW), UUID.randomUUID());

        for(int i = 0; i < Math.min(10, list.size()); i++) {
            JsonArray messageJson = new JsonArray();
            //add text (Example: 1. 100000¥ - Player)
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText((i + 1) + ((i == 9) ? ". " : ".  ")).formatted(Formatting.GRAY)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText(list.get(i).bal + "").formatted(Formatting.GREEN)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText("¥").formatted(Formatting.GRAY)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText(" - ").formatted(Formatting.DARK_GRAY)));
            messageJson.add(Text.Serializer.toJsonTree(new LiteralText(list.get(i).playerName).formatted(Formatting.GRAY)));
            //serialize
            Text message = Text.Serializer.fromJson(messageJson);

            target.sendSystemMessage(message, UUID.randomUUID());
        }

        return 0;
    }
}
