package com.mjkrempl.portalridethru.Remount;

import java.util.*;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public final class VehicleRemountManager {
	private final PlayerTeleportEvent.TeleportCause teleportCause;
	private final boolean keepSpeed;
	private final int portalCooldown;
	private final Map<UUID, VehicleInfo> vehicleInfoMap;
	
	public VehicleRemountManager(PlayerTeleportEvent.TeleportCause teleportCause, boolean keepSpeed, int portalCooldown) {
		this.teleportCause = teleportCause;
		this.keepSpeed = keepSpeed;
		this.portalCooldown = portalCooldown;
		this.vehicleInfoMap = new HashMap<>();
	}
	
	
	public void willEnterPortal(Vehicle vehicle) {
		// Save vehicle info and dismount all passengers
		saveVehicleInfo(vehicle, VehicleInfo.State.DISMOUNTED);
		dismount(vehicle);
	}
	
	public void didEnterPortal(Vehicle vehicle) {
		if (!isTracked(vehicle)) return;
		
		// Update state to reflect that vehicle has entered portal
		VehicleInfo vehicleInfo = getVehicleInfo(vehicle);
		vehicleInfo.state = VehicleInfo.State.ENTERED;
	}
	
	public void didMove(Vehicle vehicle) {
		if (!isTracked(vehicle)) return;
		
		// Check current state after new movement update
		VehicleInfo vehicleInfo = getVehicleInfo(vehicle);
		
		switch (vehicleInfo.state) {
			case DISMOUNTED:
				// Vehicle was emptied, wait until it has entered portal
				break;
			
			case ENTERED:
				// Vehicle appeared again after being in portal, remount all passengers
				remount(vehicle, vehicleInfo.passengersInfo);
				
				// Reset portal cooldown to enable future portal use again
				vehicle.setPortalCooldown(portalCooldown);
				
				if (keepSpeed) {
					// Queue updates to restore previous speed (setting velocity once isn't sufficient, has to be set for multiple ticks in succession)
					updateSpeed(vehicle, vehicleInfo);
					vehicleInfo.state = VehicleInfo.State.REMOUNTED;
				} else {
					// Done, forget vehicle
					removeVehicleInfo(vehicle);
				}
				break;
				
			case REMOUNTED:
				// Vehicle was remounted, apply first speed update with passengers
				updateSpeed(vehicle, vehicleInfo);
				vehicleInfo.state = VehicleInfo.State.SPEED_UPDATE;
				break;
				
			case SPEED_UPDATE:
				// Apply second speed update with passengers
				updateSpeed(vehicle, vehicleInfo);
				
				// Done, forget vehicle
				removeVehicleInfo(vehicle);
				break;
		}
	}
	
	public void wasDestroyed(Vehicle vehicle) {
		removeVehicleInfo(vehicle);
	}
	
	
	private boolean isTracked(Vehicle vehicle) {
		if (vehicleInfoMap.isEmpty()) return false;
		UUID vehicleUID = vehicle.getUniqueId();
		return vehicleInfoMap.containsKey(vehicleUID);
	}
	
	private void saveVehicleInfo(Vehicle vehicle, VehicleInfo.State state) {
		UUID vehicleUID = vehicle.getUniqueId();
		VehicleInfo vehicleInfo = new VehicleInfo(vehicle, state);
		vehicleInfoMap.put(vehicleUID, vehicleInfo);
	}
	
	private VehicleInfo getVehicleInfo(Vehicle vehicle) {
		UUID vehicleUID = vehicle.getUniqueId();
		return vehicleInfoMap.get(vehicleUID);
	}
	
	private void removeVehicleInfo(Vehicle vehicle) {
		UUID vehicleUID = vehicle.getUniqueId();
		vehicleInfoMap.remove(vehicleUID);
	}
	
	
	private void dismount(Vehicle vehicle) {
		if (vehicle.isEmpty()) return;
		
		// Dismount all passengers
		vehicle.eject();
	}
	
	private void remount(Vehicle vehicle, List<PassengerInfo> passengersInfo) {
		// Remount all passengers
		for (PassengerInfo passengerInfo : passengersInfo) {
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
	}
	
	private void updateSpeed(Vehicle vehicle, VehicleInfo vehicleInfo) {
		// Restore previous speed if vehicle is slower
		double previousSpeed = vehicleInfo.velocity.length();
		double currentSpeed = vehicle.getVelocity().length();
		if (currentSpeed > previousSpeed) return;
		
		setSpeed(vehicle, previousSpeed);
	}
	
	private void setSpeed(Vehicle vehicle, double speed) {
		// Apply speed to current velocity vector
		Vector velocity = vehicle.getVelocity();
		velocity.normalize();
		velocity.multiply(speed);
		vehicle.setVelocity(velocity);
	}
}
