package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAutosell implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			if (Main.getManager().isAutoselling(player)) {
				Main.getManager().removeAutoSellingPlayer(player);
				player.sendMessage("Autosell deaktiviert");
				return true;
			}
			Main.getManager().addAutoSellingPlayer(player);
			player.sendMessage("Autosell aktiviert");
			return true;
		}
		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("on")) {
				Main.getManager().addAutoSellingPlayer(player);
				player.sendMessage("Autosell aktiviert");
				return true;
			}
			if (args[0].equalsIgnoreCase("off")) {
				Main.getManager().removeAutoSellingPlayer(player);
				player.sendMessage("Autosell deaktiviert");
				return true;
			}

		}

		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

}
