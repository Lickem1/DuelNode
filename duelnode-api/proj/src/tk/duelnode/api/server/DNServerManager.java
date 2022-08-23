package tk.duelnode.api.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tk.duelnode.api.API;
import tk.duelnode.api.util.redis.RedisManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
public class DNServerManager {

    private static final String channel = "dn/servers/";


    private final RedisManager redis;
    private final DNServerData currentServer = new DNServerData();
    private final Gson gson = new GsonBuilder().create();

    public DNServerManager(Plugin plugin, RedisManager manager, String serverLocation) {

        currentServer.online = true;
        redis = manager;
        if(redis.getRedisConnection().isOpen()) {
            System.out.println("[INFO] DNServerManager initialized, storing data");
            final List<String> online = new ArrayList<>();

            currentServer.externalIP = getIP();
            currentServer.serverPort = Bukkit.getServer().getPort();
            currentServer.serverName = Bukkit.getServerName();
            currentServer.serverLocation = serverLocation;

            new BukkitRunnable() {
                @Override
                public void run() {
                    currentServer.whitelisted = Bukkit.getServer().hasWhitelist();
                    currentServer.maxPlayers = Bukkit.getServer().getMaxPlayers();
                    currentServer.playerCount = Bukkit.getServer().getOnlinePlayers().size();


                    online.clear();
                    for(Player s : Bukkit.getServer().getOnlinePlayers()) {
                        online.add(s.getName());
                    }
                    currentServer.onlinePlayers = online;


                    redis.getRedisConnection().async().set(channel + Bukkit.getServerName(), gson.toJson(currentServer));
                }
            }.runTaskTimerAsynchronously(plugin, 1, 20);


        } else {
            System.out.println("[INFO] DNServerManager could not initialized, please restart and try again");
        }
    }

    public void end() {
        currentServer.playerCount = 0;
        currentServer.online = false;
        currentServer.onlinePlayers = new ArrayList<>();
        redis.getRedisConnection().async().set(channel + Bukkit.getServerName(), gson.toJson(currentServer));
        System.out.println("[INFO] DNServerManager has been terminated");
    }

    @SneakyThrows
    private String getIP() {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        final BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }

    public static DNServerData getServerData(String serverID, RedisManager redis) {
        try {
            String string = redis.getRedisConnection().async().get(channel + serverID).get();

            return API.getGson().fromJson(string, DNServerData.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<DNServerData> getAllServerData(RedisManager redis) {
        List<DNServerData> globalServerList = new ArrayList<>();
        try {
            List<String> keys = redis.getRedisConnection().async().keys(channel + "*").get();

            for(String s : keys) {
                DNServerData value = API.getGson().fromJson(redis.getRedisConnection().async().get(s).get(), DNServerData.class);
                globalServerList.add(value);
            }

            return globalServerList;
        } catch (Exception e) {
            return globalServerList;
        }
    }
}