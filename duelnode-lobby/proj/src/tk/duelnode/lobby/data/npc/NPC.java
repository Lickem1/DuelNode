package tk.duelnode.lobby.data.npc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import tk.duelnode.lobby.Plugin;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.NPCManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public class NPC {

    private final UUID uid;
    private final int pid;
    private final String name;
    private final PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn;
    private final GameProfile gameProfile;
    private final List<Player> viewers = new ArrayList<>();

    private Location location;
    @Setter private INPCEvent event;

    public NPC(Player player, Location location) {
        this.uid = UUID.randomUUID();
        this.pid = new Random().nextInt(1000);
        this.name = "$NPC_" + pid;
        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
        this.gameProfile = new GameProfile(uid, name);
        this.location = location;
        viewers.add(player);
        register();
    }
    public NPC(Player player, Location location, GameProfile profile) {
        this.uid = UUID.randomUUID();
        this.pid = new Random().nextInt(1000);
        this.name = "$NPC_" + pid;
        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
        this.gameProfile = profile;
        this.location = location;
        viewers.add(player);
        register();
    }

    public NPC(Player player, Location location, String name) {
        this.uid = UUID.randomUUID();
        this.pid = new Random().nextInt(1000);
        this.name = name;
        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
        this.gameProfile = new GameProfile(uid, name);
        this.location = location;
        viewers.add(player);
        register();
    }

    public NPC(Player player, Location location, GameProfile profile, String name) {
        this.uid = UUID.randomUUID();
        this.pid = new Random().nextInt(1000);
        this.name = name;
        this.packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();
        this.gameProfile = profile;
        this.location = location;
        viewers.add(player);
        register();
    }

    private void register() {
        DataWatcher dataWatcher = new DataWatcher(null);
        dataWatcher.register(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte)127);
        set(packetPlayOutNamedEntitySpawn, "a", pid);
        set(packetPlayOutNamedEntitySpawn, "b", uid);
        set(packetPlayOutNamedEntitySpawn, "c", location.getX());
        set(packetPlayOutNamedEntitySpawn, "d", location.getY());
        set(packetPlayOutNamedEntitySpawn, "e", location.getZ());

        set(packetPlayOutNamedEntitySpawn, "f", (byte)location.getYaw());
        set(packetPlayOutNamedEntitySpawn, "g", (byte)location.getPitch());
        set(packetPlayOutNamedEntitySpawn, "h", dataWatcher);

        DynamicManager.get(NPCManager.class).create(this);
    }

    public NPC modifyTab(PacketPlayOutPlayerInfo.EnumPlayerInfoAction enumPlayerInfoAction) {
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData playerInfoData = packetPlayOutPlayerInfo.new PlayerInfoData(gameProfile, 0, EnumGamemode.NOT_SET, IChatBaseComponent.ChatSerializer.a(" "));
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) get(packetPlayOutPlayerInfo, "b");

        players.add(playerInfoData);

        set(packetPlayOutPlayerInfo, "a", enumPlayerInfoAction);
        set(packetPlayOutPlayerInfo, "b", players);

        sendPacket(packetPlayOutPlayerInfo);
        return this;
    }

    public NPC headRotation(float yaw, float pitch) {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(pid, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        set(packetHead, "a", pid);
        set(packetHead, "b", getFixRotation(yaw));

        sendPacket(packet, packetHead);
        return this;
    }

    public NPC equipItem(EnumItemSlot slot, ItemStack stack) {
        PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment();
        set(packetPlayOutEntityEquipment, "a", pid);
        set(packetPlayOutEntityEquipment, "b", slot);
        set(packetPlayOutEntityEquipment, "c", stack);
        sendPacket(packetPlayOutEntityEquipment);
        return this;
    }

    public NPC setNamePlateVisibility(ScoreboardTeamBase.EnumNameTagVisibility enumNameTagVisibility) {
        ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), name);
        team.setPrefix(ChatColor.GRAY + "NPC: " + ChatColor.WHITE);

        team.setNameTagVisibility(enumNameTagVisibility);
        List<String> playerToAdd = new ArrayList<>();
        playerToAdd.add(name);

        sendPacket(
                new PacketPlayOutScoreboardTeam(team, 1),
                new PacketPlayOutScoreboardTeam(team, 0),
                new PacketPlayOutScoreboardTeam(team, playerToAdd, 3)
        );
        return this;
    }

    public NPC teleport(Location location) {
        this.location = location;

         PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport();
         set(tp, "a", this.pid);
         set(tp, "b", location.getX());
         set(tp, "c", location.getY());
         set(tp, "d", location.getZ());
         set(tp, "e", (byte) location.getYaw());
         set(tp, "f", (byte) location.getPitch());
         set(tp, "g", true);

         sendPacket(tp);
         headRotation(location.getYaw(), location.getPitch());
         return this;
    }

    public NPC changeSkin(String value, String signature) {
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
        update();
        return this;
    }

    public NPC spawn() {
        modifyTab(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        sendPacket(packetPlayOutNamedEntitySpawn);
        headRotation(location.getYaw(), location.getPitch());
        return this;
    }

    public void destroy() {
        modifyTab(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        sendPacket(new PacketPlayOutEntityDestroy(pid));
    }

    public void update() {
        destroy();
        spawn();
        Bukkit.getServer().getScheduler().runTaskLater(Plugin.getInstance(), () -> modifyTab(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER), 2L);

    }

    public void sendPacket(Packet<?>... packet) {
        for(Player pp : viewers) {
            for(Packet<?> p : packet)
                ((CraftPlayer) pp).getHandle().playerConnection.sendPacket(p);
        }
    }

    public int getFixLocation(double pos) {
        return (int) MathHelper.floor(pos * 32.0D);
    }

    public byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    public boolean hasEvent() {
        return event != null;
    }

    private void set(Packet<?> packet, String field, Object value) {
        try {
            Field f = packet.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(packet, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object get(Packet<?> packet, String field) {
        try {
            Field f = packet.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(packet);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
