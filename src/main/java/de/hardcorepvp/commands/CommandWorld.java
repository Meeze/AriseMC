package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CommandWorld implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;

		//TODO SAVE WORLDS IN CONFIG AND LOAD THEM PROPERLY

		if (args.length == 0) {
			player.sendMessage(Messages.formatMessage(Bukkit.getWorlds().toString()));
			return true;
		}
		if (args.length == 1) {
			try {
				World world = Bukkit.getWorld(args[0]);
				player.sendMessage(world.getName() + " " + world.getSeed() + " " + world.getSpawnLocation());
				return true;
			} catch (Exception exception) {
				player.sendMessage("World nicht gefunden /world");
				return true;
			}

		}
		if (args.length == 2) {

			if (args[0].equalsIgnoreCase("create")) {
				WorldCreator worldcreator = new WorldCreator(args[1]);
				worldcreator.environment(World.Environment.NORMAL);
				World world = Bukkit.createWorld(worldcreator);
				world.save();
				return true;
			}
			if (args[0].equalsIgnoreCase("tp")) {
				try {
					player.getLocation().setWorld(Bukkit.getWorld(args[1]));
					player.teleport(new Location(Bukkit.getWorld(args[1]), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
				} catch (Exception exception) {
					player.sendMessage("World nicht gefunden /world");
					return true;
				}

			}
			if (args[0].equalsIgnoreCase("delete")) {

				World world = Bukkit.getWorld(args[1]);
				Bukkit.unloadWorld(world, false);
				File worldFile = world.getWorldFolder();
				Utils.deleteWorld(worldFile);
				return true;

			}

		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}
}
