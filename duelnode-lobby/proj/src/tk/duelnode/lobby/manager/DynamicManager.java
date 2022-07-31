package tk.duelnode.lobby.manager;


import org.atteo.classindex.ClassIndex;
import tk.duelnode.api.API;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.api.util.packet.PacketEvent;
import tk.duelnode.api.util.packet.PacketEventReference;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.manager.dynamic.annotations.PostInit;
import tk.duelnode.lobby.manager.dynamic.annotations.PreInit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class DynamicManager {

    private static final Map<Class<?>, Object> reflectionClasses = new HashMap<>();

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
                                API.getPacketInjector().add(reference, packetEventReference);
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

    public static <T> T get(Class<T> clazz) {
        return (T) reflectionClasses.get(clazz);
    }
}
