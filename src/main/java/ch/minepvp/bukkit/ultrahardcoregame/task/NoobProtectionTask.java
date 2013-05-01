package ch.minepvp.bukkit.ultrahardcoregame.task;

import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class NoobProtectionTask extends BukkitRunnable {

    private Integer counter;

    public NoobProtectionTask( Integer time ) {
        counter = time -1;

        runTaskTimerAsynchronously(UltraHardCoreGame.getInstance(), 60 * 20L, 60 * 20L);
    }

    @Override
    public void run() {

        counter--;

        if ( counter == 0 ) {
            UltraHardCoreGame.getInstance().setNoobProtection(false);
            UltraHardCoreGame.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "Now you can kill each other!");

            cancel();
        }
        else if ( counter == 1 || counter == 2 || counter == 5 || counter == 10 || counter == 15 || counter == 20 ) {
            UltraHardCoreGame.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "In "+ counter + "min is the Noobprotection off!");
        }

    }

}
