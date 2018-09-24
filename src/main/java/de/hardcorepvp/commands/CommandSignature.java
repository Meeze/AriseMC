package de.hardcorepvp.commands;

import de.hardcorepvp.Main;
import de.hardcorepvp.utils.Messages;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSignature implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if (args.length == 0) {

            Block block = player.getTargetBlock(null, 5);

            if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
                player.sendMessage("Schau ein Schild an!");
                return true;
            }
            Sign sign = (Sign) block.getState();
            sign.setLine(0, "-*-*-*-*-*-*-*-");
            sign.setLine(1, ChatColor.translateAlternateColorCodes('&', Main.getUserManager().getUser(player.getUniqueId()).getGroup().getPrefix()));
            sign.setLine(2, ChatColor.translateAlternateColorCodes('&', StringUtils.substring(Main.getUserManager().getUser(player.getUniqueId()).getGroup().getPrefix(), 3, 5) + " " + player.getName()));
            sign.setLine(3, "-*-*-*-*-*-*-*-");
            sign.update();
            return true;
        }
        player.sendMessage(Messages.formatMessage(Messages.TOO_MANY_ARGUMENTS));
        return true;
    }

}
