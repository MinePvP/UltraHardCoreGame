package ch.minepvp.bukkit.ultrahardcoregame.populator;

import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class BorderPopulator extends BlockPopulator {

    private UltraHardCoreGame plugin;

    private World world;
    private Integer radius;

    public BorderPopulator( World world ) {
        this.plugin = UltraHardCoreGame.getInstance();
        this.world = world;

        radius =  plugin.getConfig().getInt("Settings.Border.Radius");

        if ( world.getEnvironment() == World.Environment.NETHER ) {
            radius /= 5;
        }

    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {

        int cx = chunk.getX() * 16;
        int cz = chunk.getZ() * 16;

        for ( int x = 0; x < 16; x++ ) {

            for ( int z = 0; z < 16; z++ ) {

                Location location = new Location( world, cx + x, 0, cz + z );

                if ( location.distance( world.getSpawnLocation() ) > radius &&
                     location.distance( world.getSpawnLocation() ) < radius + 2 ) {

                    placeWall(location);

                }

            }

        }

    }

    private void placeWall( Location location ) {

        for (int y = 2 ; y < world.getMaxHeight(); y++) {
            world.getBlockAt( location.getBlockX(), y, location.getBlockZ() ).setType( Material.BEDROCK );
        }

    }

}
