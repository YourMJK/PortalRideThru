package com.mjkrempl.portalridethru.Events;

import com.mjkrempl.portalridethru.Remount.VehicleRemountManager;
import java.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class VehicleEventListener implements Listener {
	private final VehicleRemountManager vehicleRemountManager;
	private final Material portalMaterial;
	private final int portalCooldown;
	private final Set<UUID> vehiclesInPortal;
	private boolean hasVehiclesInPortal;
	
	public VehicleEventListener(VehicleRemountManager vehicleRemountManager, Material portalMaterial, int portalCooldown) {
		this.vehicleRemountManager = vehicleRemountManager;
		this.portalMaterial = portalMaterial;
		this.portalCooldown = portalCooldown;
		this.vehiclesInPortal = new HashSet<>();
		this.hasVehiclesInPortal = false;
	}
	
	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		Vehicle vehicle = event.getVehicle();
		
		// Check if vehicle is a minecart
		if (vehicle.getType() != EntityType.MINECART) return;
		
		// Check if vehicle appeared again after being in portal
		if (hasVehiclesInPortal && removeVehicleInPortal(vehicle)) {
			// Reset portal cooldown
			vehicle.setPortalCooldown(portalCooldown);
			
			// Remount previous passengers
			vehicleRemountManager.remount(vehicle);
		}
		
		// Check if vehicle is occupied
		if (vehicle.isEmpty()) return;
		
		// Get location 0.5 blocks in front of vehicle
		Location loc = event.getTo();
		Vector velocity = vehicle.getVelocity();
		velocity.normalize();
		velocity.multiply(0.5);
		loc.add(velocity);
		
		// Check location for portal block
		BlockData block = vehicle.getWorld().getBlockData(loc);
		if (block.getMaterial() == portalMaterial) {
			vehicleRemountManager.dismount(vehicle);
		}
	}
	
	@EventHandler
	public void onEntityPortalUse(EntityPortalEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() != EntityType.MINECART) return;
		
		// When empty minecart used the portal, mark it for potential remounting
		Vehicle vehicle = (Vehicle)entity;
		addVehicleInPortal(vehicle);
	}
	
	
	private void addVehicleInPortal(Vehicle vehicle) {
		vehiclesInPortal.add(vehicle.getUniqueId());
		hasVehiclesInPortal = true;
	}
	
	private boolean removeVehicleInPortal(Vehicle vehicle) {
		boolean exists = vehiclesInPortal.remove(vehicle.getUniqueId());
		if (vehiclesInPortal.isEmpty()) {
			hasVehiclesInPortal = false;
		}
		return exists;
	}
}
