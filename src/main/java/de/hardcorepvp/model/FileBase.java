package de.hardcorepvp.model;

import de.hardcorepvp.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class FileBase {

	private File file;
	private FileConfiguration fileConfiguration;

	public FileBase(String path, String name) {
		this.file = new File(Main.getInstance().getDataFolder() + path, name);
		this.checkFile();
		this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
		this.writeDefaults();
	}

	private void checkFile() {
		if (!this.file.exists()) {
			try {
				this.file.getParentFile().mkdir();
				this.file.createNewFile();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	public FileConfiguration getFileConfiguration() {
		return fileConfiguration;
	}

	public void saveFile() {
		try {
			this.fileConfiguration.save(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public abstract void writeDefaults();

	public abstract void readAll();
}
