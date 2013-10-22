package ch.minepvp.bukkit.ultrahardcoregame.listener;

import ch.minepvp.bukkit.ultrahardcoregame.Team;
import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import ch.minepvp.bukkit.ultrahardcoregame.populator.BorderPopulator;
import ch.minepvp.bukkit.ultrahardcoregame.populator.ChestPopulator;
import ch.minepvp.bukkit.ultrahardcoregame.task.CompassCooldownTask;
import ch.minepvp.bukkit.ultrahardcoregame.task.SneakTask;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class UHCGListener implements Listener {

    private UltraHardCoreGame plugin = UltraHardCoreGame.getInstance();


    public UHCGListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {

        if ( plugin.isActive() && plugin.getTeamByPlayer( event.getPlayer() ) == null ) {

            if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {
                return;
            }

            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Game is already Started! Try it again later.");
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
     public void onPlayerJoin(PlayerJoinEvent event) {

        if ( plugin.isActive() && plugin.getTeamByPlayer( event.getPlayer() ) == null ) {

            if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {

                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false) );
                event.getPlayer().setGameMode(GameMode.CREATIVE);
                event.getPlayer().sendMessage(ChatColor.GOLD + "You joined the Game as Spectator!");
                return;
            }

        }

        if ( plugin.getConfig().getBoolean("Settings.Sneak.Auto") ) {
            event.getPlayer().setSneaking(true);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        if ( plugin.isActive() && plugin.getTeamByPlayer( event.getPlayer() ) == null ) {

            if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {
                event.setCancelled(true);
            }

        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {

        if ( plugin.isActive() && plugin.getTeamByPlayer( event.getPlayer() ) == null ) {

            if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {
                event.setCancelled(true);
            }

        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {

        if ( plugin.getConfig().getBoolean("Settings.Sneak.Auto") ) {

            event.getPlayer().setSneaking(true);
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {

            if ( plugin.getTeamByPlayer( event.getPlayer() ) == null ) {
                event.setCancelled(true);
                return;
            }

        }

        if ( plugin.getConfig().getBoolean("Settings.Sneak.Auto") ) {
            event.getPlayer().setSneaking(false);
            new SneakTask( event.getPlayer());
        }

        if ( !plugin.getConfig().getBoolean("Settings.Compass.ShowNearPlayers") ) {
            return;
        }

        if ( event.getMaterial() != Material.COMPASS ) {
            return;
        }

        if ( event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK ) {
            return;
        }

        if ( plugin.getConfig().getBoolean("Settings.Compass.Cooldown.Active") ) {

            if ( event.getPlayer().getCompassTarget().distance( event.getPlayer().getWorld().getSpawnLocation() ) != 0 ) {
                event.getPlayer().sendMessage(ChatColor.RED + "You need to Wait!");
                return;
            }

        }

        int distance = plugin.getConfig().getInt("Settings.Compass.MaxDistance");

        List<Entity> nearbyEntities = event.getPlayer().getNearbyEntities(distance, distance, distance);

        if ( nearbyEntities.size() > 0 ) {

            for ( Entity entity : nearbyEntities ) {

                if ( entity instanceof Player ) {

                    if ( !plugin.sameTeam( event.getPlayer(), (Player) entity )  ) {

                        new CompassCooldownTask( event.getPlayer() );

                        event.getPlayer().setCompassTarget( entity.getLocation() );
                        event.getPlayer().sendMessage(ChatColor.GOLD + "Player found and set his last Location as Target!");
                        return;
                    }

                }

            }

        }

        event.getPlayer().setCompassTarget( event.getPlayer().getWorld().getSpawnLocation() );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if ( event.getEntity() instanceof Player ) {

            Player toPlayer = (Player) event.getEntity();

            if ( event.getDamager() instanceof Player ) {

                if ( !plugin.isActive() ) {
                    event.setCancelled(true);
                    return;
                }

                Player fromPlayer = (Player) event.getDamager();

                if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {

                    if ( plugin.getTeamByPlayer( fromPlayer ) == null ) {
                        event.setCancelled(true);
                        return;
                    }

                }

                if ( plugin.getConfig().getBoolean("Settings.Teams.FriendlyFire") ) {

                    if ( plugin.sameTeam( toPlayer, fromPlayer ) ) {

                        fromPlayer.sendMessage(ChatColor.RED + "You can't attack a Team Member!");
                        event.setCancelled(true);
                        return;
                    }

                }

                if ( plugin.isNoobProtection() ) {

                    fromPlayer.sendMessage(ChatColor.RED + "Noob Protection is still on you need to wait!");
                    event.setCancelled(true);
                    return;

                }

            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {

        if ( event.getEntity() instanceof Player) {

            if ( plugin.getConfig().getBoolean("Settings.Regain.Normal") ) {
                return;
            }

            if ( plugin.isActive() && (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN ||
                    event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) ) {

                event.setCancelled(true);
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {

        if ( !plugin.isActive() ) {
            return;
        }

        Team team = plugin.getTeamByPlayer( event.getEntity() );

        if ( team.getMembers().size() > 1 ) {
            team.removeMember( event.getEntity() );
        } else {
            plugin.removeTeam(team);
        }

        if ( plugin.getConfig().getBoolean("Settings.OnPlayerDeath.Lightning") ) {
            event.getEntity().getWorld().strikeLightningEffect( event.getEntity().getLocation() );
        }

        if ( plugin.getConfig().getBoolean("Settings.Spectator.Active") ) {

            event.getEntity().sendMessage(ChatColor.RED + "You lost!");
            event.getEntity().teleport( plugin.getServer().getWorld("world").getSpawnLocation() );
            event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false) );
            event.getEntity().setGameMode(GameMode.CREATIVE);
            event.getEntity().sendMessage(ChatColor.GOLD + "You are now a Spectator!");

        } else {
            event.getEntity().kickPlayer("You lost!");
        }

        if ( plugin.getTeams().size() == 1 ) {

            for ( Player player : plugin.getTeams().get(0).getMembers() ) {

                if (  plugin.getConfig().getBoolean("Settings.AfterGame.RemoveWorlds") ) {
                    player.kickPlayer("You are the Winner!");
                } else {
                    player.sendMessage(ChatColor.GOLD +"You are the Winner");
                }

            }

            plugin.resetServer();
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldInitEvent event) {

        plugin.getLogger().info("WorldInit : " + event.getWorld().getName());

        attachPopulators(event.getWorld());

    }

    public void attachPopulators( World world ) {

        // World Border
        if ( plugin.getConfig().getBoolean("Settings.Border.Generation") ) {

            if ( world.getName().equalsIgnoreCase("world") || world.getName().equalsIgnoreCase("world_nether") ) {
                world.getPopulators().add( new BorderPopulator(world) );
            }

        }

        // Chest
        if ( plugin.getConfig().getBoolean("Settings.BonusChests.Active") ) {

            if ( world.getName().equalsIgnoreCase("world") ) {
                world.getPopulators().add( new ChestPopulator(world) );
            }

        }

        // Generate Spawn
        if ( plugin.getConfig().getBoolean("Settings.SaveSpawn.Generation") ) {

            if ( world.getName().equalsIgnoreCase("world") ) {
                generateSaveSpawn(world);
            }

        }

    }

    public void generateSaveSpawn( World world ) {

        plugin.getLogger().info("Generate Save Spawn");

        Location location = world.getSpawnLocation();

        location.setY( world.getMaxHeight() - 30 );

        world.setSpawnLocation( location.getBlockX(), location.getBlockY(), location.getBlockZ() );

        // Generate Ground
        for ( int x = -10; x <= 10; x++ ) {

            for ( int z = -10; z <= 10; z++ ) {
                world.getBlockAt( location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z ).setType( Material.BEDROCK );

                if ( x == -10 || x == 10 || z == -10 || z == 10 ) {

                    for ( int y = 1; y <= 4; y++ ) {

                        if ( y == 4 ) {
                            world.getBlockAt( location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z ).setType(Material.GLOWSTONE);
                        } else {
                            world.getBlockAt( location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z ).setType(Material.BEDROCK);
                        }

                    }

                }

            }

        }

    }

}
