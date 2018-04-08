package me.ccrama.Trails;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WrappedLocation implements Serializable{
	
	private static final long serialVersionUID = -5944092517430475806L;
	/**
	 * 
	 */
	private double x;
	private double y;
	private double z;
	private String world;
	private int blockX;
	private int blockY;
	private int blockZ;
	
	public WrappedLocation(Location loc){
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.blockX = loc.getBlockX();
		this.blockY = loc.getBlockY();
		this.blockZ = loc.getBlockZ();
		this.world = loc.getWorld().getUID().toString();
	}
	
	public int getBlockX(){
		return blockX;
	}
	
	public int getBlockY(){
		return blockY;
	}
	
	public int getBlockZ(){
		return blockZ;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public World getWorld(){
		return Bukkit.getServer().getWorld((UUID.fromString(this.world)));	
	}
	
	public static boolean compareLocations(WrappedLocation loc1, Location loc2){
		if(loc1.getBlockX() == loc2.getBlockX())
			if(loc1.getBlockY() == loc2.getBlockY())
				if(loc1.getBlockZ() == loc2.getBlockZ())
					if(loc1.getWorld() == loc2.getWorld())
						return true;
		return false;
	}
}