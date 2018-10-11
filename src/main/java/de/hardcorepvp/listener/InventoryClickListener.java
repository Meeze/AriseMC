package de.hardcorepvp.listener;

import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
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

		if (inventory.getName() == Utils.spawnerInv.getName()) {
			if (event.getSlot() == 2 && event.getClickedInventory().getName() == Utils.spawnerInv.getName()) {
				Utils.spawnerToChange.get(player).setSpawnedType(EntityType.PIG);
				Utils.spawnerToChange.get(player).update();
				Utils.spawnerToChange.remove(player);
				player.closeInventory();
				event.setCancelled(true);
			}
			if (event.getSlot() == 3 && event.getClickedInventory().getName() == Utils.spawnerInv.getName()) {
				Utils.spawnerToChange.get(player).setSpawnedType(EntityType.PIG_ZOMBIE);
				Utils.spawnerToChange.get(player).update();
				Utils.spawnerToChange.remove(player);
				player.closeInventory();
				event.setCancelled(true);
			}
			if (event.getSlot() == 5 && event.getClickedInventory().getName() == Utils.spawnerInv.getName()) {
				Utils.spawnerToChange.get(player).setSpawnedType(EntityType.CREEPER);
				Utils.spawnerToChange.get(player).update();
				Utils.spawnerToChange.remove(player);
				player.closeInventory();
				event.setCancelled(true);
			}
			if (event.getSlot() == 6 && event.getClickedInventory().getName() == Utils.spawnerInv.getName()) {
				Utils.spawnerToChange.get(player).setSpawnedType(EntityType.IRON_GOLEM);
				Utils.spawnerToChange.get(player).update();
				Utils.spawnerToChange.remove(player);
				player.closeInventory();
				event.setCancelled(true);
			}
			event.setCancelled(true);
		}
	}
}
