package me.ccrama.Trails.data;

import java.util.Iterator;

import org.bukkit.Material;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import me.ccrama.Trails.objects.Links;

public class LinksDataManager{
	
	private Trails main;
	private Links links = new Links();
	
	public LinksDataManager(Trails plugin) {
		this.main = plugin;
		createLinks();
	}
	
	private void createLinks() {
	      Iterator<?> var2 = this.main.getConfig().getConfigurationSection("Trails").getKeys(false).iterator();

	      while(var2.hasNext()) {
	         String configs = (String)var2.next();
	         String s = this.main.getConfig().getString("Trails." + configs);
	         String[] sarray = s.split(">");
	         int numb = sarray.length - 1;
	         Link lastlink = null;

	         while(true) {
	        	boolean legit = true;
	        	Material mat;
	        	byte dataValue = 0;
	            String numbs = sarray[numb];
	            String[] sarray2 = numbs.split(":");
	            if(sarray2[0].contains(";")){
	            	String[] matAndDataValue = sarray2[0].split(";");
	            	mat = Material.getMaterial(matAndDataValue[0].toUpperCase());
	            	dataValue = (byte) Integer.parseInt(matAndDataValue[1]);
	            }else{
	            	mat = Material.getMaterial(sarray2[0]);
	            }
	            for(Link link : links){
	            	if(link.getMat() == mat && link.getDataValue() == dataValue){
	            		System.out.println("[Trails] ERROR(severe): Two Trails have the same material && data values. Please check the config and make sure no two trails have the same data values.");
	                    numb = -1;
	                    legit = false;
	            	}
	            }
	            if(legit) {
	            	int wearTimes = Integer.parseInt(sarray2[1]);
	                int chance = Integer.parseInt(sarray2[2]);
	                Link link2 = new Link(mat, dataValue, wearTimes - 1, chance, numb, lastlink);
	                
	                links.add(link2);
	                lastlink = link2;
	                System.out.println("[TRAILS] added: Link material = " + mat.name() + ":" + dataValue + " wear = " + wearTimes + " chance = " + chance + "%");
	                --numb;
	            }
	            
	            if(numb == -1) {
	               break;
	            }
	         }
	      }

	   }
	
	public Links getLinks() {
		return this.links;
	}
}