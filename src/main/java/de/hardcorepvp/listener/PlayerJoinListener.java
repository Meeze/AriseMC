package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import de.hardcorepvp.data.User;
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
		Main.getUserManager().addUser(uniqueId);
		Main.getPermissionManager().addAttachment(player);

		if (Main.getClanManager().hasClan(uniqueId)) {
			Clan clan = Main.getClanManager().getClan(uniqueId);
			clan.broadcast(player, player.getName() + " ist nun online.");
		}

		User user = Main.getUserManager().getUser(uniqueId);
		user.addReadyExecutor(new Runnable() {
			@Override
			public void run() {
				Main.getPermissionManager().addPermissions(player, user.getGroup().getPermissions());
				if (!player.hasPlayedBefore()) {
					user.setMoney(5231123);
				}
				user.setMoney(21231);
				user.setDeaths(Integer.MAX_VALUE);
				user.setKills(Integer.MAX_VALUE);
				user.addHome("test", player.getLocation());

				player.sendMessage("Group: " + user.getGroup().getName());
				player.sendMessage("Clan: " + (Main.getClanManager().getClan(uniqueId) == null ? "Kein Clan" : "Hat einen Clan"));
				player.sendMessage("Homes: " + user.getHomes().size());
				player.sendMessage("Money: " + user.getMoney());
				player.sendMessage("Kills: " + user.getKills());
				player.sendMessage("Deaths: " + user.getDeaths());
				player.sendMessage("K/D: " + user.getKD());
				player.sendMessage("Rank: " + Main.getRankingManager().getUserRank(uniqueId));
			}
		});
	}
}
