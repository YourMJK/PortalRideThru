package com.mjkrempl.portalridethru.Events;

import com.mjkrempl.portalridethru.Remount.VehicleRemountManager;
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
import org.bukkit.util.Vector;

public class VehicleEventListener implements Listener {
	private final VehicleRemountManager vehicleRemountManager;
	private final Material portalMaterial;
	
	public VehicleEventListener(VehicleRemountManager vehicleRemountManager, Material portalMaterial) {
		this.vehicleRemountManager = vehicleRemountManager;
		this.portalMaterial = portalMaterial;
	}
	
	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		Vehicle vehicle = event.getVehicle();
		
		// Check if vehicle is a minecart
		if (vehicle.getType() != EntityType.MINECART) return;
		
		vehicleRemountManager.didMove(vehicle);
		
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
			vehicleRemountManager.willEnterPortal(vehicle);
		}
	}
	
	@EventHandler
	public void onVehicleDestroyed(VehicleDestroyEvent event) {
		Vehicle vehicle = event.getVehicle();
		if (vehicle.getType() != EntityType.MINECART) return;
		
		vehicleRemountManager.wasDestroyed(vehicle);
	}
	
	@EventHandler
	public void onEntityPortalUse(EntityPortalEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() != EntityType.MINECART) return;
		
		// When empty minecart used the portal, notify manager it entered
		Vehicle vehicle = (Vehicle)entity;
		vehicleRemountManager.didEnterPortal(vehicle);
	}
}
