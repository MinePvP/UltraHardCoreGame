package ch.minepvp.bukkit.ultrahardcoregame.task;

import ch.minepvp.bukkit.ultrahardcoregame.Team;
import ch.minepvp.bukkit.ultrahardcoregame.UltraHardCoreGame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class UpdateScoreboardTask extends BukkitRunnable {

    private UltraHardCoreGame plugin;

    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private Objective objective;

    public UpdateScoreboardTask() {
        plugin = UltraHardCoreGame.getInstance();

        runTaskTimer(plugin, 20L, 100L);
    }

    @Override
    public void run() {

        if ( scoreboardManager == null ) {

            scoreboardManager = plugin.getServer().getScoreboardManager();
            scoreboard = this.scoreboardManager.getNewScoreboard();

            scoreboard.registerNewObjective("Player Health", "health");

            objective = scoreboard.getObjective("Player Health");
            objective.setDisplaySlot( DisplaySlot.SIDEBAR );

        }

        if ( plugin.getServer().getOnlinePlayers().length > 0 ) {

            for ( Player player : plugin.getServer().getOnlinePlayers() ) {
                scoreboard.resetScores( player );
            }


            for ( Team team : plugin.getTeams() ) {

                for ( Player player : team.getMembers() ) {

                    Score score = objective.getScore(player);
                    score.setScore( new Double( player.getHealth() ).intValue() );
                }

            }

            for ( Player player : plugin.getServer().getOnlinePlayers() ) {
                player.setScoreboard( scoreboard );
            }

        }

    }

}
