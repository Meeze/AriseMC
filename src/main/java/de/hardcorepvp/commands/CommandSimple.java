package de.hardcorepvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSimple implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("youtube")) {
			player.sendMessage("Unser YT Kanal");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("teamspeak")) {
			player.sendMessage("Unser Ts3");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("skype")) {
			player.sendMessage("McMeze: Scarface01: WKChris: Criikcz:");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("vote")) {
			player.sendMessage("Vote hier:");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("spenden")) {
			player.sendMessage("Per Skype oder Discord oder ts3 bei admins");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("bewerben")) {
			player.sendMessage("Bewerbungsphase");
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("discord")) {
			player.sendMessage("McMeze: Scarface01: WKChris: Criikcz:");
			return true;
		}
		return false;
	}
}
