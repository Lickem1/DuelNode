package tk.duelnode.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import tk.duelnode.api.util.packet.PacketInjector;

public class API {

    @Getter
    private static final Gson gson = new GsonBuilder().create();

    @Getter
    private static final PacketInjector packetInjector = new PacketInjector();
}
