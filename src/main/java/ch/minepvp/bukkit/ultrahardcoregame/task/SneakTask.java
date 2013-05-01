package ch.minepvp.bukkit.ultrahardcoregame.task;

import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SneakTask extends BukkitRunnable {

    private Player player;

    public SneakTask( Player player ) {
        this.player = player;

        runTaskLater(UltraHardCoreGame.getInstance(), 0L);
    }

    @Override
    public void run() {
        player.setSneaking(true);
        cancel();
    }
}
