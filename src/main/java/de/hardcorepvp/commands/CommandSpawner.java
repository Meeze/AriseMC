package de.hardcorepvp.commands;

import de.hardcorepvp.utils.Messages;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CommandSpawner implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}

		Player player = (Player) sender;
		if (args.length == 0) {

			Block block = player.getTargetBlock(null, 5);
			if (block.getType() != Material.MOB_SPAWNER) {
				player.sendMessage(Messages.formatMessage("Du musst einen Spawner anschauen!"));
				return true;
			}
			CreatureSpawner cs = (CreatureSpawner) block.getState();
			player.sendMessage(cs.getCreatureTypeName());
			return true;
		}
		if (args.length == 1) {

			Block block = player.getTargetBlock(null, 5);
			if (block.getType() != Material.MOB_SPAWNER) {
				player.sendMessage(Messages.formatMessage("Du musst einen Spawner anschauen!"));
				return true;
			}
			CreatureSpawner spawner = (CreatureSpawner) block.getState();
			player.sendMessage(spawner.getCreatureTypeName());
			if (args[0].equalsIgnoreCase("creeper")) {
				spawner.setSpawnedType(EntityType.CREEPER);
				spawner.update();
				return true;
			}
			if (args[0].equalsIgnoreCase("pigman")) {
				spawner.setSpawnedType(EntityType.PIG_ZOMBIE);
				spawner.update();
				return true;
			}
			if (args[0].equalsIgnoreCase("golem")) {
				spawner.setSpawnedType(EntityType.IRON_GOLEM);
				spawner.update();
				return true;
			}
			if (args[0].equalsIgnoreCase("pig")) {
				spawner.setSpawnedType(EntityType.PIG);
				spawner.update();
				return true;
			}

		}
		player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
		return true;
	}

}
