package mods.banana.economy.balance;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Balance {
    public static File balFile;
    public static JsonObject balJson;

    public static void setup() throws IOException {
        //create file
        balFile = new File("economy/balFile.json");
        if (balFile.createNewFile()) {
            System.out.println("File created: " + balFile.getName());
        } else {
            System.out.println("File already exists.");
        }

        //check if balFile is empty
        if(balFile.length() == 0) {
            //write basic json
            FileWriter writer = new FileWriter(balFile);
            writer.write("{}");
            writer.close();
        }

        JsonParser parser = new JsonParser();
        balJson = parser.parse(new BufferedReader(new FileReader(balFile))).getAsJsonObject();

    }

    //verifies if someone has mone
    public static void verifyPlayer(String player) {
        if(!balJson.has(player)) {
            System.out.println(player + " was not found, adding to database!");
            resetPlayer(player);
            System.out.println("balJson is now:");
            System.out.println(balJson);
        }
    }

    public static void resetPlayer(String player) {
        Gson gson = new Gson();

        Player playerJson = new Player();

        //playerJson.playerName = player;

        playerJson.bal = 10000;
        //trade and chest shop
        playerJson.moneySpent = 0;
        playerJson.moneyRecieved = 0;
        //give
        playerJson.moneyGiven = 0;
        playerJson.moneyDonated = 0;

        JsonParser parser = new JsonParser();
        balJson.add(player, parser.parse(gson.toJson(playerJson)));
    }

    public static void balAdd(String player, Long amount) {
        Gson gson = new Gson();
        verifyPlayer(player);
        Player playerJson = getPlayer(player);
        playerJson.bal += amount;
        setPlayer(player, playerJson);
    }

    public static void balSet(String player, Long amount) {
        Gson gson = new Gson();
        verifyPlayer(player);
        Player playerJson = getPlayer(player);
        playerJson.bal = amount;
        setPlayer(player, playerJson);
    }

    public static long balGet(String player) {
        Gson gson = new Gson();
        verifyPlayer(player);
        Player playerJson = getPlayer(player);
        return playerJson.bal;
    }

    public static Player getPlayer(String player) {
        //json related stuff
        Gson gson = new Gson();

        //get player json as string
        JsonElement playerElement = balJson.getAsJsonObject().get(player);

        //get player as player class
        return gson.fromJson(playerElement, Player.class);
    }

    private static void setPlayer(String playerName, Player player) {
        JsonParser parser = new JsonParser();
        Gson gson = new Gson();
        balJson.remove(playerName);
        balJson.add(playerName, parser.parse(gson.toJson(player)));
    }
}
