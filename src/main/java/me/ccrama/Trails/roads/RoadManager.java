package me.ccrama.Trails.roads;

import java.util.HashMap;
import java.util.Map;

public class RoadManager {

    public static Map<String, Road> roads = new HashMap<>();

    public static void addRoad(Road road){
        if(roads.containsKey(road.name)) return;
        roads.put(road.name, road);
    }

}
