package me.ccrama.Trails;

import me.ccrama.Trails.configs.Language;
import me.ccrama.Trails.roads.Road;
import me.ccrama.Trails.roads.RoadEditor;
import me.ccrama.Trails.roads.RoadManager;
import me.ccrama.Trails.util.RoadUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
/*
        if(sender instanceof Player){
            Player player = (Player) sender;

            if(args.length == 2){
                if(!player.hasPermission("trails.roads.edit")){
                    player.sendMessage(Language.getString("roads.no-edit-permission", player.getName()));
                    return true;
                }
                if(args[0].equalsIgnoreCase("edit")){
                    if(args[1].equalsIgnoreCase("stop")){
                        RoadManager.removeRoadEditor(player);
                        return true;
                    }
                    if(args[1].equalsIgnoreCase("expand")){
                        RoadEditor roadEditor = RoadManager.roadEditors.get(player);
                        if(roadEditor == null) return true;
                        roadEditor.expand(player.getLocation().getYaw(), player.getLocation().getPitch());
                        return true;
                    }
                    RoadManager.createRoadEditor(player, args[1], player.getLocation().getBlock());
                    return true;
                }
            }

            if(Trails.roadMap.containsKey(player)){
                Trails.roadMap.remove(player);
                RoadUtils.clearRoadCache(player);
                player.sendMessage("Roads disabled");
            }
            else if (args.length == 1){
                if(!player.hasPermission("trails.roads."+args[0])){
                    player.sendMessage(Language.getString("roads.no-permission", player.getName()));
                    return true;
                }
                Road road = RoadManager.roads.get(args[0]);
                Trails.roadMap.put(player, road);
                player.sendMessage("Roads enabled");
            }
            return true;
        }

        return false;*/
        return true;
    }
}
