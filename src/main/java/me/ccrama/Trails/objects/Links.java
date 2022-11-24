package me.ccrama.Trails.objects;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.Material;

public class Links
{
    private final HashMap<Material, ArrayList<Link>> linkHashMap = new HashMap<>();

    public boolean add(Link e){
        ArrayList<Link> linkList = linkHashMap.get(e.getMat());
        if(linkList == null) linkList = new ArrayList<>();
        linkList.add(e);
        linkList.sort(Comparator.comparingInt(Link::identifier));
        linkHashMap.put(e.getMat(), linkList);
        return true;
    }
    
    public ArrayList<Link> getFromMat(Material mat) {
  	  	return linkHashMap.get(mat);
    }
    
}