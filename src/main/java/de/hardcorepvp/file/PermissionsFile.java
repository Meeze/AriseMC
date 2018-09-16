package de.hardcorepvp.file;

import de.hardcorepvp.model.FileBase;
import de.hardcorepvp.permissions.PermissionGroup;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PermissionsFile extends FileBase {

	private List<PermissionGroup> groups;

	public PermissionsFile() {
		super("", "permissions.yml");
		this.groups = new CopyOnWriteArrayList<>();
		this.readAll();
	}

	@Override
	public void writeDefaults() {
		FileConfiguration configuration = this.getFileConfiguration();
		configuration.addDefault("groups.Mitglied.default", true);
		configuration.addDefault("groups.Mitglied.prefix", "&7[&9Mitglied&7]");
		configuration.addDefault("groups.Mitglied.permissions", Arrays.asList("system.kit.mitglied", "system.fix"));
		configuration.addDefault("groups.Ask.default", false);
		configuration.addDefault("groups.Ask.prefix", "&7[&2Ask&7]");
		configuration.addDefault("groups.Ask.inheritance", Arrays.asList("Mitglied"));
		configuration.addDefault("groups.Ask.permissions", Arrays.asList("system.chatclear", "system.teamchat"));

		configuration.options().copyDefaults(true);
		this.saveFile();
	}

	@Override
	public void readAll() {
		FileConfiguration configuration = this.getFileConfiguration();
		for (String name : configuration.getConfigurationSection("groups").getKeys(false)) {
			String groupPath = "groups." + name;
			System.out.println(groupPath);
			if (configuration.isSet(groupPath + ".default") && configuration.isSet(groupPath + ".prefix") && configuration.isSet(groupPath + ".permissions"))  {
				boolean isDefault = configuration.getBoolean(groupPath + ".default");
				System.out.println("default: " + isDefault);
				String prefix = configuration.getString(groupPath + ".prefix");
				System.out.println("prefix: " + prefix);
				List<String> permissions = configuration.getStringList(groupPath + ".permissions");
				permissions.forEach(permission -> System.out.println(permission));
				List<String> inheritance = new ArrayList<>();
				if (configuration.isSet(groupPath + ".inheritance")) {
					inheritance = configuration.getStringList(groupPath + ".inheritance");
					System.out.println(inheritance);
					for (String current : inheritance) {
						if (configuration.isSet("groups." + current + ".permissions")) {
							List<String> inheritancePermissions = configuration.getStringList("groups." + current + ".permissions");
							permissions.addAll(inheritancePermissions);
							System.out.println("groups." + current + ".permissions");
						}
					}
				}
				PermissionGroup group = new PermissionGroup(name, prefix, isDefault, inheritance, permissions);
				this.groups.add(group);
				System.out.println(group.isDefault());
			}
		}
	}

	public List<PermissionGroup> getGroups() {
		return groups;
	}

	public PermissionGroup getGroup(String name) {
		for (PermissionGroup group : this.groups) {
			if (group.getName().equalsIgnoreCase(name)) {
				return group;
			}
		}
		return null;
	}

	public int getDefaultGroupSize() {
		int counter = 0;
		for (PermissionGroup group : this.groups) {
			if (group.isDefault()) {
				counter++;
			}
		}
		return counter;
	}

	public PermissionGroup getDefaultGroup() {
		for (PermissionGroup group : this.groups) {
			if (group.isDefault()) {
				return group;
			}
		}
		return null;
	}
}
