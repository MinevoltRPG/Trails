package me.ccrama.Trails.objects;

import org.bukkit.Material;

public class Link {
   private final Material mat;
   private final int decay;
   private final int id;
   private final int chance;
   private final float speedBoost;
   private final Link next;
   private Link previous = null;
   private String trailName;

   public Link(Material material, int decaynum, int chance, float speedBoost, int linknumb, Link nextlink, String trailName) {
      this.mat = material;
      this.decay = decaynum;
      this.id = linknumb;
      this.speedBoost = speedBoost;
      this.chance = chance;
      this.next = nextlink;
      this.trailName = trailName;
   }

   public Material getMat() {
      return this.mat;
   }

   public float getSpeedBoost(){ return this.speedBoost;}

   public int decayNumber() {
      return this.decay;
   }

   public int identifier() {
      return this.id;
   }

   public Link getPrevious() {
      return previous;
   }

   public void setPrevious(Link previous) {
      this.previous = previous;
   }

   public Link getNext() {
      return this.next;
   }

   public int chanceOccurance() {
      return this.chance;
   }

   public String getTrailName() {
      return trailName;
   }
}
