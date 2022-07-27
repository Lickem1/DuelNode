package tk.duelnode.lobby.manager;


import org.atteo.classindex.ClassIndex;
import tk.duelnode.lobby.data.packet.ClassType;
import tk.duelnode.lobby.data.packet.PacketEvent;
import tk.duelnode.lobby.data.packet.PacketEventReference;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.manager.dynamic.annotations.PostInit;
import tk.duelnode.lobby.manager.dynamic.annotations.PreInit;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DynamicManager {

    private static final Map<Class<?>, Object> reflectionClasses = new HashMap<>();
    private static final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<PacketEventReference>> packetMap = new ConcurrentHashMap<>();

    public static void init(ClassLoader loader) {
        ClassIndex.getAnnotated(PreInit.class, loader).forEach(DynamicManager::selfConstruct);

        ClassIndex.getAnnotated(Init.class, loader).forEach(clazz -> {
            Object classOb = selfConstruct(clazz);
            Class<?> newClass = classOb.getClass();
            Init init = newClass.getAnnotation(Init.class);

            if(init != null) {
                for(ClassType type : init.classType()) {

                    if(type == ClassType.PACKET_LISTENER) {
                        Method[] methods = newClass.getMethods();

                        for(Method m : methods) {
                            PacketEvent annotation = m.getAnnotation(PacketEvent.class);
                            if(annotation != null) {
                                Class<?> reference = annotation.packet();
                                PacketEventReference packetEventReference = new PacketEventReference(m, classOb, reference);
                                add(reference, packetEventReference);
                            }
                        }
                    }
                }
            }
        });
        ClassIndex.getAnnotated(PostInit.class, loader).forEach(DynamicManager::selfConstruct);
    }

    private static <T> T selfConstruct(Class clazz) {
        try {
            Constructor c = clazz.getDeclaredConstructor();
            c.setAccessible(true);

            T test = (T) c.newInstance();
            reflectionClasses.put(clazz, test);

            return test;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void add(Class<?> clazz, PacketEventReference reference) {
        List<PacketEventReference> list = list(clazz);
        list.add(reference);
    }

    private static CopyOnWriteArrayList<PacketEventReference> list(Class<?> clazz) {
        return packetMap.computeIfAbsent(clazz,(c)->new CopyOnWriteArrayList<>());
    }

    public static List<PacketEventReference> getPacketReference(Class<?> clazz) {
        return packetMap.get(clazz);
    }

    public static <T> T get(Class<T> clazz) {
        return (T) reflectionClasses.get(clazz);
    }
}
