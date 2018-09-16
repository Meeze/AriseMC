package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPvP implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(Messages.formatMessage("PvP ist " + Main.getManager().isPvP()));
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("on")) {
				Main.getManager().setPvP(true);
				player.sendMessage(Messages.formatMessage("Das PvP wurde aktiviert"));
				return true;
			}
			if (args[0].equalsIgnoreCase("off")) {
				Main.getManager().setPvP(false);
				player.sendMessage(Messages.formatMessage("Das PvP wurde deaktiviert"));
				return true;
			}
			if (args[0].equalsIgnoreCase("toggle")) {
				if (Main.getManager().isPvP()) {
					Main.getManager().setPvP(false);
					player.sendMessage(Messages.formatMessage("Das PvP wurde deaktiviert"));
					return true;
				}
				Main.getManager().setPvP(true);
				player.sendMessage(Messages.formatMessage("Das PvP wurde aktiviert"));
				return true;
			}
			player.sendMessage(Messages.formatMessage(Messages.WRONG_ARGUMENTS));
			return true;
		}
		player.sendMessage(Messages.TOO_MANY_ARGUMENTS);
		return true;
	}
}
