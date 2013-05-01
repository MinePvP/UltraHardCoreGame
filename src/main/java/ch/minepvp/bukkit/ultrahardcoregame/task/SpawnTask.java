package ch.minepvp.bukkit.ultrahardcoregame.task;

import ch.minepvp.bukkit.ultrahardcoregame.Team;
import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class SpawnTask extends BukkitRunnable {

    private Integer minDistance = UltraHardCoreGame.getInstance().getConfig().getInt("Settings.Spawn.MinDistance");
    private Integer minRadius = UltraHardCoreGame.getInstance().getConfig().getInt("Settings.Spawn.MinRadius");
    private Integer maxRadius = UltraHardCoreGame.getInstance().getConfig().getInt("Settings.Spawn.MaxRadius");

    private World world = UltraHardCoreGame.getInstance().getServer().getWorld("world");
    private Location spawn = UltraHardCoreGame.getInstance().getServer().getWorld("world").getSpawnLocation();

    private ArrayList<Location> teamSpawns = new ArrayList<Location>();

    public SpawnTask() {
        runTask(UltraHardCoreGame.getInstance());
    }

    @Override
    public void run() {

        for ( Player player : UltraHardCoreGame.getInstance().getServer().getOnlinePlayers() ) {

            if ( UltraHardCoreGame.getInstance().getTeamByPlayer( player ) == null ) {
                Team team = new Team();
                team.addMember(player);

                UltraHardCoreGame.getInstance().addTeam(team);
            }

        }

        for ( Team team : UltraHardCoreGame.getInstance().getTeams() ) {

            // Get Random Spawn Point
            Location location = calcTeamSpawn();


            for ( Player player : team.getMembers() ) {

                player.setGameMode( GameMode.SURVIVAL );
                player.getInventory().clear();

                player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);

                player.setFoodLevel(20);
                player.setHealth(20);
            }

        }

        cancel();

    }

    private Location calcTeamSpawn() {

        Location location = new Location(world, 20000, 70, 200000);
        Material below = Material.STATIONARY_WATER;

        while ( !isOke(location, below) ) {

            location.setX( spawn.getX() + Math.random() * maxRadius * 2 - maxRadius);
            location.setZ( spawn.getZ() + Math.random() * maxRadius * 2 - maxRadius);

            boolean gen = world.loadChunk( location.getChunk().getX(), location.getChunk().getZ(), true );

            while ( !gen ) {

                gen = location.getChunk().isLoaded();

            }

            location.setY( world.getHighestBlockAt(location.getBlockX(), location.getBlockZ() ).getY() );
            below = world.getBlockAt( location.getBlockX(), location.getBlockY() - 1, location.getBlockZ() ).getType();
        }

        location.setY( location.getY() + 2 );
        teamSpawns.add(location);

        return location;
    }

    private boolean isOke( Location location, Material material ) {

        if ( !inOrbit(spawn, location) ) {
            return false;
        }
        else if ( inTeamSpawns(location) ) {
            return false;
        }
        else if ( inNearOfTeamSpawn(location) ) {
            return false;
        }
        else if ( !isSave( material ) ) {
            return false;
        }

        return true;
    }

    private boolean isSave( Material material) {

        if ( material == Material.WATER ||
             material == Material.WATER_LILY ||
             material == Material.STATIONARY_WATER ||
             material == Material.LAVA ||
             material == Material.STATIONARY_LAVA ||
             material == Material.CACTUS ||
             material == Material.FIRE ) {

            return false;
        }

        return true;
    }

    private boolean inTeamSpawns( Location location ) {

        for ( Location teamSpawn : teamSpawns ) {

            if ( teamSpawn.equals( location ) ) {
                return true;
            }

        }

        return false;
    }

    private boolean inNearOfTeamSpawn( Location location ) {

        for ( Location teamSpawn : teamSpawns ) {

            if ( location.distance( teamSpawn ) < minDistance ) {
                return true;
            }

        }

        return false;
    }

    private boolean inOrbit( Location spawn, Location location ) {

        if ( distance(spawn, location) > minRadius && distance(spawn, location) < maxRadius) {
            return true;
        }

        return false;
    }

    private double distance(Location a, Location b) {
        return a.distance(b);
    }

}
