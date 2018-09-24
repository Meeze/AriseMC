package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMuteHistory implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("system.mutehistory")) {
			player.sendMessage(Messages.NO_PERMISSIONS);
			return true;
		}
		if (args.length == 0 || args.length > 2) {
			player.sendMessage("§cVerwendung: §b/mutehistory <Spieler>");
			player.sendMessage("§cVerwendung: §b/mutehistory clear <Spieler>");
			return true;
		}
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				return true;
			}
			//UUID holen
		}
		if (args.length == 2) {
			if (!args[0].equalsIgnoreCase("clear")) {
				player.sendMessage("§cVerwendung: §b/mutehistory clear <Spieler>");
				return true;
			}
			Player target = Bukkit.getPlayer(args[1]);
			if (target != null) {
				Main.getPunishmentManager().deleteMuteHistoryAsync(target.getUniqueId(), new Callback<Boolean>() {
					@Override
					public void onResult(Boolean type) {
						player.sendMessage("Die Mute-History von " + target.getName() + " wurde gelöscht.");
					}

					@Override
					public void onFailure(Throwable cause) {
						player.sendMessage("Die Mute-History konnte nicht gelöscht werden!");
					}
				});
				return true;
			}
			//UUID holen
		}
		return true;
	}
}
