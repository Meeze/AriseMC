package de.hardcorepvp.manager;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.UserStats;
import de.hardcorepvp.model.Callback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager {

    private Map<Player, Scoreboard> scoreboardMap;

    public ScoreboardManager() {
        this.scoreboardMap = new ConcurrentHashMap<>();
    }

    public Map<Player, Scoreboard> getScoreboardMap() {
        return scoreboardMap;
    }

    public void addPlayer(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6Statistiken");
        objective.getScore(" §aKills ").setScore(4);
        objective.getScore("§f").setScore(3);
        objective.getScore(" §cDeaths ").setScore(2);
        objective.getScore("§f§f").setScore(1);

        Team killsTeam = scoreboard.registerNewTeam("kills");
        killsTeam.addEntry("§f");
        killsTeam.setSuffix("0");

        Team deathsTeam = scoreboard.registerNewTeam("deaths");
        deathsTeam.addEntry("§f§f");
        deathsTeam.setSuffix("0");

        Team adminTeam = scoreboard.registerNewTeam("AATeamAdmin");
        adminTeam.setAllowFriendlyFire(false);
        adminTeam.setCanSeeFriendlyInvisibles(true);
        adminTeam.setPrefix("§c");

        Team memberTeam = scoreboard.registerNewTeam("BBTeamMember");
        memberTeam.setAllowFriendlyFire(false);
        memberTeam.setCanSeeFriendlyInvisibles(false);
        memberTeam.setPrefix("§9");

        player.setScoreboard(scoreboard);
        Main.getStatsManager().getUserStats(player.getUniqueId(), new Callback<UserStats>() {
            @Override
            public void onResult(UserStats stats) {
                killsTeam.setSuffix(String.valueOf(stats.getKills()));
                deathsTeam.setSuffix(String.valueOf(stats.getDeaths()));
            }

            @Override
            public void onFailure(Throwable cause) {
                System.out.println("error scoreboard");
            }
        });
        this.scoreboardMap.put(player, scoreboard);
    }

    public void removePlayer(Player player) {
        this.scoreboardMap.remove(player);
        for (Player target : this.scoreboardMap.keySet()) {
            Scoreboard scoreboard = this.scoreboardMap.get(target);
            Team targetTeam = this.getTeam(player, scoreboard);
            targetTeam.removePlayer(player);
        }
    }

    private Team getTeam(Player player, Scoreboard scoreboard) {
        if (player.isOp()) {
            return scoreboard.getTeam("AATeamAdmin");
        }
        return scoreboard.getTeam("BBTeamMember");
    }

    public void updateTablist() {
        for (Player player : this.scoreboardMap.keySet()) {
            Scoreboard scoreboard = this.scoreboardMap.get(player);
            Team playerTeam = this.getTeam(player, scoreboard);
            playerTeam.addPlayer(player);
            for (Player target : this.scoreboardMap.keySet()) {
                Team targetTeam = this.getTeam(target, scoreboard);
                targetTeam.addPlayer(target);
            }
        }
    }
}
