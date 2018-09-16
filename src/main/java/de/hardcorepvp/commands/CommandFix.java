package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFix implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		int fixedItems = Utils.fixItems(player, true);
		if (fixedItems > 0) {
			player.sendMessage(Messages.formatMessage(fixedItems + " Items wurden repariert."));
			return true;
		}
		player.sendMessage(Messages.formatMessage("Da hattest keine Items zum reparieren!"));
		return true;

	}

}
