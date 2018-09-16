package de.hardcorepvp.listener;

import de.hardcorepvp.model.Callback;
import de.hardcorepvp.model.Excavator;
import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (event.getBlock().getType() == Utils.excavatorMaterial) {
			if (player.getItemInHand() != null) {
				ItemStack item = player.getItemInHand();
				if (item.hasItemMeta()) {
					if (item.getItemMeta().getDisplayName().substring(2).equalsIgnoreCase(Messages.EXCAVATOR_BLOCK.substring(2))) {
						if (item.getItemMeta().hasEnchant(Utils.uniqueEnchant)) {
							String radiusString = item.getItemMeta().getLore().get(0).substring(Messages.EXCAVATOR_RADIUS.length());
							int radius = Integer.parseInt(radiusString);
							Utils.destroyCube(event.getBlock().getLocation(), radius, new Callback<Excavator>() {
								@Override
								public void onResult(Excavator excavator) {
									excavator.start();
								}

								@Override
								public void onFailure(Throwable cause) {
									player.sendMessage("§cBeim setzen des Excavator Blocks ist ein Fehler aufgetreten!");
								}
							});
						}
					}
				}
			}
		}
	}
}
