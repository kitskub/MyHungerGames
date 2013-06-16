package me.kitskub.hungergames.utils.config;

import java.util.List;
import java.util.Set;
import me.kitskub.hungergames.utils.Cuboid;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author garbagemule
 * @author kitskub
 */
public interface ConfigSection extends ConfigurationSection {
    
    public Config getParent();
        
    public String getPath();

    public ConfigSection getConfigSection(String path);

    public Location getLocation(String node);

    public Location getLocation(String node, Location def);

    public Cuboid getCuboid(String node);

    public Cuboid getCuboid(String node, Cuboid def);

    public ItemStack getItemStack(String path);

    public ItemStack getItemStack(String path, ItemStack def);

    public Set<String> getKeys();

    public Set<String> getKeys(String node);

    public List<String> getStringList(String node, List<String> def);
    
    public ConfigSection createSection(String path);
    
    public boolean save();

    public boolean reload();
    
    public void remove(String path);
    
    public boolean exists();
    
}
