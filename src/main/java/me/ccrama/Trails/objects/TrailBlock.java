package me.ccrama.Trails.objects;


import java.io.Serializable;
import org.bukkit.Location;

public class TrailBlock implements Serializable{
	
	private static final long serialVersionUID = -5944092517430475806L;
	private WrappedLocation location;
	private Integer walks = 0;
	
	/**
	 * 
	 */
	
	public TrailBlock(WrappedLocation location, Integer numberOfWalks) {
		this.location = location;
		this.walks = numberOfWalks;
	}
	
	public TrailBlock(Location location, Integer numberOfWalks) {
		this.location = new WrappedLocation(location);
		this.walks = numberOfWalks;
	}
	
	public Location getLocation() {
		return this.location.getLocation();
	}
	
	public int getWalks() {
		return this.walks;
	}
	
	public WrappedLocation getWrappedLocation() {
		return this.location;
	}
}