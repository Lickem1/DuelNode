package tk.duelnode.lobby.data.player;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.data.menu.info.InfoMenu;
import tk.duelnode.lobby.data.npc.NPC;
import tk.duelnode.lobby.data.queue.Queue;
import tk.duelnode.lobby.data.queue.QueueManager;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.PlayerDataManager;
import tk.duelnode.lobby.util.itembuilder.ItemBuilder;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    @Setter(AccessLevel.NONE)
    private final UUID UUID;
    private Player player;
    private Queue current_Queue;
    public int scoreboard_title_tick = 0;

    public PlayerData(UUID uuid) {
        this.UUID = uuid;
    }

    public void createLobbyPlayer() {
        if (player == null) return;
        if (current_Queue != null) DynamicManager.get(QueueManager.class).removeFromQueue(this);

        ItemStack queue = new ItemBuilder(Material.DIAMOND_SWORD, 1, 0).setName("&bJoin Queue &7(Right Click)").build();
        ItemStack info = new ItemBuilder(Material.BOOK, 1, 0).setName("&bInfo &7(Right Click)").build();
        ItemStack disconnect = new ItemBuilder(Material.RED_ROSE, 1, 1).setName("&cDisconnect &7(Right Click)").build();

        player.getInventory().setItem(1, info);
        player.getInventory().setItem(4, queue);
        player.getInventory().setItem(7, disconnect);
        player.updateInventory();

    }

    public void queueItems() {
        if (player == null) return;
        ItemStack leaveQueue = new ItemBuilder(Material.REDSTONE, 1, 0).setName("&cLeave Queue &7(Right Click)").build();
        player.getInventory().setItem(4, leaveQueue);
        player.updateInventory();
    }

    public boolean isInQueue() {
        return (current_Queue != null);
    }

    public void handleNPC() {
        Location location = new Location(Bukkit.getWorld("world"), 0.5, 70, -8.5, 0, 0);
        Location location2 = new Location(Bukkit.getWorld("world"), -3, 70, -6, -27, 0);
        Location location3 = new Location(Bukkit.getWorld("world"), 4.5, 70, -6, 30, 0);
        Location location4 = new Location(Bukkit.getWorld("world"), -23.5, 70, -35.5, -90, 0);

        String spacemanV = "ewogICJ0aW1lc3RhbXAiIDogMTY2MjM5NTA3ODI2OCwKICAicHJvZmlsZUlkIiA6ICI4YjU5OGE5NmE0NTY0YTJjYjg1NmQ1YWM4NjdiNDllOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcGFjZXlzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFiOTcxNzAwZjlmZDljMzBiZmY0ZjU3YTE3N2RjNTc5NWY1OTRkZjgxOTcyMzY4ZDUwNTIzNzAwNDkzYjQ0ZmQiCiAgICB9CiAgfQp9";
        String spacemanS = "e9sPgFXj5mnNLYc3oa2NmKmsIM3ZZiAY7RDUhq9UxPHboKwkTms9Rpyd9haVybSqdTpXYj09jrQChZEl/xo9iqG3gnYw5xkitBIMOFeqUU4LaQHM6XNQgX5CWtXqcr7YwHmqU7Oy5F6ch52ACzyjQcPh/Rm53TBuqNUFU1h8eXO0nDALUK7QXP8rkKTkxSMhkLVv9XUDEAY0V1kWdg8I25V2r7CPz9aghIqi1+uU0jRIFilWNEZj6OvNS85sOWSbVfmvzp7ubESz1J26VRBBrmkSFxUzljDpBnO7lwWEdOr8Zl5Zzfe4U43fujq0TwwU3W3oqRLdV9DjvlkFUVgfPcJvVKd2EitU84IVpdYZptP/+lJAB4uDj4GjkYhxlcV7e98Z/1upGwwnhWwj5nXm3GsuPEPXKcNcd8iz86uDPg2Xc86Lop1zmdUof1VmUhOS7iIiwTcoQMgh37wokOOSJ4ELpGu9cSVHRhvgAFG7/E6gAspTvIUOXCdUiWTNJEGnWE6klCK1FUF6u2ZZ6ZACq7DrVoj+/DdW2ejVjjvZhNE7jU0MpyHWdddOMLGlMW3aPXiNq6trU0IyRxDyWkrZykc6XatcccIT7l0pxaJIN3nJhwI7oW35sYvCaaZy38o0g6NgsYgoOsQdClkiQQcN5nXe9kdhOGQKPU5a4cEoXKQ=";

        String terminalV = "ewogICJ0aW1lc3RhbXAiIDogMTY2MjMxMTIwNjU5OSwKICAicHJvZmlsZUlkIiA6ICJiMmIxNzk1M2VjZTA0ZjFjYjUxMjVjYjEwODVmZGY5ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJiY2kiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjk2OGVhYmFjMmMyZTFmMDRiODQzNDQxZTRmZjhjOGUzNTQzMDFiM2JhMDVkYzlmOGMyZmJlN2E1ZTgzNWVkMCIKICAgIH0KICB9Cn0=";
        String terminalS = "Od4PbVGWlTfQ4ev5Ha21zIMpedckiDXL+A2RhTuz+Gj/bla8TkAUE/Dryixw6J0ASycilx2z5RXNCNccVmb9horaZ4zsL4dv2hFjjJ0I3pKAb5/3Q/S44WVsDtPGkeg++KWyfFsqRvtapXrGuiUPztDTi74ASyP/Wz2DMYrbeWv/I3FpbodVVHeFh3PL71ha/4gXHWfkay+A/A6PK4auCvhyq5+URatomBeDzanrjSpdMRr+6D/6MhIZHAEkWjZUf1tcnKWhJKIHKpGEgKNQ9GoiKy5bJwFt+D98HtJFTDuIyCQPlwrIUD5MqFUojkVGZpcl5fnQxs+Rc48/XZ46gaf5ODy5gwaR83cQYYusftv0y+ao1ggVSLPFKliLcMNdT4eb4ktDICq4zD6Y+yr2wJgLTVdsZeM8S+f3Bfu4vRXXlGcUPCSOJkFF7SaU89wnqHq+AR6B2w3EXMcK83BckgooEG22KOWwSfGDSzdQZA/SZXFyE/V8sFmEMTuT4/uzvpjSsSIaRfWT6PONVeIiopuIWq9ZZbCSEnBvQckCBvWe84uO8eykUa8Uz8c0JxX5qM923g8PALpkqma50wy/Lum1LKhrXCgOjrpJ4QkZYq/Fq6n3c/+eHN6zBLwLJ30iL7Dyi2ekCfGfceXj4mXaDNrIhJkpHvCLxKEDYkbTn8c=";

        String disconnectV = "ewogICJ0aW1lc3RhbXAiIDogMTY2MjM5Njc1MDk4NiwKICAicHJvZmlsZUlkIiA6ICIwMDA0OTgyZWJhZjA0NTg3OWE0ZDU2MTM4OTFjOTk4MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJhcmNoaXZhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JlYzA4MTlhOTkwNTY5ZjAyNGQ0MDRmYTA4NjFlYTA0MzJlNjg3MzFmZGQ2YmQ2NDVkNGQ2ZWI3OTBhYWJkOWQiCiAgICB9CiAgfQp9";
        String disconnectS = "pREKr9RgH51tvmaB8BLj33TjW7IdruO4BIOxUh1+NzLelyCW5DZrowEeXE289WmPRitZbaMXwL2FQQ7RknH8JXCBQxxXNV//m3TqQLOZbJWPyjHGMQMNfxL3U+ywTSM0SnlUPKceleoynSKHsIc9Aehl2ifEEfUHYz8lw0cRujB6RH1xJddSxOQZxG9+dbmFHZFwYTbVVEPZKg3CdKOcfK4DiCgu5Q5aBY7CBAQ3Qk3ik/xcp8R+A19YvgbEJtacxdmv6J7XKXMEyG7m/nnrHRzId6TYE1UaE6/T1Z9jbj9tcM0djYE1O/QNYIhe7xzKqdQP1hYKolwP+sDWNjCoP2Ollx+n+VXVKOrwLvRXxSjiEOzmzVrEAgdJJ3lSpRJCuqch+IJ1zGL3wl7kVsc3440NyVCPktKebttTRndDqUBF4ohUsPvQYD3Ngo1aA9klUUgYcUs/DYZUXR8ikbdmToyz6FgwiolBZakyftszV4OrC9rDHtb9lcH0z6h3eN2HKHA6QV8bBrz8CZ+hVMGHE66Tew+e8Nr/oQukIj1D9ca85Y341iGXDMpdba0fTDU0N7l8Y2iYHbxJb7CN++KBm7SGeq8gDK1WtAPNM3GxLMDIz3zPMyZt7HhKj/RI3RgGHe6d8pc93wnZGjPTpaCXyxHskYzcFJDBNAXEN8K63Zc=";

        GameProfile playerProfile = ((CraftPlayer) player).getHandle().getProfile();
        Property playerProperty = playerProfile.getProperties().get("textures").stream().findFirst().orElse(null);


        NPC npc = new NPC(this.player, location, "Queue");
        npc.spawn()
                .changeSkin(playerProperty.getValue(), playerProperty.getSignature())
                .equipItem(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_12_R1.ItemStack(Items.DIAMOND_SWORD))
                .setNamePlateVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS)
                .setEvent(((npc1, player) -> {
                    PlayerData data = DynamicManager.get(PlayerDataManager.class).getProfile(player);
                    if (!data.isInQueue()) {
                        player.sendMessage(ChatColor.GREEN + "» " + ChatColor.GRAY + "You have joined the queue, please allow a few seconds for matchmaking!");
                        data.queueItems();
                        DynamicManager.get(QueueManager.class).addToQueue(data);
                    } else {
                        player.sendMessage(ChatColor.RED + "» " + ChatColor.GRAY + "You have left the queue!");
                        data.createLobbyPlayer();
                        DynamicManager.get(QueueManager.class).removeFromQueue(data);
                    }
                }));

        NPC npc2 = new NPC(this.player, location2, "Info");
        npc2.spawn()
                .changeSkin(terminalV, terminalS)
                .equipItem(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_12_R1.ItemStack(Items.ENCHANTED_BOOK))
                .setNamePlateVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS)
                .setEvent(((npc1, player) -> DynamicManager.get(InfoMenu.class).open(player)));

        NPC npc3 = new NPC(this.player, location3, "Disconnect");
        npc3.spawn()
                .changeSkin(disconnectV, disconnectS)
                .equipItem(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_12_R1.ItemStack(Items.REDSTONE))
                .setNamePlateVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS)
                .setEvent(((npc1, player) -> Bukkit.getServer().getScheduler().runTask(Plugin.getInstance(), () -> player.kickPlayer("Disconnected"))));

        NPC npc4 = new NPC(this.player, location3, "Teller");
        npc4.spawn()
                .changeSkin(spacemanV, spacemanS)
                .equipItem(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_12_R1.ItemStack(Items.EMERALD))
                .setNamePlateVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);

        npc4.teleport(location4);
    }
}
