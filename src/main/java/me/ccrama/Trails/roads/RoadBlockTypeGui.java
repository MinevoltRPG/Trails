package me.ccrama.Trails.roads;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoadBlockTypeGui extends ChestGui {
    public RoadBlockTypeGui(RoadBlock roadBlock) {
        super(3, "Road block editor");
        List<RoadBlockType> list = roadBlock.materialWeight.stream().sorted((rbt1, rbt2) -> (int) (rbt1.weight-rbt2.weight)).collect(Collectors.toList());

        int rows = (int)Math.min(6, Math.ceil(list.size()/7.0) + 2);
        this.setRows(rows);

        OutlinePane background1 = new OutlinePane(0,0,9,rows, Pane.Priority.LOWEST);
        ItemStack bg1 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta1 = bg1.getItemMeta();
        meta1.setDisplayName(" ");
        bg1.setItemMeta(meta1);
        background1.addItem(new GuiItem(bg1, inventoryClickEvent -> inventoryClickEvent.setCancelled(true)));
        background1.setRepeat(true);
        this.addPane(background1);

        OutlinePane background2 = new OutlinePane(1,1,7,rows-2, Pane.Priority.LOW);
        ItemStack bg2 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta2 = bg2.getItemMeta();
        meta2.setDisplayName(" ");
        bg2.setItemMeta(meta2);
        background2.addItem(new GuiItem(bg2, inventoryClickEvent -> inventoryClickEvent.setCancelled(true)));
        background2.setRepeat(true);
        this.addPane(background2);

        PaginatedPane pane = new PaginatedPane(1,1,7,rows-2);
        List<GuiItem> guiItems = new ArrayList<>();
        for(RoadBlockType roadBlockType : list){
            Material mat = roadBlockType.material;
            double weight = roadBlockType.weight;
            String roadName = roadBlockType.trailName;
            Integer walks = roadBlockType.trailWalks;

            if(mat == Material.AIR) mat = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
            ItemStack stack = new ItemStack(mat);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+roadBlockType.material.name()));
            List<String> lore = new ArrayList<>();
            lore.add("Weight: "+weight);
            lore.add("Trail id: "+roadName);
            lore.add("Walks: "+walks);
            meta.setLore(lore);
            stack.setItemMeta(meta);

            GuiItem guiItem = new GuiItem(stack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
            guiItems.add(guiItem);
        }
        pane.populateWithGuiItems(guiItems);
        this.addPane(pane);
    }
}
