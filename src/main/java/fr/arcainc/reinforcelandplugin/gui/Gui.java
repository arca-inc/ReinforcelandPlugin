package fr.arcainc.reinforcelandplugin.gui;

import fr.arcainc.reinforcelandplugin.ReinforceLandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

/**
 * The base abstract class for creating graphical user interfaces (GUIs) in Bukkit/Spigot.
 * This class serves as a template for creating custom GUIs by providing essential methods
 * and properties.
 */
public abstract class Gui implements Listener {
    /**
     * The inventory of the GUI.
     */
    protected final Inventory inventory;

    /**
     * The player for whom the GUI is created.
     */
    protected Player player;

    protected BukkitTask task;

    /**
     * Creates a new instance of the Gui class.
     *
     * @param title  The title of the GUI window.
     * @param size   The size of the GUI, typically specified as a multiple of 9 (e.g., 9, 18, 27).
     */
    public Gui(String title, int size) {
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * Initializes the contents of the GUI. This method should be overridden in derived classes
     * to add items and customize the GUI's appearance.
     */
    public abstract void initialize();

    /**
     * Opens the GUI for the player.
     */
    public void open(Player player) {
        this.player = player;
        player.openInventory(inventory);
        registerSelfListener();
        initialize();
    }

    /**
     * Handles a click event in the GUI at the specified slot. This method should be overridden
     * in derived classes to define the behavior of the GUI in response to clicks.
     *
     * @param slot The slot in which the click event occurred.
     */
    public abstract void handleClick(int slot, InventoryClickEvent event);

    /**
     * Updates the GUI. This method can be overridden in derived classes to implement
     * periodic updates or dynamic content changes.
     */
    public abstract void update();

    public void startUpdater() {
        task = Bukkit.getScheduler().runTaskTimer(ReinforceLandPlugin.getPlugin(ReinforceLandPlugin.class), this::update, 1, 1);
    }

    public void stopUpdater() {
        task.cancel();
    }

    /**
     * Registers the GUI as a listener for click events.
     */
    protected void registerSelfListener() {
        Bukkit.getPluginManager().registerEvents(this, ReinforceLandPlugin.getPlugin(ReinforceLandPlugin.class));
    }

    /**
     * Unregisters the GUI as a listener for click events.
     */
    protected void unregisterSelfListener() {
        InventoryClickEvent.getHandlerList().unregister(this);
    }

    protected void close() {
        unregisterSelfListener();
        startUpdater();
    }

    @EventHandler
    public void mouseClick(InventoryClickEvent event) {
        if(event.getViewers().get(0) != null && event.getViewers().get(0).equals(player))
            handleClick(event.getSlot(), event);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        if(event.getPlayer().equals(player)) {
            unregisterSelfListener();
        }
    }
}