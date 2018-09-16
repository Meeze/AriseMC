package de.hardcorepvp.permissions;

import de.hardcorepvp.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class PermissionGroup {

	private String name;
	private String prefix;
	private boolean isDefault;
	private List<String> inheritance;
	private List<String> permissions;

	public PermissionGroup(String name, String prefix, boolean isDefault, List<String> inheritance, List<String> permissions) {
		this.name = name;
		this.prefix = prefix;
		this.isDefault = isDefault;
		this.inheritance = inheritance;
		this.permissions = permissions;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public List<String> getInheritance() {
		return inheritance;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		this.writeToFile(true, false, false);
	}

	public void addInheritance(String name) {
		this.inheritance.add(name);
		this.writeToFile(false, true, false);
	}

	public void removeInheritance(String name) {
		this.inheritance.remove(name);
		this.writeToFile(false, true, false);
	}

	public void addPermission(String permission) {
		this.permissions.add(permission);
		this.writeToFile(false, false, true);
	}

	public void removePermission(String permission) {
		this.permissions.remove(permission);
		this.writeToFile(false, false, true);
	}

	private void writeToFile(boolean prefix, boolean inheritance, boolean permission) {
		FileConfiguration configuration = Main.getPermissionsFile().getFileConfiguration();
		if (prefix) {
			configuration.addDefault("groups." + name + ".prefix", this.prefix);
			Main.getPermissionsFile().saveFile();
		}
		if (inheritance) {
			configuration.addDefault("groups." + name + ".inheritance", this.inheritance);
			Main.getPermissionsFile().saveFile();
		}
		if (permission) {
			configuration.addDefault("groups." + name + ".permissions", this.permissions);
			Main.getPermissionsFile().saveFile();
		}
	}
}
