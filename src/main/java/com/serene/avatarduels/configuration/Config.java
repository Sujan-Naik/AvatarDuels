package com.serene.avatarduels.configuration;

import com.serene.avatarduels.AvatarDuels;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

	AvatarDuels plugin;

	private File file;
	public FileConfiguration config;

	public Config(File file) {
		this.plugin = AvatarDuels.plugin;
		this.file = new File(plugin.getDataFolder() + File.separator + file);
		this.config = YamlConfiguration.loadConfiguration(this.file);
		reloadConfig();
	}

	public void createConfig() {
		if (!file.getParentFile().exists()) {
			try {
				file.getParentFile().mkdir();
				plugin.getLogger().info("Generating new directory for " + file.getName() + "!");
			} catch (Exception e) {
				plugin.getLogger().info("Failed to generate directory!");
				e.printStackTrace();
			}
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
				plugin.getLogger().info("Generating new " + file.getName() + "!");
			} catch (Exception e) {
				plugin.getLogger().info("Failed to generate " + file.getName() + "!");
				e.printStackTrace();
			}
		}
	}

	public FileConfiguration getConfig() {
		return config;
	}
	
	public void reloadConfig() {
		createConfig();
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			config.options().copyDefaults(true);
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
