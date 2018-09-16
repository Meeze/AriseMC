package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.manager.UUIDManager;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.utils.Messages;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandSkin implements CommandExecutor {


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;

		if (args.length == 0) {
			try {
				UUIDManager.getSkinData(player.getName(), new Callback<String[]>() {
					@Override
					public void onResult(String[] properties) {
						Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
							GameProfile profile = ((CraftPlayer) player).getProfile();
							profile.getProperties().clear();
							profile.getProperties().put("textures", new Property("textures", properties[0], properties[1]));
							for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
								onlinePlayer.hidePlayer(player);
								onlinePlayer.showPlayer(player);
							}
							player.sendMessage("Du hast deinen Skin resetet");
						});
					}

					@Override
					public void onFailure(Throwable cause) {
						player.sendMessage("Beim laden des Skins ist ein Problem aufgetreten!");
					}
				});
			} catch (Exception exception) {
				player.sendMessage("Beim laden des Skins ist ein Problem aufgetreten!");
			}
			return true;
		}
		if (args.length == 1) {
			try {
				Player target = Bukkit.getPlayer(args[0]);
				if (target != null) {
					Property property = UUIDManager.getSkinData(Bukkit.getPlayer(args[0]));
					GameProfile profile = ((CraftPlayer) player).getProfile();
					profile.getProperties().clear();
					profile.getProperties().put("textures", new Property("textures", property.getValue(), property.getSignature()));
					player.sendMessage("Du hast nun einen neuen Skin");
					for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						onlinePlayer.hidePlayer(player);
						onlinePlayer.showPlayer(player);
					}
					return true;
				}
				UUIDManager.getSkinData(args[0], new Callback<String[]>() {
					@Override
					public void onResult(String[] properties) {
						Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
							GameProfile profile = ((CraftPlayer) player).getProfile();
							profile.getProperties().clear();
							profile.getProperties().put("textures", new Property("textures", properties[0], properties[1]));
							for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
								onlinePlayer.hidePlayer(player);
								onlinePlayer.showPlayer(player);
							}
							player.sendMessage("Du hast nun einen neuen Skin");
						});
					}

					@Override
					public void onFailure(Throwable cause) {
						player.sendMessage("Beim laden des Skins ist ein Problem aufgetreten!");
					}
				});

			} catch (Exception exception) {
				player.sendMessage("Beim laden des Skins ist ein Problem aufgetreten!");
			}
			return true;
		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}
}


