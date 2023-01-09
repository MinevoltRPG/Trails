package me.ccrama.Trails.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.roads.Road;
import me.ccrama.Trails.roads.RoadManager;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonStorage {

    public static void loadData(){
        Gson gson = new Gson();
        loadRoads(gson);
    }

    public static void loadRoads(Gson gson){
        File file = new File(Trails.getInstance().getDataFolder().getAbsolutePath()+File.separator+"roads");
        try{
            file.mkdirs();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        for(File file1 : Arrays.stream(file.listFiles()).filter(f -> f.isFile()).collect(Collectors.toList())){
            Reader reader = null;
            try{
                reader = new FileReader(file1);
            } catch (Exception ex){
                ex.printStackTrace();
            }

            try{
                Type listType = new TypeToken<Road>(){}.getType();
                RoadManager.addRoad(gson.fromJson(reader, listType));
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }


    }

    public static void saveRoads() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Trails.getInstance().getDataFolder().getAbsolutePath()+File.separator+"roads");
        try{
            file.mkdirs();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        for(Road road : RoadManager.getRoads()) {
            Writer writer;
            File file1 = new File(file.getAbsolutePath()+File.separator+road.name+".json");
            if(!file1.exists()){
                try {
                    file1.createNewFile();
                } catch (Exception exception){
                    exception.printStackTrace();
                }

            }
            try {
                writer = new FileWriter(file1, false);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            gson.toJson(road, writer);
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
