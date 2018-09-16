package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		Main.getManager().removePlayer(player);
		Main.getPermissionManager().removeAttachment(player);
		Main.getUserManager().removeUser(uniqueId);
		Main.getClanManager().removeClanRequests(uniqueId);
		if (Main.getClanManager().hasClan(uniqueId)) {
			Clan clan = Main.getClanManager().getClan(uniqueId);
			clan.broadcast(player, player.getName() + " ist nun offline.");
		}
	}
}
