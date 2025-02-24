package com.mjkrempl.portalridethru.Remount;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

public class VehicleInfo {
	public final List<PassengerInfo> passengersInfo;
	public final Vector velocity;
	
	public VehicleInfo(List<PassengerInfo> passengersInfo, Vector velocity) {
		this.passengersInfo = passengersInfo;
		this.velocity = velocity;
	}
	
	public VehicleInfo(Vehicle vehicle) {
		// Build passengers info
		List<Entity> passengers = vehicle.getPassengers();
		List<PassengerInfo> passengersInfo = new ArrayList<>(passengers.size());
		for (Entity passenger : passengers) {
			passengersInfo.add(new PassengerInfo(passenger, passenger.getLocation()));
		}
		
		this.passengersInfo = passengersInfo;
		this.velocity = vehicle.getVelocity();
	}
}
