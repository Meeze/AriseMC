package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFeed implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {

			player.setFoodLevel(20);
			player.setSaturation(20);
			player.sendMessage(Messages.formatMessage("Deine Hungerbalken wurde aufgefüllt."));

		}
		if (args.length == 1) {
			if (Bukkit.getPlayer(args[0]).isOnline()) {
				player.sendMessage(Messages.formatMessage(Messages.PLAYER_NOT_FOUND));
				return true;
			}

			Player target = Bukkit.getPlayer(args[0]);
			target.setFoodLevel(20);
			target.setSaturation(20);
			target.sendMessage(Messages.formatMessage("Deine Hungerbalken wurde aufgefüllt."));

			player.sendMessage(Messages.formatMessage("Du hast die Hungerbalken von " + target.getName() + " aufgefüllt."));
			return true;
		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;

	}
}
