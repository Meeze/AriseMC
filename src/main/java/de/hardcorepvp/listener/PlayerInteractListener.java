package de.hardcorepvp.listener;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (player.getItemInHand() != null) {
				ItemStack item = player.getItemInHand();
				if (item.hasItemMeta()) {
					if (item.getItemMeta().getDisplayName().contains(Messages.CMD_ITEM_PREFIX.substring(2))) {
						if (item.getItemMeta().hasEnchant(Utils.uniqueEnchant)) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), item.getItemMeta().getLore().get(0).substring(2).replace("%p%", player.getName()));
							if (player.getItemInHand().getAmount() == 1) {
								player.setItemInHand(null);
							} else {
								item.setAmount(item.getAmount() - 1);
							}
							event.setCancelled(true);
						} else {
							player.sendMessage("Netter Versuch :)");
							player.setItemInHand(null);
						}
					}
				}
			}
		}
	}
}


