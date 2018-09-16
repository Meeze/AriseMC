package de.hardcorepvp.utils;

import de.hardcorepvp.Main;
import de.hardcorepvp.model.Callback;
import de.hardcorepvp.model.Excavator;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

	public static CMDItemEnchant uniqueEnchant = new CMDItemEnchant(1337);

	public static int tpaCooldown = 5;

	public static List<Material> toFilter = Arrays.asList(new Material[]{Material.GOLD_SWORD, Material.ROTTEN_FLESH, Material.RED_ROSE});
	public static Material excavatorMaterial = Material.NOTE_BLOCK;
	public static Material rankupMaterial = Material.BOOK;

	public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


	public static int stackItems(Player player) {

		int stackedItems = 0;
		ItemStack[] contents = player.getInventory().getContents();

		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			if ((item != null) && (item.getType() != Material.AIR) && (item.getAmount() > 0)) {
				if (item.getAmount() < 64) {
					int needed = 64 - item.getAmount();
					for (int i2 = i++; i2 < contents.length; i2++) {
						ItemStack nextCurrent = contents[i2];
						if ((nextCurrent != null) && (nextCurrent.getType() != Material.AIR) && (nextCurrent.getAmount() > 0)) {
							if ((item.getType() == nextCurrent.getType()) && (item.getDurability() == nextCurrent.getDurability()) && (((item.getItemMeta() == null) && (nextCurrent.getItemMeta() == null)) || ((item.getItemMeta() != null) && (item.getItemMeta().equals(nextCurrent.getItemMeta()))))) {
								if (nextCurrent.getAmount() > needed) {
									item.setAmount(64);
									nextCurrent.setAmount(nextCurrent.getAmount() - needed);
									stackedItems++;
									break;
								}
								contents[i2] = null;
								item.setAmount(item.getAmount() + nextCurrent.getAmount());
								needed = 64 - item.getAmount();

							}
						}
					}
				}
			}
		}
		return stackedItems;
	}

	public static void sendItemInChat(Player player, ItemStack item, String playerstuff, String message) {


		//TODO USE IF MESSAGES BEGINS WITH [ITEM] OR CMD /SHAREITEM IDC AND REPLACE PREFIX, CLAN, NAME and MESSAGE @PASCAL :)
		IChatBaseComponent itemString = CraftItemStack.asNMSCopy(item).E();
		ChatComponentText base = new ChatComponentText(playerstuff);
		ChatComponentText content = new ChatComponentText(message);

		base.addSibling(itemString);
		base.addSibling(content);

		IChatBaseComponent component = base;

		PacketPlayOutChat packet = new PacketPlayOutChat(component, true);

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

	}

	public static void renameItemInHand(Player player, String name) {
		String itemname = ChatColor.translateAlternateColorCodes('&', name);
		ItemStack item = player.getItemInHand();
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName(itemname);
		item.setItemMeta(itemmeta);
	}

	public static void removeNegativePotions(Player player) {
		for (PotionEffect effects : player.getActivePotionEffects()) {
			for (NegativeEffects negEffects : NegativeEffects.values()) {
				if (effects.getType().getName().equalsIgnoreCase(negEffects.name())) {
					player.removePotionEffect(effects.getType());
				}
			}
		}
	}

	public static int fixItems(Player player, boolean armorfix) {

		int fixedItems = 0;
		ItemStack[] items = player.getInventory().getContents();
		for (ItemStack item : items) {
			if (item != null) {
				item.setDurability((short) 0);
				fixedItems++;

			}
		}

		if (armorfix) {
			ItemStack[] armor = player.getInventory().getArmorContents();
			for (ItemStack anArmor : armor) {
				if (anArmor != null) {
					anArmor.setDurability((short) 0);
					fixedItems++;
				}
			}
		}
		return fixedItems;
	}

	public static void registerCustomEnchantments() {
		try {
			CMDItemEnchant cmdItemEnchant = new CMDItemEnchant(1337);
			Enchantment.setAcceptingNew(true);
			Enchantment.registerEnchantment(cmdItemEnchant);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String serializeLocation(Location location) {
		return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
	}

	public static Location deserializeLocation(String location) {
		String[] deserialized = location.split(";");
		return new Location(Bukkit.getServer().getWorld(deserialized[0]), Double.valueOf(deserialized[1]), Double.valueOf(deserialized[2]), Double.valueOf(deserialized[3]), Float.valueOf(deserialized[4]), Float.valueOf(deserialized[5]));
	}

	public static void deleteWorld(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					deleteWorld(file);
				} else {
					file.delete();
				}
			}
			path.delete();
		}
	}

	public static void destroyCube(Location location, int radius, Callback<Excavator> callback) {
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			try {
				LinkedList<Block> toRemove = new LinkedList<>();
				long timestamp = System.currentTimeMillis();
				for (int x = (radius * -1); x <= radius; x++) {
					for (int y = (radius * -1); y <= radius; y++) {
						for (int z = (radius * -1); z <= radius; z++) {
							Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);
							if (block.getType() != Material.BEDROCK && block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST && block.getType() != Material.MOB_SPAWNER && block.getType() != Material.AIR) {
								toRemove.add(block);
							}
						}
					}
				}
				System.out.println(System.currentTimeMillis() - timestamp);
				callback.onResult(new Excavator(new ConcurrentLinkedQueue<>(toRemove), 200, 0L, 2L));
			} catch (Exception e) {
				callback.onFailure(e.getCause());
			}
		});
	}

	public static ItemStack getCommandItem(Material material, String lore, String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setLore(Collections.singletonList(lore));
		im.setDisplayName(Messages.CMD_ITEM_PREFIX + name);
		im.addEnchant(uniqueEnchant, 1, true);
		item.setItemMeta(im);
		return item;
	}

	public static ItemStack getExcavatorBlock(int radius) {
		ItemStack item = new ItemStack(excavatorMaterial);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Collections.singletonList(Messages.EXCAVATOR_RADIUS + radius));
		meta.setDisplayName(Messages.EXCAVATOR_BLOCK);
		meta.addEnchant(uniqueEnchant, 1, true);
		item.setItemMeta(meta);
		return item;
	}

	public static String formatDate(long timestamp) {
		return simpleDateFormat.format(new Date(timestamp));
	}

	public static long parseDateDiff(String time, boolean future) {
		Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
		Matcher m = timePattern.matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find()) {
			if (m.group() == null || m.group().isEmpty()) {
				continue;
			}
			for (int i = 0; i < m.groupCount(); i++) {
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					found = true;
					break;
				}
			}
			if (found) {
				if (m.group(1) != null && !m.group(1).isEmpty()) years = Integer.parseInt(m.group(1));
				if (m.group(2) != null && !m.group(2).isEmpty()) months = Integer.parseInt(m.group(2));
				if (m.group(3) != null && !m.group(3).isEmpty()) weeks = Integer.parseInt(m.group(3));
				if (m.group(4) != null && !m.group(4).isEmpty()) days = Integer.parseInt(m.group(4));
				if (m.group(5) != null && !m.group(5).isEmpty()) hours = Integer.parseInt(m.group(5));
				if (m.group(6) != null && !m.group(6).isEmpty()) minutes = Integer.parseInt(m.group(6));
				if (m.group(7) != null && !m.group(7).isEmpty()) seconds = Integer.parseInt(m.group(7));
				break;
			}
		}
		if (!found) return -1L;
		Calendar c = new GregorianCalendar();
		if (years > 0) c.add(Calendar.YEAR, years * (future ? 1 : -1));
		if (months > 0) c.add(Calendar.MONTH, months * (future ? 1 : -1));
		if (weeks > 0) c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		if (days > 0) c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		if (hours > 0) c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		if (minutes > 0) c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		if (seconds > 0) c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		return c.getTimeInMillis();
	}
}
