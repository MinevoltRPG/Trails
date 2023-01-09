package me.ccrama.Trails;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.ccrama.Trails.configs.Language;
import me.ccrama.Trails.util.ParticleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleEffect;

public class TrailCommand implements CommandExecutor {
    private Trails plugin;

    public TrailCommand(Trails plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase(plugin.getLanguage().command)) {

                if (args.length == 0) {
                    if (sender instanceof Player)
                        switchTrails((Player) sender, plugin.getToggles().isDisabled(((Player) sender).getUniqueId().toString()));
                    else sender.sendMessage(Language.getString("messages.consoleSpecify", sender.getName()));
                    return true;
                } else if (args.length == 1) {
                    if(args[0].equalsIgnoreCase("reload")){
                        reload(sender);
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("show")){
                        if(!sender.hasPermission("trails.show")) {
                            sender.sendMessage(Language.getString("messages.noPerm", sender.getName()));
                            return true;
                        }
                        if(sender instanceof Player) {
                            ParticleUtil.showTrailBlocks((Player) sender, ((Player)sender).getLocation(), 30.0, Trails.config.trailParticle);
                            sender.sendMessage(Language.getString("messages.showing-trails", sender.getName()));
                        }
                        else{
                            sender.sendMessage("Only for players!");
                        }
                        return true;
                    }
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (args[0].equalsIgnoreCase("on")) switchTrails(p, true);
                        else if (args[0].equalsIgnoreCase("off")) switchTrails(p, false);
                        else if (args[0].equalsIgnoreCase("boost"))
                            switchBoost(p, !plugin.getToggles().isBoost(p.getUniqueId().toString()));
                        else {
                            String playerName = args[0];
                            UUID uuid = getOfflinePlayerUUID(playerName);
                            if (uuid != null)
                                switchTrails(sender, uuid.toString(), args[0], plugin.getToggles().isDisabled(uuid.toString()));
                            else
                                sender.sendMessage(Language.getString("messages.notPlayedBefore", playerName));
                        }
                        return true;
                    } else if (sender instanceof ConsoleCommandSender) {
                        String playerName = args[0];
                        UUID uuid = getOfflinePlayerUUID(playerName);
                        if (uuid != null)
                            switchTrails(sender, uuid.toString(), args[0], plugin.getToggles().isDisabled(uuid.toString()));
                        else sender.sendMessage(Language.getString("messages.notPlayedBefore", playerName));
                        return true;
                    }
                    wrongArgs(sender);
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("on")) switchTrails(sender, getUUIDString(args[1]), args[1], true);
                    else if (args[0].equalsIgnoreCase("show")){
                        if(!sender.hasPermission("trails.show")) {
                            sender.sendMessage(Language.getString("messages.noPerm", sender.getName()));
                            return true;
                        }
                        if(sender instanceof Player player) {
                            double rad = 30.0;
                            try {
                                rad = Double.parseDouble(args[1]);
                            } catch (Exception ex) {
                                //ex.printStackTrace();
                                sender.sendMessage(Language.getString("messages.second-argument-not-double", sender.getName()));
                            }
                            ParticleUtil.showTrailBlocks((Player) sender,((Player) sender).getLocation(),  rad, Trails.config.trailParticle);
                            Map<String, String> replace = Map.of("%radius%", (int)rad+"");
                            sender.sendMessage(Language.getString("messages.showing-trails-radius", replace, player));
                        } else {
                            sender.sendMessage(Language.getString("messages.only-layers", sender.getName()));
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("off")) switchTrails(sender, getUUIDString(args[1]), args[1], false);
                    else if (args[0].equalsIgnoreCase("boost")){
                        if(sender instanceof Player){
                            Player player = (Player) sender;
                            if(args[1].equalsIgnoreCase("on")) switchBoost(player, true);
                            else if(args[1].equalsIgnoreCase("off")) switchBoost(player, false);
                            else switchBoost(sender, getUUIDString(args[1]), args[1], !getCurrentBoostState(args[1]));
                        } else{
                            switchBoost(sender, getUUIDString(args[1]), args[1], !getCurrentBoostState(args[1]));
                        }
                        return true;
                    }
                    else {
                        wrongArgs(sender);
                    }
                    return true;
                } else if (args.length == 3) {
                    if(args[0].equalsIgnoreCase("boost") && args[1].equalsIgnoreCase("on")) switchBoost(sender, getUUIDString(args[2]), args[2], true);
                    else if(args[0].equalsIgnoreCase("boost") && args[1].equalsIgnoreCase("off")) switchBoost(sender, getUUIDString(args[2]), args[2], false);
                    else{
                        wrongArgs(sender);
                    }
                    return true;
                } else {
                    sender.sendMessage(Language.getString("messages.tooManyArgs", null, null));
                    return true;
                }
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(getFormattedMessage("", "%plugin_prefix% has encountered a serious error."));
            Bukkit.getConsoleSender().sendMessage(getFormattedMessage("", "%plugin_prefix% Please report to the author"));
            Bukkit.getConsoleSender().sendMessage(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void switchTrails(Player player, boolean on) {
        if (player == null) return;
        String uuid = player.getUniqueId().toString();
        if (on) {
            if (player.hasPermission("trails.toggle")) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    this.plugin.getToggles().enablePlayer(uuid);
                    player.sendMessage(Language.getString("messages.toggledOn", null, player)); //send toggled on message to player
                } else {
                    player.sendMessage(Language.getString("messages.alreadyOn", null, player)); //send toggled off message to player
                }
            } else {
                player.sendMessage(Language.getString("messages.noPerm", null, player));
            }
        } else {
            if (player.hasPermission("trails.toggle")) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    player.sendMessage(Language.getString("messages.alreadyOff", null, player)); //send toggled on message to player
                } else {
                    this.plugin.getToggles().disablePlayer(uuid);
                    player.sendMessage(Language.getString("messages.toggledOff", null, player)); //send toggled off message to player
                }
            } else {
                player.sendMessage(Language.getString("messages.noPerm", null, player));
            }
        }
    }

    public void switchTrails(CommandSender sender, String uuid, String playerName, boolean on) {
        if (uuid == null || sender == null){
            sender.sendMessage(Language.getString("messages.notPlayedBefore", playerName));
            return;
        }
        boolean temp = true;
        if(sender instanceof Player) temp = sender.hasPermission("trails.other");
        if (on) {
            if (temp) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    this.plugin.getToggles().enablePlayer(uuid);
                    sender.sendMessage(Language.getString("messages.toggledOnOther", playerName));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(Language.getString("messages.toggledOn", null, player1));
                    }
                } else {
                    sender.sendMessage(Language.getString("messages.alreadyOn", playerName));
                }
            } else {
                sender.sendMessage(Language.getString("messages.noPerm", null, null));
            }
        } else {
            if (temp) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    sender.sendMessage(Language.getString("messages.alreadyOff", playerName));
                } else {
                    this.plugin.getToggles().disablePlayer(uuid);
                    sender.sendMessage(Language.getString("messages.toggledOffOther", playerName));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(Language.getString("messages.toggledOff", null, player1));
                    }
                }
            } else {
                sender.sendMessage(Language.getString("messages.noPerm", null, null));
            }
        }
    }

    public void switchBoost(Player player, boolean on) {
        if (player == null) return;
        String uuid = player.getUniqueId().toString();
        if (on) {
            if (player.hasPermission("trails.toggle-boost")) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    this.plugin.getToggles().enableBoost(uuid);
                    player.sendMessage(Language.getString("messages.boostOn", null, player)); //send toggled on message to player
                } else {
                    player.sendMessage(Language.getString("messages.alreadyOnBoost", null, player)); //send toggled off message to player
                }
            } else {
                player.sendMessage(Language.getString("messages.noPermBoost", null, player));
            }
        } else {
            if (player.hasPermission("trails.toggle-boost")) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    player.sendMessage(Language.getString("messages.alreadyOffBoost", null, player)); //send toggled on message to player
                } else {
                    this.plugin.getToggles().disableBoost(uuid);
                    player.sendMessage(Language.getString("messages.boostOff", null, player)); //send toggled off message to player
                }
            } else {
                player.sendMessage(Language.getString("messages.noPermBoost", null, player));
            }
        }
    }

    public void switchBoost(CommandSender sender, String uuid, String playerName ,boolean on) {
        if (uuid == null){
            sender.sendMessage(Language.getString("messages.notPlayedBefore", playerName));
            return;
        }
        boolean temp = true;
        if(sender instanceof Player) temp = sender.hasPermission("trails.toggle-boost.other");
        if (on) {
            if (temp) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    this.plugin.getToggles().enableBoost(uuid);
                    sender.sendMessage(Language.getString("messages.toggledOnBoostOther", playerName));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(Language.getString("messages.boostOn", null, player1));
                    }
                } else {
                    sender.sendMessage(Language.getString("messages.alreadyOnBoostOther", null, null)); //send toggled off message to player
                }
            } else {
                sender.sendMessage(Language.getString("messages.noPermBoost", null, null));
            }
        } else {
            if (temp) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    sender.sendMessage(Language.getString("messages.alreadyOffBoostOther", null, null)); //send toggled on message to player
                } else {
                    this.plugin.getToggles().disableBoost(uuid);
                    sender.sendMessage(Language.getString("messages.toggledOffBoostOther", playerName));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(Language.getString("messages.boostOff", null, player1));
                    }
                }
            } else {
                sender.sendMessage(Language.getString("messages.noPermBoost", null, null));
            }
        }
    }

    public String getUUIDString(String playerName) {
        UUID uuid = getOfflinePlayerUUID(playerName);
        if (uuid == null) return null;
        return uuid.toString();
    }

    public boolean getCurrentTrailsState(String playerName) {
        String uuidStr = getUUIDString(playerName);
        if (uuidStr == null) return false;
        return !plugin.getToggles().isDisabled(uuidStr);
    }

    public boolean getCurrentBoostState(String playerName) {
        String uuidStr = getUUIDString(playerName);
        if (uuidStr == null) return false;
        return plugin.getToggles().isBoost(uuidStr);
    }

    public void wrongArgs(CommandSender sender) {
        if (sender == null) return;
        sender.sendMessage(Language.getString("messages.wrongArgs", null, null));
    }


    public void reload(CommandSender p) {
        if (p.hasPermission("trails.reload")) {
            plugin.getToggles().saveUserList(false);
            plugin.reloadConfig();
            plugin.onDisable();
            plugin.onLoad();
            plugin.onEnable();
            p.sendMessage(Language.getString("messages.reload", null, null));
        } else {
            p.sendMessage(Language.getString("messages.reloadNoPerm", null, null));
        }
    }

    private UUID getOfflinePlayerUUID(String playerName) {
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName().equalsIgnoreCase(playerName)) {
                return op.getUniqueId();
            }
        }
        return null;
    }

    public String getFormattedMessage(String name, String message) {
        message = message.replace("%plugin_prefix%", Language.pluginPrefix);
        message = message.replace("%name%", name);
        message = message.replace("%command%", Language.command);
        // Do Colors
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }
}
