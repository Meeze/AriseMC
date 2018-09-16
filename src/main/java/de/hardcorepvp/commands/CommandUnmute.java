package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.User;
import de.hardcorepvp.manager.PunishmentManager;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnmute implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("system.unmute")) {
			player.sendMessage(Messages.NO_PERMISSIONS);
			return true;
		}
		if (args.length == 0 || args.length > 1) {
			player.sendMessage("§cVerwendung: §b/unmute <Spieler>");
			return true;
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase(player.getName())) {
				player.sendMessage("§cVerwendung: §b/unmute <Spieler>");
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				User user = Main.getUserManager().getUser(target.getUniqueId());
				if (user.getMuteData() == null) {
					player.sendMessage("§cDer Spieler ist nicht gemuted.");
					return true;
				}
				Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> Main.getPunishmentManager().unmute(target.getUniqueId()));
				user.setMuteData(null);
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.hasPermission("system.unmute") && !all.equals(player)) {
						all.sendMessage(Messages.formatMessage(Messages.PREFIX + "§6" + player.getName() + " §chat den Spieler §e" + args[0] + " §centmuted."));
					}
				}
				player.sendMessage("§cDer Spieler wurde entmuted.");
				target.sendMessage("§eDu wurdest entmuted.");
				return true;
			}
			UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
				@Override
				public void onResult(UUIDManager.ProfileHolder profile) {
					PunishmentManager.MuteData muteData = Main.getPunishmentManager().getMuteData(profile.getUniqueId());
					if (muteData == null) {
						player.sendMessage("§cDer Spieler ist nicht gemuted.");
						return;
					}
					boolean success = Main.getPunishmentManager().unban(profile.getUniqueId());
					if (!success) {
						player.sendMessage("§cDer Spieler konnte nicht entmuted werden!");
						return;
					}
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.hasPermission("system.unmute") && !all.equals(player)) {
							all.sendMessage(Messages.formatMessage(Messages.PREFIX + "§6" + player.getName() + " §chat den Spieler §e" + args[0] + " §centmuted."));
						}
					}
					player.sendMessage("§cDer Spieler wurde entmuted.");
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage("§cDer Spieler konnte nicht entmuted werden.");
				}
			});
		}
		return true;
	}
}
