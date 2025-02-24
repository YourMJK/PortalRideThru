package com.mjkrempl.portalridethru.Remount;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public final class PassengerInfo {
	public final Entity passenger;
	public final Location location;
	
	public PassengerInfo(Entity passenger, Location location) {
		this.passenger = passenger;
		this.location = location;
	}
}
