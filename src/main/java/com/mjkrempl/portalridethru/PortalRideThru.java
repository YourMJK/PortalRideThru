package com.mjkrempl.portalridethru;

import com.mjkrempl.portalridethru.Events.VehicleEventListener;
import com.mjkrempl.portalridethru.Remount.VehicleRemountManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class PortalRideThru extends JavaPlugin {
	private Configuration config;
	
	@Override
	public void onLoad() {
		saveDefaultConfig();
		config = new Configuration(this);
	}
	
	@Override
	public void onEnable() {
		// Don't set up if config wishes to disable functionality
		if (!config.enabled) return;
		
		VehicleRemountManager remountManager = new VehicleRemountManager(this, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
		
		// Register event handlers
		VehicleEventListener vehicleEventListener = new VehicleEventListener(this, remountManager, Material.NETHER_PORTAL, config.portalCooldown);
		getServer().getPluginManager().registerEvents(vehicleEventListener, this);
	}
}
