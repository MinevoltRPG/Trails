package me.ccrama.Trails.objects;

import java.util.AbstractList;

import org.bukkit.Material;

public class Links extends AbstractList<Link>
{
	private Link[] list = new Link[10];
    private int size = 0;

    public Link get(int i){
        if(i >= size) throw new IndexOutOfBoundsException("duh!");
        return list[i];
    }

    public boolean add(Link e){
        if(size >= list.length){
            Link[] newList = new Link[list.length + 10];
            System.arraycopy(list,0, newList, 0, list.length);
            list = newList;
        }
        list[size] = e;
        size++;
        return true;
    }

    public int size(){
        return size;
    }
    
    public Link getFromMat(Material mat) {
  	  	if(size!=0){
  		  	for(Link link : list) {
  			  	if(link.getMat() == mat) {
  				  	return link;
  			  	}
  		  	}
  	  	}
  	  	return null;
    }
    
}