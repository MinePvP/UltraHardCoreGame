package ch.minepvp.bukkit.ultrahardcoregame.task;

import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassCooldownTask extends BukkitRunnable {

    private UltraHardCoreGame plugin;
    private Player player;

    public CompassCooldownTask( Player player ) {
        plugin = UltraHardCoreGame.getInstance();
        this.player = player;

        int time = 60 * plugin.getConfig().getInt("Settings.Compass.Cooldown.Time");

        runTaskLater(plugin, time * 20L);
    }

    @Override
    public void run() {
        player.setCompassTarget( player.getWorld().getSpawnLocation() );
        player.sendMessage(ChatColor.GOLD + "Now you can use your Compass again!");
        cancel();
    }
}
