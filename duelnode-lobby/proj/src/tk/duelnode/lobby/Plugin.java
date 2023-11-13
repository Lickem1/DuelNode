package tk.duelnode.lobby;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import io.lettuce.core.RedisClient;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import tk.duelnode.api.API;
import tk.duelnode.api.server.DNServerData;
import tk.duelnode.api.server.DNServerManager;
import tk.duelnode.api.server.EnumServerType;
import tk.duelnode.api.util.menu.MenuListener;
import tk.duelnode.api.util.plasma.Plasma;
import tk.duelnode.api.util.redis.RedisManager;
import tk.duelnode.lobby.data.commands.ListCommand;
import tk.duelnode.lobby.data.menu.info.InfoMenu;
import tk.duelnode.lobby.data.npc.NPC;
import tk.duelnode.lobby.data.player.PlayerData;
import tk.duelnode.lobby.data.queue.QueueManager;
import tk.duelnode.lobby.data.world.chunk.NMSChunk;
import tk.duelnode.lobby.manager.DynamicManager;
import tk.duelnode.lobby.manager.PlayerDataManager;
import tk.duelnode.lobby.manager.ScoreboardAdapter;
import tk.duelnode.lobby.util.WorldEditUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class Plugin extends JavaPlugin implements PluginMessageListener {

    @Getter private static Plugin instance;
    private RedisManager redisManager;
    private DNServerManager dnServerManager;


    private Location spawnLocation;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        new Plasma(this, new ScoreboardAdapter());
        new MenuListener(this);
        DynamicManager.init(this.getClassLoader());
        getCommand("list").setExecutor(new ListCommand());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);


        for(World worlds : getServer().getWorlds()) new NMSChunk(worlds);

        try {
            World world = Bukkit.getServer().getWorld("world");
            File file = new File(Plugin.getInstance().getDataFolder(), "spawn.schematic");
            WorldEditUtil worldEditUtil = DynamicManager.get(WorldEditUtil.class);

            Clipboard clipboard = worldEditUtil.load(file);

            worldEditUtil.paste(clipboard, new Vector(0, 70, 0), world);


        } catch (Exception e) {
            e.printStackTrace();
        }

        //spawnLocation = new Location(Bukkit.getServer().getWorld("world"), 0.500, 71, 0.500, -90, 0);
        spawnLocation = new Location(Bukkit.getServer().getWorld("world"), 0.500, 71, 0.500, -180, -8);

        redisManager.subscribe("dn/server/gameserver-chat", ((channel, message) -> {
            Bukkit.broadcast(ComponentSerializer.parse(message));

        }));
    }

    public void onDisable() {
        dnServerManager.end();
        instance = null;
    }

    public void sendToGameServer(String gameServer, Player player) {
        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
        dataOutput.writeUTF("Connect");
        dataOutput.writeUTF(gameServer);
        player.sendPluginMessage(this, "BungeeCord", dataOutput.toByteArray());
    }


    public int getFixedPlayerCount() {
        int player = 0;
        for(DNServerData data : DNServerManager.getAllServerData(Plugin.getInstance().getRedisManager())) {
            if(data.online) player+=data.playerCount;
        }
        return player;
    }


    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

    }
}
