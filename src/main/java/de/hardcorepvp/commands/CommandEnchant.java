package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CommandEnchant implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(Messages.formatMessage(Messages.TOO_LESS_ARGUMENTS));
			return true;
		}

		if (args.length == 1) {
			if (player.getItemInHand() == null) {
				player.sendMessage(Messages.formatMessage("Du musst ein Item in der Hand halten"));
				return true;
			}
			ItemStack item = player.getItemInHand();
			if (args[0].equalsIgnoreCase("full")) {
				for (Enchantment enchantment : Enchantment.values()) {
					if (enchantment.canEnchantItem(item)) {
						if (!Arrays.asList(Utils.IGNOREENCHANTS).contains(enchantment)) {
							item.addEnchantment(enchantment, enchantment.getMaxLevel());
						}
					}
				}
				player.sendMessage("Dein Item ist nun komplett verzaubert!");
				return true;
			}
			player.sendMessage(Messages.formatMessage("Ungültige Parameter!"));
			return true;
		}

		if (args.length == 2) {

			if (player.getItemInHand() == null) {
				player.sendMessage(Messages.formatMessage("Du musst ein Item in der Hand halten"));
				return true;
			}
			ItemStack item = player.getItemInHand();
			Enchantment enchantment = Utils.getEnchFromString(args[0]);
			if (args[1].equalsIgnoreCase("max")) {
				item.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
				return true;
			}

			if (!player.hasPermission("system.unsafeenchant")) {
				player.sendMessage(Messages.NO_PERMISSIONS);
				return true;
			}
			int level = Integer.parseInt(args[1]);
			item.addUnsafeEnchantment(enchantment, level);

			player.sendMessage(Messages.formatMessage("Das Item wurde enchantet"));
			return true;
		}

		player.sendMessage(Messages.TOO_MANY_ARGUMENTS);
		return true;
	}


}
