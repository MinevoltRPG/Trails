package me.ccrama.Trails.roads;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.ccrama.Trails.configs.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RoadBlockGui extends ChestGui {
    public RoadBlockGui(RoadBlock roadBlock, int x, int y, int z, RoadEditor roadEditor, boolean stiars) {
        super(3, "Road block editor");
        OutlinePane background1 = new OutlinePane(0,0,9,3, Pane.Priority.LOWEST);
        ItemStack bg1 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta1 = bg1.getItemMeta();
        meta1.setDisplayName(" ");
        bg1.setItemMeta(meta1);
        background1.addItem(new GuiItem(bg1, inventoryClickEvent -> inventoryClickEvent.setCancelled(true)));
        background1.setRepeat(true);
        this.addPane(background1);

        OutlinePane background2 = new OutlinePane(1,1,7,1, Pane.Priority.LOW);
        ItemStack bg2 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta2 = bg2.getItemMeta();
        meta2.setDisplayName(" ");
        bg2.setItemMeta(meta2);
        background2.addItem(new GuiItem(bg2, inventoryClickEvent -> inventoryClickEvent.setCancelled(true)));
        background2.setRepeat(true);
        this.addPane(background2);

        StaticPane pane = new StaticPane(0,0,9,3, Pane.Priority.HIGH);
        ItemStack blockTypes = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta typesMeta = blockTypes.getItemMeta();
        typesMeta.setDisplayName(Language.getString("roads.block-types-display", null, null));
        typesMeta.setLore(Language.getStringList("roads.block-types-lore", null, null));
        blockTypes.setItemMeta(typesMeta);
        GuiItem blockItem = new GuiItem(blockTypes, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            (new RoadBlockTypeGui(roadBlock)).show(inventoryClickEvent.getWhoClicked());
        });
        pane.addItem(blockItem, 1,1);
        this.addPane(pane);

        if(!stiars) {
            ItemStack setAxisStack = new ItemStack(Material.STICK);
            ItemMeta axisMeta = setAxisStack.getItemMeta();
            axisMeta.setDisplayName(Language.getString("roads.axis-display", null, null));
            axisMeta.setLore(Language.getStringList("roads.axis-lore", null, null));
            setAxisStack.setItemMeta(axisMeta);
            GuiItem axisItem = new GuiItem(setAxisStack, inventoryClickEvent -> {
                roadEditor.setAxis(x, y);
                inventoryClickEvent.setCancelled(true);
            });
            pane.addItem(axisItem, 3, 1);
        }

    }
}
