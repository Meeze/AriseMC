package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class CommandNear implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			LinkedHashMap<Player, Double> nearPlayers = new LinkedHashMap<>();
			for (Player allPlayer : Bukkit.getOnlinePlayers()) {
				if (player.getLocation().distanceSquared(allPlayer.getLocation()) < 2500 && player != allPlayer) {
					nearPlayers.put(allPlayer, (double) Math.round(Math.sqrt(player.getLocation().distanceSquared(allPlayer.getLocation()))));
				}
			}
			player.sendMessage(nearPlayers.toString());
			return true;
		}
		// TODO PERMISSIONS
		if (args.length == 1) {
			LinkedHashMap<Player, Double> nearPlayers = new LinkedHashMap<>();
			for (Player allPlayer : Bukkit.getOnlinePlayers()) {
				if (player.getLocation().distanceSquared(allPlayer.getLocation()) < Double.valueOf(args[0]) * Double.valueOf(args[0]) && player != allPlayer) {
					nearPlayers.put(allPlayer, (double) Math.round(Math.sqrt(player.getLocation().distanceSquared(allPlayer.getLocation()))));
				}
			}
			player.sendMessage(nearPlayers.toString());
			return true;
		}

		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

}
