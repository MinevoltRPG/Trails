package me.ccrama.Trails.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.ccrama.Trails.WrappedLocation;

public class SerializeLocation {
    public static String toBase64(WrappedLocation location) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            // Save every element in the list            
            dataOutput.writeObject(location);        
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