package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpaccept implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;

		if (args.length == 0) {
			if (Main.getManager().getCurrentTpaRequest().containsKey(player.getName())) {
				Player toTeleport = Bukkit.getPlayer(Main.getManager().getCurrentTpaRequest().get(player.getName()));
				Main.getManager().getCurrentTpaRequest().remove(player.getName());
				if (toTeleport != null) {
					toTeleport.teleport(player);
					return true;
				}
				player.sendMessage(Messages.PLAYER_NOT_FOUND);
				return true;
			}
			if (Main.getManager().getCurrentTpahereRequest().containsKey(player.getName())) {
				Player toTeleport = Bukkit.getPlayer(Main.getManager().getCurrentTpahereRequest().get(player.getName()));
				Main.getManager().getCurrentTpahereRequest().remove(player.getName());
				if (toTeleport != null) {
					player.teleport(toTeleport);
					return true;
				}
				player.sendMessage(Messages.PLAYER_NOT_FOUND);
				return true;


			}
		}

		player.sendMessage(Messages.TOO_MANY_ARGUMENTS);
		return true;
	}
}
