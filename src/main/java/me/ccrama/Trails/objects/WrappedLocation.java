package me.ccrama.Trails.objects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

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
	
	public boolean isLocation(Location loc){
		if(this.getBlockX() == loc.getBlockX())
			if(this.getBlockY() == loc.getBlockY())
				if(this.getBlockZ() == loc.getBlockZ())
					if(this.getWorld() == loc.getWorld())
						return true;
		return false;
	}
	
	public Location getLocation() {
		return new Location(getWorld(), x, y, z);
	}
	
	public String toBase64() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            // Save every element in the list            
            dataOutput.writeObject(this);        
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save locations.", e);
        }        
    }
	
	public static WrappedLocation fromBase64(String locationSerial) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(locationSerial));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            WrappedLocation location = (WrappedLocation)dataInput.readObject();         
            dataInput.close();
            return location;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}