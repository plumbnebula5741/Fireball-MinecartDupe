package plumb.fireball;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FireballDupe extends JavaPlugin implements Listener {

    // Set the multiplier here
    private final int dupeMultiplier = 2;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("minecartdupe disabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("minecartdupe disabled");
    }

    @EventHandler
    public void onCartDestroyed(VehicleDestroyEvent event) {
        Entity vehicle = event.getVehicle();

        // Only chest or hopper minecarts
        if (!(vehicle instanceof StorageMinecart) && !(vehicle instanceof HopperMinecart)) return;

        // Only if destroyed by a fireball
        Entity attacker = event.getAttacker();
        if (!(attacker instanceof Fireball) && !(attacker instanceof SmallFireball)) return;

        // Prevent vanilla drops
        event.setCancelled(true);

        // Handle StorageMinecart
        if (vehicle instanceof StorageMinecart) {
            StorageMinecart cart = (StorageMinecart) vehicle;
            dropMultiplied(cart.getInventory().getContents(), cart);
            cart.getInventory().clear(); // clear contents
            cart.remove(); // remove entity
            // Optionally return the cart item
            cart.getWorld().dropItemNaturally(cart.getLocation(),
                    new ItemStack(org.bukkit.Material.STORAGE_MINECART, 1));
        }

        // Handle HopperMinecart
        if (vehicle instanceof HopperMinecart) {
            HopperMinecart cart = (HopperMinecart) vehicle;
            dropMultiplied(cart.getInventory().getContents(), cart);
            cart.getInventory().clear(); // clear contents
            cart.remove();
            cart.getWorld().dropItemNaturally(cart.getLocation(),
                    new ItemStack(org.bukkit.Material.HOPPER_MINECART, 1));
        }
    }

    private void dropMultiplied(ItemStack[] contents, Entity cart) {
        for (ItemStack item : contents) {
            if (item == null) continue;

            // Multiply the amount
            int total = item.getAmount() * dupeMultiplier;

            // If bigger than max stack size, split into stacks
            while (total > 0) {
                int stackSize = Math.min(item.getMaxStackSize(), total);
                ItemStack clone = item.clone();
                clone.setAmount(stackSize);
                cart.getWorld().dropItemNaturally(cart.getLocation(), clone);
                total -= stackSize;
            }
        }
    }
}