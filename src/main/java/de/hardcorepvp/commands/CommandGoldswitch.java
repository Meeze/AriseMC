package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CommandGoldswitch implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {

			Inventory inv = player.getInventory();
			int nuggets = 0;
			ItemStack[] invContents = inv.getContents();


			for (ItemStack item : invContents) {
				if (item != null) {
					if (item.getType() == Material.GOLD_NUGGET) {
						nuggets += item.getAmount();
						player.getInventory().remove(item);
					}
				}
			}
			if (nuggets / 9 > 0) {
				player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, nuggets / 9));
			}

			if (nuggets % 9 > 0) {
				player.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, nuggets % 9));
			}
			player.sendMessage("Deine " + nuggets + " Goldnuggets wurden in " + nuggets / 9 + " Ingots geswitched " + " und dir wird ein Rest von " + nuggets % 9 + " erstattet");
			return true;
		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}
}


