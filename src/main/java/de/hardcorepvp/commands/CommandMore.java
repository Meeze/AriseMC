package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandMore implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (player.getItemInHand() == null) {
			player.sendMessage(Messages.formatMessage("Du musst ein Item in der Hand halten"));
			return true;
		}


		if (args.length == 0) {
			ItemStack item = player.getItemInHand();
			item.setAmount(64);
			player.sendMessage(Messages.formatMessage("Das Item wurde vervollständigt."));
			return true;
		}
		if (args.length == 1) {
			ItemStack item = player.getItemInHand();
			try {
				int amount = Integer.parseInt(args[0]);
				item.setAmount(amount);
			} catch (NumberFormatException e) {
				player.sendMessage("Bitte gebe eine gültige Zahl an!");
			}

			player.sendMessage(Messages.formatMessage("Das Item wurde vervollständigt."));
			return true;
		}

		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

}