package de.hardcorepvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inventory = event.getInventory();
		if (inventory == null) {
			return;
		}
		if (inventory.getType() == InventoryType.ENDER_CHEST && !inventory.toString().equals(player.getEnderChest().toString())) {
			Bukkit.broadcastMessage("Du kannst andere Echests nicht nutzen!");
			event.setCancelled(true);
		}
		if (inventory.getType() == InventoryType.PLAYER && !inventory.toString().equals(player.getInventory().toString())) {
			Bukkit.broadcastMessage("Du kannst andere Inventories nicht nutzen!");
			event.setCancelled(true);
		}
	}
}
