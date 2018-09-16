package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.data.User;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.permissions.PermissionGroup;
import de.hardcorepvp.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandRank implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("system.rank")) {
			player.sendMessage(Messages.formatMessage(Messages.NO_PERMISSIONS));
			return true;
		}
		if (args.length == 0 || args.length > 2) {
			player.sendMessage("/rank <Spieler> <Rang>");
			return true;
		}
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				this.sendRank(player, target.getUniqueId(), args[0]);
				return true;
			}
			UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
				@Override
				public void onResult(UUIDManager.ProfileHolder profile) {
					sendRank(player, profile.getUniqueId(), profile.getName());
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage(Messages.formatMessage(Messages.ERROR_OCCURRED));
				}
			});
		}
		if (args.length == 2) {
			PermissionGroup group = Main.getPermissionsFile().getGroup(args[1]);
			if (group == null) {
				player.sendMessage("Dieser Rang existiert nicht!");
				return true;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if (target != null) {
				this.setRank(player, target.getUniqueId(), args[0], group);
				Main.getPermissionManager().removePermissions(player);
				Main.getPermissionManager().addPermissions(player, group.getPermissions());
				return true;
			}
			UUIDManager.getProfileHolderAt(args[0], System.currentTimeMillis(), new Callback<UUIDManager.ProfileHolder>() {
				@Override
				public void onResult(UUIDManager.ProfileHolder profile) {
					setRank(player, profile.getUniqueId(), profile.getName(), group);
				}

				@Override
				public void onFailure(Throwable cause) {
					player.sendMessage(Messages.formatMessage(Messages.ERROR_OCCURRED));
				}
			});
		}
		return true;
	}

	private void sendRank(Player executor, UUID targetUniqueId, String targetName) {
		User user = Bukkit.getOfflinePlayer(targetUniqueId).isOnline() ? Main.getUserManager().getUser(targetUniqueId) : new User(targetUniqueId);
		if (user == null) {
			executor.sendMessage(Messages.formatMessage(Messages.ERROR_OCCURRED));
			return;
		}
		user.addReadyExecutor(new Runnable() {
			@Override
			public void run() {
				executor.sendMessage("Rang von " + targetName + ": " + ChatColor.translateAlternateColorCodes('&', user.getGroup().getPrefix()));
			}
		});
	}

	private void setRank(Player executor, UUID targetUniqueId, String targetName, PermissionGroup group) {
		User user = Bukkit.getOfflinePlayer(targetUniqueId).isOnline() ? Main.getUserManager().getUser(targetUniqueId) : new User(targetUniqueId);
		if (user == null) {
			executor.sendMessage(Messages.formatMessage(Messages.ERROR_OCCURRED));
			return;
		}
		user.addReadyExecutor(new Runnable() {
			@Override
			public void run() {
				user.setGroup(group);
				executor.sendMessage("Der Spieler " + targetName + " ist nun in der Gruppe '" + ChatColor.translateAlternateColorCodes('&', group.getPrefix()) + "'");
			}
		});
	}
}
