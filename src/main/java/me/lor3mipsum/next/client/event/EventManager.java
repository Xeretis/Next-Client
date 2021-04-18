package me.lor3mipsum.next.client.event;

import me.lor3mipsum.next.client.event.events.Stoppable;
import me.lor3mipsum.next.client.event.events.interfaces.IEvent;
import me.lor3mipsum.next.client.event.types.Priority;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventManager {

    private static final Map<Class<? extends IEvent>, List<MethodData>> REGISTRY_MAP = new HashMap<>();

    private EventManager() {
    }

    public static void register(Object object) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            if (isMethodBad(method)) {
                continue;
            }

            register(method, object);
        }
    }

    public static void register(Object object, Class<? extends IEvent> eventClass) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            if (isMethodBad(method, eventClass)) {
                continue;
            }

            register(method, object);
        }
    }

    public static void unregister(Object object) {
        for (final List<MethodData> dataList : REGISTRY_MAP.values()) {
            dataList.removeIf(data -> data.getSource().equals(object));
        }

        cleanMap(true);
    }

    public static void unregister(Object object, Class<? extends IEvent> eventClass) {
        if (REGISTRY_MAP.containsKey(eventClass)) {
            REGISTRY_MAP.get(eventClass).removeIf(data -> data.getSource().equals(object));

            cleanMap(true);
        }
    }

    private static void register(Method method, Object object) {
        Class<? extends IEvent> indexClass = (Class<? extends IEvent>) method.getParameterTypes()[0];
        //New MethodData from the Method we are registering.
        final MethodData data = new MethodData(object, method, method.getAnnotation(EventTarget.class).value());

        //Set's the method to accessible so that we can also invoke it if it's protected or private.
        if (!data.getTarget().isAccessible()) {
            data.getTarget().setAccessible(true);
        }

        if (REGISTRY_MAP.containsKey(indexClass)) {
            if (!REGISTRY_MAP.get(indexClass).contains(data)) {
                REGISTRY_MAP.get(indexClass).add(data);
                sortListValue(indexClass);
            }
        } else {
            REGISTRY_MAP.put(indexClass, new CopyOnWriteArrayList<MethodData>() {
                //Eclipse was bitching about a serialVersionUID.
                private static final long serialVersionUID = 666L;

                {
                    add(data);
                }
            });
        }
    }

    public static void removeEntry(Class<? extends IEvent> indexClass) {
        Iterator<Map.Entry<Class<? extends IEvent>, List<MethodData>>> mapIterator = REGISTRY_MAP.entrySet().iterator();

        while (mapIterator.hasNext()) {
            if (mapIterator.next().getKey().equals(indexClass)) {
                mapIterator.remove();
                break;
            }
        }
    }

    public static void cleanMap(boolean onlyEmptyEntries) {
        Iterator<Map.Entry<Class<? extends IEvent>, List<MethodData>>> mapIterator = REGISTRY_MAP.entrySet().iterator();

        while (mapIterator.hasNext()) {
            if (!onlyEmptyEntries || mapIterator.next().getValue().isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    private static void sortListValue(Class<? extends IEvent> indexClass) {
        List<MethodData> sortedList = new CopyOnWriteArrayList<>();

        for (final byte priority : Priority.VALUE_ARRAY) {
            for (final MethodData data : REGISTRY_MAP.get(indexClass)) {
                if (data.getPriority() == priority) {
                    sortedList.add(data);
                }
            }
        }

        //Overwriting the existing entry.
        REGISTRY_MAP.put(indexClass, sortedList);
    }


    private static boolean isMethodBad(Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventTarget.class);
    }

    private static boolean isMethodBad(Method method, Class<? extends IEvent> eventClass) {
        return isMethodBad(method) || !method.getParameterTypes()[0].equals(eventClass);
    }

    public static IEvent call(final IEvent event) {
        List<MethodData> dataList = REGISTRY_MAP.get(event.getClass());

        if (dataList != null) {
            if (event instanceof Stoppable) {
                Stoppable stoppable = (Stoppable) event;

                for (final MethodData data : dataList) {
                    invoke(data, event);

                    if (stoppable.isStopped()) {
                        break;
                    }
                }
            } else {
                for (final MethodData data : dataList) {
                    invoke(data, event);
                }
            }
        }

        return event;
    }

    static void invoke(MethodData data, IEvent argument) {
        try {
            data.getTarget().invoke(data.getSource(), argument);
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (InvocationTargetException e) {
        }
    }

    private static final class MethodData {

        private final Object source;

        private final Method target;

        private final byte priority;

        public MethodData(Object source, Method target, byte priority) {
            this.source = source;
            this.target = target;
            this.priority = priority;
        }

        public Object getSource() {
            return source;
        }

        public Method getTarget() {
            return target;
        }

        public byte getPriority() {
            return priority;
        }

    }
}
