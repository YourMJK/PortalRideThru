package com.mjkrempl.portalridethru;

import com.mjkrempl.portalridethru.Events.VehicleEventListener;
import com.mjkrempl.portalridethru.Remount.VehicleRemountManager;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PortalRideThru extends JavaPlugin {
	private Configuration config;
	private static final String maxGameVersion = "1.20.6";
	
	@Override
	public void onLoad() {
		saveDefaultConfig();
		Configuration.migrateIfNecessary(this);
		config = new Configuration(this);
	}
	
	@Override
	public void onEnable() {
		// Don't set up and disable plugin if config wishes to or if plugin is redundant due to newer version
		if (!config.enabled || checkGameMajorVersionTooNew()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		VehicleRemountManager remountManager = new VehicleRemountManager(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
		
		// Register event handlers
		VehicleEventListener vehicleEventListener = new VehicleEventListener(remountManager, Material.NETHER_PORTAL, config.portalCooldown);
		getServer().getPluginManager().registerEvents(vehicleEventListener, this);
	}
	
	
	private boolean checkGameMajorVersionTooNew() {
		String version = getServer().getBukkitVersion();
		String[] versionComponents = version.split("-");
		if (versionComponents.length == 0) return false;
		String mcVersion = versionComponents[0];
		
		int compare = mcVersion.compareTo(maxGameVersion);
		if (compare > 0) {
			getLogger().log(Level.WARNING, "Game version is newer than " + maxGameVersion + ", this plugin's functionality is already implemented. Will disable.");
			return true;
		}
		return false;
	}
}
