package me.ccrama.Trails.roads;

import me.ccrama.Trails.util.JsonStorage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadManager {

    public static Map<String, Road> roads = new HashMap<>();
    public static Map<Player, RoadEditor> roadEditors = new HashMap<>();

    public static void addRoad(Road road){
        if(roads.containsKey(road.name)) return;
        roads.put(road.name, road);
    }

    public static void createRoadEditor(Player player, String roadName, Block block){
        if(roadEditors.containsKey(player)) removeRoadEditor(player);
        Road road;
        if(!roads.containsKey(roadName)){
            road = new Road(roadName, 3, 3, 3, 1,0, 0);
        } else road = roads.get(roadName);
        addRoad(road);
        RoadEditor roadEditor = new RoadEditor(player, block, road);
        roadEditors.put(player, roadEditor);
    }

    public static void removeRoadEditor(Player player){
        if(!roadEditors.containsKey(player)) return;
        RoadEditor roadEditor = roadEditors.get(player);
        roads.put(roadEditor.road.name, roadEditor.road);
        roadEditor.removeEditor();
        roadEditors.remove(player);
        JsonStorage.saveRoads();
    }

    public static Collection<Road> getRoads(){
        return roads.values();
    }

}
