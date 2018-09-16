package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHat implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.getInventory().setHelmet(player.getItemInHand());
			player.sendMessage(Messages.formatMessage("Du trägst nun einen Hut."));
			return true;
		}

		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;

	}

}
