package com.mjkrempl.portalridethru.Remount;

import java.util.*;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public final class VehicleRemountManager {
	private final PlayerTeleportEvent.TeleportCause teleportCause;
	private final Map<UUID, VehicleInfo> vehicleInfoMap;
	
	public VehicleRemountManager(PlayerTeleportEvent.TeleportCause teleportCause) {
		this.teleportCause = teleportCause;
		this.vehicleInfoMap = new HashMap<>();
	}
	
	public void dismount(Vehicle vehicle) {
		if (vehicle.isEmpty()) return;
		UUID vehicleUID = vehicle.getUniqueId();
		
		// Save vehicle info
		VehicleInfo vehicleInfo = new VehicleInfo(vehicle);
		vehicleInfoMap.put(vehicleUID, vehicleInfo);
		
		// Dismount all passengers
		vehicle.eject();
	}
	
	public boolean remount(Vehicle vehicle) {
		UUID vehicleUID = vehicle.getUniqueId();
		
		// Get vehicle info
		VehicleInfo vehicleInfo = vehicleInfoMap.remove(vehicleUID);
		if (vehicleInfo == null) return false;
		
		// Remount all passengers
		for (PassengerInfo passengerInfo : vehicleInfo.passengersInfo) {
			// Determine teleport location using new vehicle position and previous passenger orientation
			Location newLocation = vehicle.getLocation();
			newLocation.setPitch(passengerInfo.location.getPitch());
			newLocation.setYaw(passengerInfo.location.getYaw());
			
			// Prevent (creative) players from immediately entering returning portal after teleport
			passengerInfo.passenger.setPortalCooldown(10);
			
			// Teleport passenger
			boolean success = passengerInfo.passenger.teleport(newLocation, teleportCause);
			if (!success) continue;
			
			// Remount passenger
			vehicle.addPassenger(passengerInfo.passenger);
		}
		
		/*
		// Update vehicle speed after exiting portal
		double speed = vehicleInfo.velocity.length();
		setSpeed(vehicle, speed);
		*/
		
		return true;
	}
	
	
	private void setSpeed(Vehicle vehicle, double speed) {
		Vector velocity = vehicle.getVelocity();
		velocity.normalize();
		velocity.multiply(speed);
		vehicle.setVelocity(velocity);
	}
}
