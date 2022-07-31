package tk.duelnode.api.util.packet;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
public class PacketEventReference {

    private final Method method;
    private final Object methodOwner;
    private final Class<?> packet;

    public PacketEventReference(Method method, Object methodOwner, Class<?> packet) {
        this.method = method;
        this.methodOwner = methodOwner;
        this.packet = packet;
        method.setAccessible(true);

        if(method.getParameterCount()!=3)
            throw new RuntimeException("Invalid packet listener (" + method.getName() + ") Expected 3 parameters");
        Class<?>[] types = method.getParameterTypes();
        if(types[0] != Player.class)
            throw new RuntimeException("Invalid packet listener (" + method.getName() + ") first parameter must be an instance of Player.class");
        if(types[1] != PacketState.class)
            throw new RuntimeException("Invalid packet listener (" + method.getName() + ") second parameter must be an instance of PacketState.class");
        if(!types[2].isAssignableFrom(packet))
            throw new RuntimeException("Invalid packet listener (" + method.getName() + ") second parameter must be an instance of " + packet);
        if (method.getReturnType() != Void.TYPE)
            throw new RuntimeException("Invalid handler ("+method.getName()+") return type expected to be void");

    }

    public void call(Player player, PacketState state, Object packet) {
        try {
            method.invoke(methodOwner, player, state, packet);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
