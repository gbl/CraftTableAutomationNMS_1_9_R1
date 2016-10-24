/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.bukkit.crafttableautomation.helpers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.ContainerWorkbench;
import net.minecraft.server.v1_9_R1.World;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftShapelessRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 *
 * @author gbl
 */
public class CraftItemEventHelper_1_9_R1 implements CraftItemEventHelper {

    /**
     *
     * Finds the world position of the (workbench) object that the current
     * inventory is associated with. Uses NMS.
     * 
     * @param view
     *  The inventory view
     * @return
     *  The Location of the workbench
     */

    @Override
    public Location getInventoryViewLocation(InventoryView view) {
        CraftInventoryView cview=(CraftInventoryView)view;
        ContainerWorkbench workbench=(ContainerWorkbench) cview.getHandle();        
        try {
            BlockPosition position;
            Field field=workbench.getClass().getDeclaredField("h");
            field.setAccessible(true);
            position=(BlockPosition)field.get(workbench);
            
            World world;
            field=workbench.getClass().getDeclaredField("g");
            field.setAccessible(true);
            world=(World)field.get(workbench);
            
            return new Location(world.getWorld(), position.getX(), position.getY(), position.getZ());
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException ex) {
            getLogger().log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Player getPlayer(InventoryHolder holder) {
        return ((CraftPlayer)holder).getPlayer();
    }

    @Override
    public Collection<ItemStack> getIngredientCollection(Recipe recipe) {
        getLogger().log(Level.FINE, "recipe is "+recipe.toString());
        if (recipe instanceof CraftShapedRecipe) {
            Map<Character, ItemStack> map=((CraftShapedRecipe)recipe).getIngredientMap();
            getLogger().log(Level.FINE, "map is "+map.toString());
            return map.values();
        } else if (recipe instanceof CraftShapelessRecipe) {
            return ((CraftShapelessRecipe)recipe).getIngredientList();
        } else {
            getLogger().log(Level.WARNING, "recipe class is " + recipe.getClass().getCanonicalName());
            return null;
        }
    }
}
