package me.kitskub.hungergames.utils.config;

import java.util.List;
import java.util.Set;
import me.kitskub.hungergames.utils.Cuboid;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author garbagemule
 */
public class ConfigSectionImpl extends MemorySection implements ConfigSection {

    private Config config;
    private String path;

    public ConfigSectionImpl(Config config, String path) {
        super(config, path);
        this.config = config;
        this.path = (path.endsWith(".") ? path : path + ".");
    }
    
    @Override
    public Config getParent() {
        return config;
    }
    
    public String getPath() {
        return path.substring(0, path.length() - 2);
    }

    public ConfigSectionImpl getConfigSection(String path) {
        return new ConfigSectionImpl(config, this.path + path);
    }

    @Override
    public Object get(String node) {
        return config.get(path + node);
    }

    @Override
    public int getInt(String node) {
        return config.getInt(path + node);
    }

    @Override
    public int getInt(String node, int def) {
        return config.getInt(path + node, def);
    }

    @Override
    public double getDouble(String node) {
        return config.getDouble(path + node);
    }

    @Override
    public double getDouble(String node, double def) {
        return config.getDouble(path + node, def);
    }

    @Override
    public boolean getBoolean(String node) {
        return config.getBoolean(path + node);
    }

    @Override
    public boolean getBoolean(String node, boolean def) {
        return config.getBoolean(path + node, def);
    }

    @Override
    public String getString(String node) {
        return config.getString(path + node);
    }

    @Override
    public String getString(String node, String def) {
        return config.getString(path + node, def);
    }

    public Location getLocation(String node) {
        return config.getLocation(path + node);
    }

    public Location getLocation(String node, Location def) {
        return config.getLocation(path + node, def);
    }

    public Cuboid getCuboid(String node) {
        return config.getCuboid(path + node);
    }

    public Cuboid getCuboid(String node, Cuboid def) {
        return config.getCuboid(path + node, def);
    }

    @Override
    public ItemStack getItemStack(String path) {
        return config.getItemStack(path);
    }

    @Override
    public ItemStack getItemStack(String path, ItemStack def) {
        return config.getItemStack(path, def);
    }

    public Set<String> getKeys() {
        return config.getKeys(path);
    }

    public Set<String> getKeys(String node) {
        return config.getKeys(path + node);
    }

    @Override
    public List<String> getStringList(String node) {
        return config.getStringList(path + node);
    }

    public List<String> getStringList(String node, List<String> def) {
        return config.getStringList(path + node, def);
    }

    @Override
    public void set(String node, Object value) {
        config.set(path + node, value);
    }

    @Override
    public ConfigSection createSection(String path) {
        return config.createSection(this.path + path);
    }
    
    public boolean save() {
        return config.save();
    }

    public boolean reload() {
        return config.reload();
    }

    @Override
    public boolean contains(String path) {
        return config.contains(this.path + path);
    }

    public void remove(String path) {
        config.remove(this.path + path);
    }

    public boolean exists() {
        return config.getConfigurationSection(path) != null;
    }

}
