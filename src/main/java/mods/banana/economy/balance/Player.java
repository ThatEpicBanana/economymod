package mods.banana.economy.balance;

import java.util.Comparator;

public class Player {
    public String playerName;

    public long bal;
    public long moneyGiven;
    public long moneyDonated;
    public long moneySpent;
    public long moneyRecieved;

    public long getBal() {
        return this.bal;
    }

    public void setBal(long bal) {
        this.bal = bal;
    }

    public static class PlayerComparator implements Comparator<Player> {
        @Override
        public int compare(Player o1, Player o2) {
            return (int)(o2.getBal() - o1.getBal());
        }
    }
}