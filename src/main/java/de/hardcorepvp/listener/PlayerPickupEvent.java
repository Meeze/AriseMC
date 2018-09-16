package de.hardcorepvp.listener;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerPickupEvent implements Listener {

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		ItemStack itemstack = item.getItemStack();
		if (Main.getManager().isAutoselling(player)) {

			//TODO SELLITEMS
			player.sendMessage("Item verkauft!");
			event.getItem().setItemStack(new ItemStack(Material.AIR));

		}
		if (Main.getManager().isItemfiltered(player)) {
			if (Utils.toFilter.contains(itemstack.getType())) {
				event.setCancelled(true);

			}
		}
	}
}
