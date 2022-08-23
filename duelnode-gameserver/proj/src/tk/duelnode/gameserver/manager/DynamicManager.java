package tk.duelnode.gameserver.manager;

import org.atteo.classindex.ClassIndex;
import tk.duelnode.api.API;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.api.util.packet.PacketEvent;
import tk.duelnode.api.util.packet.PacketEventReference;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class DynamicManager {

    private static final Map<Class<?>, Object> reflectionClasses = new HashMap<>();

    public static void init(ClassLoader loader) {
        self(Init.class, loader);
    }

    private static void self(Class<? extends Annotation> c, ClassLoader loader) {
        Map<Class<?>, Integer> classMap = new HashMap<>();

        // get all classes using our annotation and store into classMap
        ClassIndex.getAnnotated(c, loader).forEach(clazz -> {
            Init init = clazz.getAnnotation(Init.class);

            if(init != null) { // just in case this happens to be null
                classMap.put(clazz, init.priority());
            }
        });

        // sort list by highest integer (priority)
        Object[] a = classMap.entrySet().toArray();
        Arrays.sort(a, (o1, o2) -> ((Map.Entry<Class<?>, Integer>) o2).getValue()
                .compareTo(((Map.Entry<Class<?>, Integer>) o1).getValue()));

        // loop through new sorted list and self construct
        for(Object e : a) {
            Class<?> clazz = ((Map.Entry<Class<?>, Integer>) e).getKey();

            Object classOb = selfConstruct(clazz);
            Class<?> newClass = classOb.getClass();
            Init init = clazz.getAnnotation(Init.class);

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
