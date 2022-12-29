package me.ccrama.Trails;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(Trails.roadMap.containsKey(player)){
                Trails.roadMap.remove(player);
                player.sendMessage("Roads disabled");
            }
            else{
                Trails.roadMap.put(player, "");
                player.sendMessage("Roads enabled");
            }
            return true;
        }

        return false;
    }
}
