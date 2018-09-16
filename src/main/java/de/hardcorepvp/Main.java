package de.hardcorepvp;

import de.hardcorepvp.commands.*;
import de.hardcorepvp.database.DatabaseManager;
import de.hardcorepvp.file.ConfigFile;
import de.hardcorepvp.file.PermissionsFile;
import de.hardcorepvp.listener.*;
import de.hardcorepvp.manager.*;
import de.hardcorepvp.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main instance;
	private static ConfigFile configFile;
	private static PermissionsFile permissionsFile;
	private static DatabaseManager databaseManager;
	private static Manager manager;
	private static UserManager userManager;
	private static RankingManager rankingManager;
	private static PunishmentManager punishmentManager;
	private static PermissionManager permissionManager;
	private static ClanManager clanManager;

	@Override
	public void onEnable() {
		Utils.registerCustomEnchantments();
		//TODO SAVE WORLDS IN CONFIG AND LOAD THEM PROPERLY
		World worldTest = WorldCreator.name("test").createWorld();
		instance = this;
		configFile = new ConfigFile();
		permissionsFile = new PermissionsFile();
		databaseManager = new DatabaseManager(configFile);
		if (permissionsFile.getGroups().size() == 0 || permissionsFile.getDefaultGroupSize() == 0 || permissionsFile.getDefaultGroupSize() > 1) {
			Bukkit.getConsoleSender().sendMessage("§cDie 'permissions.yml' wurde nicht richtig konfiguriert!");
			Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.getServer().setWhitelist(true), 60L);
			return;
		}
		if (!databaseManager.connect()) {
			Bukkit.getConsoleSender().sendMessage("§cEs konnte keine Verbindung zur Datenbank hergestellt werden...");
			Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.getServer().setWhitelist(true), 60L);
			return;
		}
		manager = new Manager();
		userManager = new UserManager();
		rankingManager = new RankingManager();
		punishmentManager = new PunishmentManager();
		permissionManager = new PermissionManager();
		clanManager = new ClanManager();

		registerCommands();
		registerListeners();

	}

	@Override
	public void onDisable() {
		if (databaseManager.isConnected()) {
			databaseManager.disconnect();
		}
	}

	private void registerCommands() {

		getCommand("heal").setExecutor(new CommandHeal());
		getCommand("vanish").setExecutor(new CommandVanish());
		getCommand("world").setExecutor(new CommandWorld());
		getCommand("feed").setExecutor(new CommandFeed());
		getCommand("craft").setExecutor(new CommandCraft());
		getCommand("fix").setExecutor(new CommandFix());
		getCommand("ifix").setExecutor(new CommandIFix());
		getCommand("enderchest").setExecutor(new CommandEnderchest());
		getCommand("hat").setExecutor(new CommandHat());
		getCommand("rename").setExecutor(new CommandRename());
		getCommand("stack").setExecutor(new CommandStack());
		getCommand("help").setExecutor(new CommandHelp());
		getCommand("pvp").setExecutor(new CommandPvP());
		getCommand("invsee").setExecutor(new CommandInvsee());
		getCommand("goldswitch").setExecutor(new CommandGoldswitch());
		getCommand("ranking").setExecutor(new CommandRanking());
		getCommand("sell").setExecutor(new CommandSell());
		getCommand("giveall").setExecutor(new CommandGiveall());
		getCommand("ban").setExecutor(new CommandBan());
		getCommand("autosell").setExecutor(new CommandAutosell());
		getCommand("spawner").setExecutor(new CommandSpawner());
		getCommand("tpa").setExecutor(new CommandTpa());
		getCommand("tpahere").setExecutor(new CommandTpahere());
		getCommand("tpaccept").setExecutor(new CommandTpaccept());
		getCommand("more").setExecutor(new CommandMore());
		getCommand("skin").setExecutor(new CommandSkin());
		getCommand("cmditem").setExecutor(new CommandCMDItem());
		getCommand("globalmute").setExecutor(new CommandGlobalmute());
		getCommand("gamemode").setExecutor(new CommandGamemode());
		getCommand("near").setExecutor(new CommandNear());
		getCommand("rank").setExecutor(new CommandRank());
		getCommand("stats").setExecutor(new CommandStats());
		getCommand("clan").setExecutor(new CommandClan());
		getCommand("excavator").setExecutor(new CommandExcavator());
		getCommand("tempban").setExecutor(new CommandTempban());
		getCommand("unban").setExecutor(new CommandUnban());
		getCommand("mute").setExecutor(new CommandMute());
		getCommand("tempmute").setExecutor(new CommandTempmute());
		getCommand("unmute").setExecutor(new CommandUnmute());
		getCommand("itemfilter").setExecutor(new CommandItemfilter());

		getCommand("skype").setExecutor(new CommandSimple());
		getCommand("teamspeak").setExecutor(new CommandSimple());
		getCommand("vote").setExecutor(new CommandSimple());
		getCommand("youtube").setExecutor(new CommandSimple());
		getCommand("discord").setExecutor(new CommandSimple());
		getCommand("bewerben").setExecutor(new CommandSimple());
		getCommand("spenden").setExecutor(new CommandSimple());

	}

	private void registerListeners() {
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
		this.getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
		this.getServer().getPluginManager().registerEvents(new VoteListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
		this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerPickupEvent(), this);
		this.getServer().getPluginManager().registerEvents(new ServerListPingListener(), this);
	}

	public static Main getInstance() {
		return instance;
	}

	public static ConfigFile getConfigFile() {
		return configFile;
	}

	public static PermissionsFile getPermissionsFile() {
		return permissionsFile;
	}

	public static DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public static Manager getManager() {
		return manager;
	}

	public static UserManager getUserManager() {
		return userManager;
	}

	public static RankingManager getRankingManager() {
		return rankingManager;
	}

	public static PunishmentManager getPunishmentManager() {
		return punishmentManager;
	}

	public static PermissionManager getPermissionManager() {
		return permissionManager;
	}

	public static ClanManager getClanManager() {
		return clanManager;
	}
}
