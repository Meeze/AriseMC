package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEnderchest implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			player.openInventory(player.getEnderChest());
			return true;
		}
		if (args.length == 1) {
			if (Bukkit.getPlayer(args[0]) == null) {
				player.sendMessage(Messages.formatMessage(Messages.PLAYER_NOT_FOUND));
				return true;
			}

			Player target = Bukkit.getPlayer(args[0]);
			player.openInventory(target.getEnderChest());
			player.sendMessage(Messages.formatMessage("Du hast die Enderchest von " + target.getName() + " geöffnet."));
			return true;
		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}
}
