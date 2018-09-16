package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExcavator implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.getInventory().addItem(Utils.getExcavatorBlock(5));
			return true;
		}
		if (args.length == 1) {
			try {
				int number = Integer.parseInt(args[0]);
				player.getInventory().addItem(Utils.getExcavatorBlock(number));
				return true;
			} catch (NumberFormatException exception) {
				player.sendMessage("Nummer angeben, kek");
				return true;
			}
		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

}
