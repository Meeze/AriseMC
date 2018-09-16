package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.clan.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		System.out.println("test");
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player player = (Player) event.getDamager();
			Player target = (Player) event.getEntity();

			System.out.println("test 1");
			if (Main.getClanManager().hasClan(player.getUniqueId()) && Main.getClanManager().hasClan(target.getUniqueId())) {
				Clan playerClan = Main.getClanManager().getClan(player.getUniqueId());
				Clan targetClan = Main.getClanManager().getClan(target.getUniqueId());
				System.out.println("test2");
				if (playerClan.equals(targetClan)) {
					player.sendMessage("§cDu bist mit §6" + target.getName() + " §cim gleichen Clan!");
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
