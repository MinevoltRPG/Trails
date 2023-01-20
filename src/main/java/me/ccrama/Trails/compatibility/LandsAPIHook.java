package me.ccrama.Trails.compatibility;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.configs.Language;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandsAPIHook {
    private final LandsIntegration landsIntegration;
    private final RoleFlag roleflag;

    public LandsAPIHook(Trails plugin) {
        ItemStack icon = new ItemStack(plugin.getLanguage().material, 1);
        this.landsIntegration = LandsIntegration.of(plugin);
        //seems like should use RoleFlag instead of LandFlag
        this.roleflag = RoleFlag.of(landsIntegration, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "allow_trails");
        //this.roleflag = new RoleFlag(plugin, Category.ACTION, "ALLOW_TRAILS", plugin.getConfigManager().applyInSubAreas, plugin.getConfigManager().landsPathsWilderness);
        roleflag.setApplyInSubareas(plugin.getConfig().getBoolean("Plugin-Integration.Lands.ApplyInSubAreas", true));
        roleflag.setAlwaysAllowInWilderness(plugin.getConfig().getBoolean("Plugin-Integration.Lands.PathsInWilderness", true));
        roleflag.setDisplayName(plugin.getLanguage().displayName);
        roleflag.setIcon(icon);
        roleflag.setDescription(Language.getStringList("lands.flag.description", null, null));
        roleflag.setDisplay(true);
        plugin.getLogger().info(Language.pluginPrefix + ChatColor.GREEN + " hooked into " + ChatColor.YELLOW + "Lands!");
        plugin.getLogger().info(Language.pluginPrefix + ChatColor.YELLOW + " ALLOW_TRAILS " + ChatColor.GREEN + "Role Flag registered!");
        //landsIntegration.registerFlag(roleflag);
    }

    public boolean hasTrailsFlag(Player player, Location location) {
        if (location.getWorld() == null) return false;
        LandWorld landWorld = landsIntegration.getWorld(location.getWorld());
        if (landWorld == null) return true;

        return landWorld.hasRoleFlag(player, location, roleflag, null, false);
    }

}