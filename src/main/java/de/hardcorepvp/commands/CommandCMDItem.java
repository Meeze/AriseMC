package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandCMDItem implements CommandExecutor {

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

		if (args.length == 1 && args[0].equalsIgnoreCase("#1")) {
			player.setItemInHand(Utils.getCommandItem(Utils.rankupMaterial, "§k" + Messages.RANKUP_CMD, Messages.RANKUP_BOOK));
			player.sendMessage("Du hast nun ein Rangupgrade Buch!");
			return true;
		}
		if (player.getItemInHand() == null) {
			player.sendMessage(Messages.formatMessage("Du musst ein Item in der Hand halten"));
			return true;
		}
		ItemStack item = player.getItemInHand();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; args.length > i; i++) {
			if (i != 0) {
				builder.append(args[i]).append(" ");
			}
		}
		String commandString = builder.toString();
		player.setItemInHand(Utils.getCommandItem(item.getType(), "§k" + builder.toString(), args[0]));
		player.sendMessage(Messages.formatMessage("Das Command Item mit dem Befehl " + commandString + " wurde created!"));
		return true;
	}
}
