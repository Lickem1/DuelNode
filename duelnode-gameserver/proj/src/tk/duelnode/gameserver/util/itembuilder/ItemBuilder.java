package tk.duelnode.gameserver.util.itembuilder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import tk.duelnode.gameserver.GameServer;

import java.lang.reflect.Field;
import java.util.*;

public class ItemBuilder implements Listener {

    @Getter
    @Setter
    private ItemStack itemStack;
    @Getter
    @Setter
    private ItemEvent itemEvent;
    private final HashSet<NBTTagCompound> tags = new HashSet<>();

    public ItemBuilder(Material material, int amount, int id) {
        this.itemStack = new ItemStack(material, amount, (short) id);
    }

    public ItemBuilder(Material material, int amount, int id, ItemEvent itemEvent) {
        this.itemStack = new ItemStack(material, amount, (short) id);
        this.itemEvent = itemEvent;
    }

    public ItemBuilder(ItemStack stack) {
        this.itemStack = stack;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSkullOwner(Player p) {
        SkullMeta a = (SkullMeta) itemStack.getItemMeta();
        a.setOwningPlayer(p);
        itemStack.setItemMeta(a);
        return this;
    }

    public ItemBuilder setCustomSkull(String ui) {
        SkullMeta a = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", ui));

        try {
            Field profileField = a.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(a, profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        itemStack.setItemMeta(a);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int lvl) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(enchantment, lvl, true);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(ChatColor.translateAlternateColorCodes('&', s));
        meta.setLore(l);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> list) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(list);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag... itemFlag) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(itemFlag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addTag(NBTTagCompound... nbtTagCompounds) {
        tags.addAll(Arrays.asList(nbtTagCompounds));
        return this;
    }

    public ItemStack build() {
        net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound uid = new NBTTagCompound();
        uid.setString("uid", UUID.randomUUID().toString());
        tags.add(uid);

        for (NBTTagCompound nbt : tags) {
            for (String s : nbt.c()) {
                itemNms.getTag().set(s, nbt.get(s));
            }
        }
        itemStack = CraftItemStack.asBukkitCopy(itemNms);
        Bukkit.getServer().getPluginManager().registerEvents(this, GameServer.getInstance());
        return itemStack;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action a = e.getAction();
        if (a == Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK || e.getItem() == null || e.getItem().getType() == Material.AIR)
            return;

        if (e.getItem().equals(itemStack)) {
            if (itemEvent != null && e.getHand() == EquipmentSlot.HAND) {
                e.setCancelled(true);
                itemEvent.e(e.getPlayer());
            }
        }
    }
}
