package tk.duelnode.lobby.data.packet;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class PacketState {

    @Getter @Setter
    private boolean cancelled = false;
    private final ChannelHandlerContext ctx;
}
