package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandVanish implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (args.length == 0) {
			if (!Main.getManager().isVanished(player)) {
				for (Player allPlayer : Bukkit.getOnlinePlayers()) {
					//TODO PERMISSION VANISH
					if (true) {
						allPlayer.hidePlayer(player);
					}
				}
				Main.getManager().addVanishedPlayer(player);
				player.sendMessage("Du bist nun im vanish");
				Bukkit.broadcastMessage(Messages.TEAM_MEMBER_LEFT);
				return true;
			}
			for (Player allPlayer : Bukkit.getOnlinePlayers()) {
				//TODO PERMISSION VANISH
				if (true) {
					allPlayer.showPlayer(player);
				}
			}
			Main.getManager().removeVanishedPlayer(player);
			player.sendMessage("Du bist nicht mehr im Vanish");
			Bukkit.broadcastMessage(Messages.TEAM_MEMBER_JOINED);
			return true;
		}
		player.sendMessage(Messages.TOO_MANY_ARGUMENTS);
		return true;
	}

}
