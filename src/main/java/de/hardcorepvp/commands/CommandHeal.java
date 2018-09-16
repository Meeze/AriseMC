package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHeal implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {

			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.setSaturation(20);
			Utils.removeNegativePotions(player);

			player.sendMessage(Messages.formatMessage("Du wurdest geheilt."));
			return true;
		}

		if (args.length == 1) {

			if (!Bukkit.getPlayer(args[0]).isOnline()) {
				player.sendMessage(Messages.formatMessage(Messages.PLAYER_NOT_FOUND));
				return true;
			}

			Player target = Bukkit.getPlayer(args[0]);

			target.setHealth(20.0);
			target.setFoodLevel(20);
			target.setSaturation(20);
			Utils.removeNegativePotions(target);


			target.sendMessage(Messages.formatMessage("Du wurdest geheilt."));
			player.sendMessage(Messages.formatMessage("Du hast " + target.getName() + " geheilt."));
			return true;
		}

		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;

	}

}
