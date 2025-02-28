package com.mjkrempl.portalridethru.Remount;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

public class VehicleInfo {
	public enum State {
		DISMOUNTED,
		ENTERED
	}
	
	public final List<PassengerInfo> passengersInfo;
	public final Vector velocity;
	public State state;
	
	public VehicleInfo(List<PassengerInfo> passengersInfo, Vector velocity, State state) {
		this.passengersInfo = passengersInfo;
		this.velocity = velocity;
		this.state = state;
	}
	
	public VehicleInfo(Vehicle vehicle, State state) {
		// Build passengers info
		List<Entity> passengers = vehicle.getPassengers();
		List<PassengerInfo> passengersInfo = new ArrayList<>(passengers.size());
		for (Entity passenger : passengers) {
			passengersInfo.add(new PassengerInfo(passenger, passenger.getLocation()));
		}
		
		this.passengersInfo = passengersInfo;
		this.velocity = vehicle.getVelocity();
		this.state = state;
	}
}
