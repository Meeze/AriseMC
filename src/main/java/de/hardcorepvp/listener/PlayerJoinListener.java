package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import de.hardcorepvp.data.UserHomes;
import de.hardcorepvp.data.UserStats;
import de.hardcorepvp.model.Callback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();
        Bukkit.broadcastMessage("test");
        event.setJoinMessage(null);
        Main.getPermissionManager().addAttachment(player);

        if (Main.getClanManager().hasClan(uniqueId)) {
            Clan clan = Main.getClanManager().getClan(uniqueId);
            clan.broadcast(player, player.getName() + " ist nun online.");
        }
        Main.getStatsManager().getUserStats(uniqueId, new Callback<UserStats>() {
            @Override
            public void onResult(UserStats stats) {
                player.sendMessage("Kills: " + stats.getKills());
                player.sendMessage("Deaths: " + stats.getDeaths());
                Main.getHomeManager().getUserHomes(uniqueId, new Callback<UserHomes>() {
                    @Override
                    public void onResult(UserHomes homes) {
                        player.sendMessage("Homes size " + homes.getHomes().size());
                        Main.getHomeManager().createHome(homes, "test", player.getLocation());
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        System.out.println(cause);
                    }
                });
            }

            @Override
            public void onFailure(Throwable cause) {
                System.out.println(cause);
            }
        });
    }
}
