package ch.minepvp.bukkit.ultrahardcoregame.populator;

import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChestPopulator extends BlockPopulator {

    private UltraHardCoreGame plugin;

    private World world;
    private Integer radius;
    private Integer maxItems;

    private Integer counter;
    private Integer chestCounter = 0;

    private List<ItemStack> items;

    public ChestPopulator( World world ) {
        this.plugin = UltraHardCoreGame.getInstance();
        this.world = world;

        radius = plugin.getConfig().getInt("Settings.Border.Radius");
        maxItems = plugin.getConfig().getInt("Settings.BonusChests.MaxItemsPerChest");

        this.items = new ArrayList<ItemStack>();
        List<String> items = plugin.getConfig().getStringList("Settings.BonusChests.Items");

        // ItemStack
        for ( String item : items ) {

            try {
                this.items.add( new ItemStack( Material.getMaterial( item ) ) );
            } catch (NumberFormatException e) {
                plugin.getLogger().info("Error Item : " + item );
            }
        }


    }


    @Override
    public void populate(World world, Random random, Chunk chunk) {

        Location location = new Location(world, chunk.getX() * 16, 64, chunk.getZ() * 16);

        if ( location.distance( world.getSpawnLocation() ) >= radius ) {
            return;
        }

        if ( plugin.getConfig().getInt("Settings.BonusChests.MaxChests") <= chestCounter ) {
            return;
        }

        if ( plugin.getConfig().getInt("Settings.BonusChests.ChancePerChunk") <= random.nextInt(100) ) {
            return;
        }

        Block block = world.getHighestBlockAt( chunk.getX() * 16 + random.nextInt(16), chunk.getZ() * 16 + random.nextInt(16) );
        Block bottom = world.getBlockAt( block.getLocation().getBlockX(), block.getLocation().getBlockY() -1, block.getLocation().getBlockZ() );

        counter = 0;

        while ( !isOke(bottom) ) {

            if ( counter == 5 ) {
                return;
            }

            block = world.getHighestBlockAt( chunk.getX() * 16 + random.nextInt(16), chunk.getZ() * 16 + random.nextInt(16) );
            bottom = world.getBlockAt( block.getLocation().getBlockX(), block.getLocation().getBlockY() -1, block.getLocation().getBlockZ() );

            counter++;
        }

        block.setType(Material.CHEST);

        fillChest(block, random);
    }

    private Boolean isOke( Block block ) {

        if ( block.getType() == Material.WATER ||
             block.getType() == Material.WATER_LILY ||
             block.getType() == Material.STATIONARY_WATER ||
             block.getType() == Material.LAVA ||
             block.getType() == Material.STATIONARY_LAVA ||
             block.getType() == Material.CACTUS ||
             block.getType() == Material.FIRE ||
             block.getType() == Material.ICE ) {

            return false;
        }

        return true;
    }

    private void fillChest( Block block, Random random ) {

        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();

        ItemStack[] chestitems = new ItemStack[27];
        for (int i = 0; i < random.nextInt( maxItems ); i++) {
            chestitems[random.nextInt(27)] = this.items.get( random.nextInt( this.items.size() ) );
        }

        inventory.setContents(chestitems);

        chestCounter++;
        plugin.getLogger().info("Chest Nr." + chestCounter);
    }

}
