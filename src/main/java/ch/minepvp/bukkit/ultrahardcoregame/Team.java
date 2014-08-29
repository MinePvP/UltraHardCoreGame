package ch.minepvp.bukkit.ultrahardcoregame;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {

    private ArrayList<Player> members = new ArrayList<Player>();

    public ArrayList<Player> getMembers() {
        return members;
    }

    public void addMember( Player player ) {
        this.members.add(player);
    }

    public void removeMember( Player player ) {
        this.members.remove(player);
    }

    public ArrayList<String> getMemberNames() {

        ArrayList<String> names = new ArrayList<String>();

        for ( Player player : members ) {
            names.add( player.getName() );
        }

        return names;
    }

    public void sendMessage(String message) {
        for ( Player player : members ) {
            player.sendMessage(message);
        }
    }
}
