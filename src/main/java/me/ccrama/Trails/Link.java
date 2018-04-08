package me.ccrama.Trails;

import java.util.HashMap;
import org.bukkit.Material;

import me.ccrama.Trails.Main.TypeAndData;

public class Link {
   private Material mat;
   private int decay;
   private int id;
   private int chanceoccur;
   private byte dataValue;
   private Link next;
   public static HashMap<TypeAndData, Link> matLinks = new HashMap<TypeAndData, Link>();

   public Link(Material material, byte dataValue, int decaynum, int chance, int linknumb, Link nextlink) {
      this.mat = material;
      this.dataValue = dataValue;
      this.decay = decaynum;
      this.id = linknumb;
      this.chanceoccur = chance;
      this.next = nextlink;
   }

   public Material getMat() {
      return this.mat;
   }

   public int decayNumber() {
      return this.decay;
   }
   
   public byte getDataValue() {
	   return this.dataValue;
   }

   public int identifier() {
      return this.id;
   }

   public Link getNext() {
      return this.next;
   }

   public int chanceOccurance() {
      return this.chanceoccur;
   }

   public Link getFromMat(Material mat) {
	  if(matLinks!=null){
		  for(TypeAndData mats : matLinks.keySet()){
			  if(mats.mat == mat){
				  return matLinks.get(mats);
			  }
		  } 
	  }
	  return null;
   }
}
