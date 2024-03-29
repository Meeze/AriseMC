package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStack implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;
		int stackedItems = Utils.stackItems(player, 64);

		if (stackedItems > 0) {
			player.sendMessage(Messages.formatMessage("Es wurden " + stackedItems + " Items gestacked!"));
			return true;
		}

		player.sendMessage(Messages.formatMessage("Es konnte nichts gestackt werden!"));
		return true;
	}
}




