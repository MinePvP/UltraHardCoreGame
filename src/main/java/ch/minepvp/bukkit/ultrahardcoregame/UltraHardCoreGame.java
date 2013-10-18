package ch.minepvp.bukkit.ultrahardcoregame;

import ch.minepvp.bukkit.ultrahardcoregame.listener.UHCGListener;
import ch.minepvp.bukkit.ultrahardcoregame.task.NoobProtectionTask;
import ch.minepvp.bukkit.ultrahardcoregame.task.SpawnTask;
import ch.minepvp.bukkit.ultrahardcoregame.task.UpdateScoreboardTask;
import mondocommand.CallInfo;
import mondocommand.MondoCommand;
import mondocommand.MondoFailure;
import mondocommand.SubHandler;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class UltraHardCoreGame extends JavaPlugin {

    private static UltraHardCoreGame instance;

    private ArrayList<Team> teams = new ArrayList<Team>();

    MondoCommand game = new MondoCommand();
    MondoCommand team = new MondoCommand();

    private boolean active = false;
    private boolean noobProtection = false;

    @Override
    public void onLoad() {

        instance = this;

        // Config
        loadConfiguration();

        getLogger().info("loaded");
    }


    @Override
    public void onEnable() {

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().info("Error Submitting stats!");
            getLogger().log(Level.WARNING, e.getMessage());
        }

        // Commands
        setupGameCommands();
        if ( getConfig().getBoolean("Settings.Teams.Active") ) {
            setupTeamCommands();
        }

        // Listener
        new UHCGListener();

        setupRecipes();

        getLogger().info("enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("disabled");
    }

    public void loadConfiguration(){
        // Create Config defaults
        getConfig().options().copyDefaults(true);

        // Teams
        getConfig().addDefault("Settings.Teams.Active", true);
        getConfig().addDefault("Settings.Teams.FriendlyFire", false);

        // Spawn
        getConfig().addDefault("Settings.SaveSpawn.Generation", true);

        // Border
        getConfig().addDefault("Settings.Border.Generation", true);
        getConfig().addDefault("Settings.Border.Radius", 300);

        // Spawn
        getConfig().addDefault("Settings.Spawn.MinDistance", 50);
        getConfig().addDefault("Settings.Spawn.MinRadius", 160);
        getConfig().addDefault("Settings.Spawn.MaxRadius", 280);

        // NoobProtection
        getConfig().addDefault("Settings.NoobProtection.Active", true);
        getConfig().addDefault("Settings.NoobProtection.Time", 20);

        // Apple
        getConfig().addDefault("Settings.GoldenApple.WithIngots", true);

        // Melon
        getConfig().addDefault("Settings.SpeckledMelon.WithBlock", true);

        // Auto Sneak
        getConfig().addDefault("Settings.Sneak.Auto", true);

        // Compass
        getConfig().addDefault("Settings.Compass.ShowNearPlayers", true);
        getConfig().addDefault("Settings.Compass.MaxDistance", 50);
        getConfig().addDefault("Settings.Compass.Cooldown.Active", true);
        getConfig().addDefault("Settings.Compass.Cooldown.Time", 5);

        // Chests
        getConfig().addDefault("Settings.BonusChests.Active", true);
        getConfig().addDefault("Settings.BonusChests.MaxChests", 10);
        getConfig().addDefault("Settings.BonusChests.ChancePerChunk", 5);
        getConfig().addDefault("Settings.BonusChests.MaxItemsPerChest", 5);

        getConfig().addDefault("Settings.BonusChests.Items", getItems());

        // Player Death
        getConfig().addDefault("Settings.OnPlayerDeath.Lightning", true);

        // Spectator
        getConfig().addDefault("Settings.Spectator.Active", false);

        // World
        getConfig().addDefault("Settings.AfterGame.RemoveWorlds", true);

        // Server
        getConfig().addDefault("Settings.AfterGame.ShutdownServer", true);

        saveConfig();
    }

    public List<String> getItems() {

        List<String> items = new ArrayList<String>();

        // Food
        items.add( Material.GOLDEN_APPLE.name() );
        items.add( Material.BREAD.name() );
        items.add( Material.BAKED_POTATO.name() );
        items.add( Material.COOKED_BEEF.name() );
        items.add( Material.COOKED_CHICKEN.name() );
        items.add( Material.COOKED_FISH.name() );
        items.add( Material.CAKE.name() );

        // Tools
        items.add( Material.STONE_SWORD.name() );
        items.add( Material.STONE_AXE.name() );
        items.add( Material.STONE_SPADE.name() );
        items.add( Material.STONE_PICKAXE.name() );

        items.add( Material.IRON_SWORD.name() );
        items.add( Material.IRON_AXE.name() );
        items.add( Material.IRON_SPADE.name() );
        items.add( Material.IRON_PICKAXE.name() );

        // Armor
        items.add( Material.LEATHER_BOOTS.name() );
        items.add( Material.LEATHER_CHESTPLATE.name() );
        items.add( Material.LEATHER_HELMET.name() );
        items.add( Material.LEATHER_LEGGINGS.name() );

        items.add( Material.IRON_BOOTS.name() );
        items.add( Material.IRON_CHESTPLATE.name() );
        items.add( Material.IRON_HELMET.name() );
        items.add( Material.IRON_LEGGINGS.name() );

        // Stuff
        items.add( Material.BONE.name() );
        items.add( Material.COAL.name() );
        items.add( Material.GOLD_INGOT.name() );
        items.add( Material.WOOD.name() );
        items.add( Material.DIAMOND.name() );

        items.add( Material.COMPASS.name() );
        items.add( Material.BOW.name() );
        items.add( Material.ARROW.name() );
        items.add( Material.TORCH.name() );

        return items;
    }

    public void setupRecipes() {

        // Remove Recipes
        Iterator<Recipe> recipes = getServer().recipeIterator();
        while (recipes.hasNext()) {
            Recipe recipe = recipes.next();
            ItemStack result = recipe.getResult();

            if ( getConfig().getBoolean("Settings.GoldenApple.WithIngots") ) {

                if ( result.getTypeId() == Material.GOLDEN_APPLE.getId() ) {
                    recipes.remove();
                }

            }

            if ( getConfig().getBoolean("Settings.SpeckledMelon.WithIngots") ) {

                if ( result.getTypeId() == Material.SPECKLED_MELON.getId() ) {
                    recipes.remove();
                }

            }

        }

        // Add new Recipes
        if ( getConfig().getBoolean("Settings.GoldenApple.WithIngots") ) {

            // Add Golden Apple with Ingot
            ShapedRecipe recipe = new ShapedRecipe(new ItemStack( Material.GOLDEN_APPLE, 1))
                    .shape(new String[] { "III", "IAI", "III" })
                    .setIngredient('I', Material.GOLD_INGOT).setIngredient('A', Material.APPLE);

            getServer().addRecipe(recipe);

        }

        if ( getConfig().getBoolean("Settings.SpeckledMelon.WithBlock") ) {

            // Add Glistering Melon with Block
            ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack( Material.SPECKLED_MELON, 1))
                    .addIngredient(Material.GOLD_BLOCK)
                    .addIngredient(Material.MELON);

            getServer().addRecipe(recipe);

        }

    }

    private void setupGameCommands() {

        getCommand("game").setExecutor(game);

        game.addSub("start", "uhcg.game.start")
                .setDescription("Start the Game")
                .setHandler( new SubHandler() {
                    @Override
                    public void handle(CallInfo callInfo) throws MondoFailure {

                        setActive(true);

                        getServer().getWorld("world").setTime( 8 * 10000 );
                        new SpawnTask();

                        // Scoreboard
                        new UpdateScoreboardTask();

                        String noob = "";

                        if ( getConfig().getBoolean("Settings.NoobProtection.Active") ) {

                            int time = getConfig().getInt("Settings.NoobProtection.Time");

                            noob = " with Nooprotection for " + time + "min";

                            setNoobProtection(true);

                            new NoobProtectionTask(time);
                        }

                        getServer().broadcastMessage(ChatColor.GOLD + "The game Starts" + noob + "!");

                    }
                });

        game.addSub("stop", "uhcg.game.stop")
                .setDescription("Stop the Game")
                .setHandler( new SubHandler() {
                    @Override
                    public void handle(CallInfo callInfo) throws MondoFailure {

                        setActive(false);
                        removeTeams();

                        getServer().getScheduler().cancelTasks(instance);

                        Location location = getServer().getWorld("world").getSpawnLocation();
                        location.setY( location.getBlockY() + 2 );

                        for ( Player player : getServer().getOnlinePlayers() ) {
                            player.teleport( location, PlayerTeleportEvent.TeleportCause.COMMAND );
                        }

                    }
                });

        game.addSub("heal", "uhcg.game.heal")
                .setDescription("Heal all Players")
                .setHandler( new SubHandler() {
                    @Override
                    public void handle(CallInfo callInfo) throws MondoFailure {

                        for ( Player player : getServer().getOnlinePlayers() ) {
                            player.setHealth(20);
                            player.setFoodLevel(20);
                        }

                        callInfo.reply("All Players had now full Life and Food!");
                    }
                });

    }

    private void setupTeamCommands() {

        getCommand("team").setExecutor(team);

        team.addSub("list", "uhcg.team.list")
                .setDescription("Show all Teams")
                .setHandler( new SubHandler() {
                    @Override
                    public void handle(CallInfo callInfo) throws MondoFailure {

                        callInfo.reply("{BLUE}-----------------------------------------------------");
                        callInfo.reply("{WHITE}Team List");
                        callInfo.reply("{BLUE}-----------------------------------------------------");

                        if ( teams.size() < 1 ) {
                            callInfo.reply("{RED}There are no Teams yet!");
                        } else {

                            for ( Team team : teams ) {
                                callInfo.reply("-> %s", team.getMemberNames().toString() );
                            }

                        }

                        callInfo.reply("{BLUE}-----------------------------------------------------");

                    }
                });

        team.addSub("add", "uhcg.team.add")
                .setDescription("Add a Player to your Team")
                .setUsage("<name>")
                .setMinArgs(1)
                .setHandler( new SubHandler() {
                    @Override
                    public void handle(CallInfo callInfo) throws MondoFailure {

                        if ( isActive() ) {
                            callInfo.reply("{RED}You can't do this while the Game is running!");
                            return;
                        }

                        Team team = getTeamByPlayer( callInfo.getPlayer() );

                        Player player = getServer().getPlayer( callInfo.getArg(0) );

                        if ( player == null ) {
                            callInfo.reply("{RED}Can't find this Player!");
                            return;
                        }

                        if ( player.getName().equalsIgnoreCase( callInfo.getPlayer().getName() ) ) {
                            callInfo.reply("{RED}You can't add yourself!");
                            return;
                        }

                        if ( getTeamByPlayer( player ) != null ) {
                            callInfo.reply("{RED}The Player is already in a Team!");
                            return;
                        }

                        if ( team == null ) {
                            team = new Team();
                            team.addMember( callInfo.getPlayer() );

                            teams.add(team);
                        }

                        callInfo.reply("{GOLD}The Player is now in your Team!");
                        player.sendMessage(ChatColor.GOLD +"You are now in a Team with " + team.getMemberNames().toString() + " !" );

                        team.addMember(player);
                    }
                });

        team.addSub("remove", "uhcg.team.remove")
                .setDescription("Remove a Player from the Team")
                .setUsage("<name>")
                .setMinArgs(1)
                .setHandler( new SubHandler() {
                    @Override
                    public void handle(CallInfo callInfo) throws MondoFailure {

                        if ( isActive() ) {
                            callInfo.reply("{RED}You can't do this while the Game is running!");
                            return;
                        }

                        Team team = getTeamByPlayer( callInfo.getPlayer() );

                        if ( team == null ) {
                            callInfo.reply("{RED}You are not in a Team!");
                            return;
                        }

                        Player player = getServer().getPlayer( callInfo.getArg(0) );

                        if ( player == null ) {
                            callInfo.reply("{RED}Can't find this Player!");
                            return;
                        }

                        if ( getTeamByPlayer( player ) == null ) {
                            callInfo.reply("{RED}The Player is not in a Team!");
                            return;
                        }

                        if ( !team.equals( getTeamByPlayer( player ) ) ) {
                            callInfo.reply("{RED}The Player is not in the same Team!");
                            return;
                        }

                        team.removeMember(player);

                        callInfo.reply("{GOLD}The Player is removed from your Team!");
                        player.sendMessage(ChatColor.RED + "You was removed from the Team!");

                    }
                });

    }

    public void resetServer() {

        if ( getConfig().getBoolean("Settings.AfterGame.RemoveWorlds") ) {

            // kick all Players
            if ( getServer().getOnlinePlayers().length > 0 ) {

                for ( Player player : getServer().getOnlinePlayers() ) {

                    player.kickPlayer("Server shut down!");

                }

            }

            for ( World world : getServer().getWorlds() ) {

                getLogger().info("Unload " + world.getName());

                // unload Chunks
                for ( Chunk chunk : world.getLoadedChunks() ) {
                    chunk.unload(false, true);
                }

                getServer().unloadWorld(world, true);

                getLogger().info("Remove " + world.getName());
                deleteFolder(world.getWorldFolder());

            }

        }

        if ( getConfig().getBoolean("Settings.AfterGame.ShutdownServer") ) {
            getLogger().info("Shutdown Server");
            getServer().shutdown();
        }
    }

    private boolean deleteFolder(File file) {

        if (file.exists()) {

            if (file.isDirectory()) {

                for (File f : file.listFiles()) {

                    if (!deleteFolder(f)) {
                        return false;
                    }
                }
            }

            file.delete();
            return !file.exists();

        } else {
            return false;
        }

    }

    public static UltraHardCoreGame getInstance() {
        return instance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isNoobProtection() {
        return noobProtection;
    }

    public void setNoobProtection(boolean noobProtection) {
        this.noobProtection = noobProtection;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void addTeam( Team team ) {
        this.teams.add(team);
    }

    public void removeTeam( Team team ) {
        this.teams.remove(team);
    }

    public void removeTeams() {
        this.teams.clear();
    }

    public Team getTeamByPlayer( Player player ) {

        for ( Team team : teams ) {

            for ( Player member : team.getMembers() ) {

                if ( member.getName().equalsIgnoreCase( player.getName() ) ) {
                    return team;
                }

            }

        }

        return null;
    }

    public Boolean sameTeam( Player playerOne, Player playerTwo ) {

        Team teamOne = getTeamByPlayer(playerOne);
        Team teamTwo = getTeamByPlayer(playerTwo);

        if ( teamOne != null && teamTwo != null ) {

            if ( teamOne.getMemberNames().toString().equalsIgnoreCase( teamTwo.getMemberNames().toString() ) ) {
                return true;
            }

        }

        return false;
    }

}
