package de.hardcorepvp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

	@EventHandler
	public void onPing(ServerListPingEvent event) {
		event.setMaxPlayers(1337);
		event.setMotd("   §3§m-------§8§m[---§r §f§lAriseMC.de §8§m---]§3§m-------§r\n                  §c§lWartungsarbeiten");
	}
}
