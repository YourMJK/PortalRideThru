package com.mjkrempl.portalridethru;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class Configuration {
	public final boolean enabled;
	public final int portalCooldown;
	public final int configVersion;

	public Configuration(JavaPlugin plugin) {
		FileConfiguration config = plugin.getConfig();

		this.enabled = config.getBoolean("enabled", true);
		this.portalCooldown = getClampedInt(plugin, "portal-cooldown", 60, 20);
		this.configVersion = config.getInt("config-version");
	}
	
	private static int getClampedInt(JavaPlugin plugin, String path, int def, int min) {
		int value = plugin.getConfig().getInt(path, def);
		if (value >= min) return value;
		
		plugin.getLogger().log(Level.WARNING, "Config value \"" + path + "\" needs to be at least " + min + "!");
		return min;
	}
}
