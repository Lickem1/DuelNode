package tk.duelnode.api.server;

import java.util.List;

public class DNServerData {

    public int playerCount, maxPlayers, serverPort;
    public String serverName, externalIP, serverLocation;
    public boolean online, whitelisted;
    public List<String> onlinePlayers;

}
