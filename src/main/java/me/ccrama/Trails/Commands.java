package me.ccrama.Trails;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    private Trails plugin;

    public Commands(Trails plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (command.getName().equalsIgnoreCase(plugin.getLanguage().command)) {

                if (args.length == 0) {
                    if (sender instanceof Player)
                        switchTrails((Player) sender, plugin.getToggles().isDisabled(((Player) sender).getUniqueId().toString()));
                    else sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().consoleSpecify));
                    return true;
                } else if (args.length == 1) {
                    if(args[0].equalsIgnoreCase("reload")){
                        reload(sender);
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
                                sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().notPlayedBefore));
                        }
                        return true;
                    } else if (sender instanceof ConsoleCommandSender) {
                        String playerName = args[0];
                        UUID uuid = getOfflinePlayerUUID(playerName);
                        if (uuid != null)
                            switchTrails(sender, uuid.toString(), args[0], plugin.getToggles().isDisabled(uuid.toString()));
                        else sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().notPlayedBefore));
                        return true;
                    }
                    wrongArgs(sender);
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("on")) switchTrails(sender, getUUIDString(args[1]), args[1], true);
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
                    sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().tooManyArgs));
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
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().toggledOn)); //send toggled on message to player
                } else {
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().alreadyOn)); //send toggled off message to player
                }
            } else {
                player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().noPerm));
            }
        } else {
            if (player.hasPermission("trails.toggle")) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().alreadyOff)); //send toggled on message to player
                } else {
                    this.plugin.getToggles().disablePlayer(uuid);
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().toggledOff)); //send toggled off message to player
                }
            } else {
                player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().noPerm));
            }
        }
    }

    public void switchTrails(CommandSender sender, String uuid, String playerName, boolean on) {
        if (uuid == null || sender == null){
            sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().notPlayedBefore));
            return;
        }
        boolean temp = true;
        if(sender instanceof Player) temp = ((Player) sender).hasPermission("trails.other");
        if (on) {
            if (temp) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    this.plugin.getToggles().enablePlayer(uuid);
                    sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().toggledOnOther));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(getFormattedMessage(player1.getName(), plugin.getLanguage().toggledOn));
                    }
                } else {
                    sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().alreadyOn));
                }
            } else {
                sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().noPerm));
            }
        } else {
            if (temp) {
                if (this.plugin.getToggles().isDisabled(uuid)) {
                    sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().alreadyOff));
                } else {
                    this.plugin.getToggles().disablePlayer(uuid);
                    sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().toggledOffOther));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(getFormattedMessage(player1.getName(), plugin.getLanguage().toggledOff));
                    }
                }
            } else {
                sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().noPerm));
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
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().boostOn)); //send toggled on message to player
                } else {
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().alreadyOnBoost)); //send toggled off message to player
                }
            } else {
                player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().noPermBoost));
            }
        } else {
            if (player.hasPermission("trails.toggle-boost")) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().alreadyOffBoost)); //send toggled on message to player
                } else {
                    this.plugin.getToggles().disableBoost(uuid);
                    player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().boostOff)); //send toggled off message to player
                }
            } else {
                player.sendMessage(getFormattedMessage(player.getName(), plugin.getLanguage().noPermBoost));
            }
        }
    }

    public void switchBoost(CommandSender sender, String uuid, String playerName ,boolean on) {
        if (uuid == null || sender == null){
            sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().notPlayedBefore));
            return;
        }
        boolean temp = true;
        if(sender instanceof Player) temp = ((Player) sender).hasPermission("trails.toggle-boost.other");
        if (on) {
            if (temp) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    this.plugin.getToggles().enableBoost(uuid);
                    sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().toggledOnBoostOther));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(getFormattedMessage(player1.getName(), plugin.getLanguage().boostOn));
                    }
                } else {
                    sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().alreadyOnBoostOther)); //send toggled off message to player
                }
            } else {
                sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().noPermBoost));
            }
        } else {
            if (temp) {
                if (!this.plugin.getToggles().isBoost(uuid)) {
                    sender.sendMessage(getFormattedMessage(playerName, plugin.getLanguage().alreadyOffBoostOther)); //send toggled on message to player
                } else {
                    this.plugin.getToggles().disableBoost(uuid);
                    sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().toggledOffBoostOther));
                    if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).isOnline()) {
                        Player player1 = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer();
                        if (player1 != null && !player1.equals(sender))
                            player1.sendMessage(getFormattedMessage(player1.getName(), plugin.getLanguage().boostOff));
                    }
                }
            } else {
                sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().noPermBoost));
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
        sender.sendMessage(getFormattedMessage(sender.getName(), plugin.getLanguage().wrongArgs));
    }


    public void reload(CommandSender p) {
        if (p.hasPermission("trails.reload")) {
            plugin.getToggles().saveUserList(false);
            plugin.reloadConfig();
            plugin.onDisable();
            plugin.onLoad();
            plugin.onEnable();
            p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().reload));
        } else {
            p.sendMessage(getFormattedMessage(p.getName(), plugin.getLanguage().reloadNoPerm));
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
        message = message.replace("%plugin_prefix%", plugin.getLanguage().pluginPrefix);
        message = message.replace("%name%", name);
        message = message.replace("%command%", plugin.getLanguage().command);
        // Do Colors
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }
}
