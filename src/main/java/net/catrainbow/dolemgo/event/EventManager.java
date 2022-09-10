package net.catrainbow.dolemgo.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.catrainbow.dolemgo.Server;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.function.Consumer;
public class EventManager {

    private final Server proxy;
    private final ExecutorService threadedExecutor;
    private final HashMap<Class<? extends Event>, EventHandler> handlerMap = new HashMap<>();

    public EventManager(Server proxy) {
        this.proxy = proxy;
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat("WaterdogEvents Executor");
        int idleThreads = 0;
        this.threadedExecutor = new ThreadPoolExecutor(idleThreads, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>(), builder.build());
    }

    public <T extends Event> void subscribe(Class<T> event, Consumer<T> handler) {
        this.subscribe(event, handler, EventPriority.NORMAL);
    }

    /**
     * Can be used to subscribe to events. Once subscribed, the handler will be called each time the event is called.
     *
     * @param event    A class reference to the target event you want to subscribe to, for example ProxyPingEvent.class
     * @param handler  A method reference or lambda with one parameter, the event which you want to handle
     * @param priority The Priority of your event handler. Can be used to execute one handler after / before another
     * @param <T>      The class reference to the event you want to subscribe to
     * @see AsyncEvent
     * @see EventPriority
     */
    public <T extends Event> void subscribe(Class<T> event, Consumer<T> handler, EventPriority priority) {
        EventHandler eventHandler = this.handlerMap.computeIfAbsent(event, e -> new EventHandler(event, this));
        eventHandler.subscribe((Consumer<Event>) handler, priority);
    }

    /**
     * Used to call an provided event.
     * If the target event has the annotation AsyncEvent present, the CompletableFuture.whenComplete can be used to
     * execute code once the event has passed the whole event pipeline. If the annotation is not present, you can
     * ignore the return and use the direct variable reference of your event
     *
     * @param event the instance of an event to be called
     * @return CompletableFuture<Event> if event has AsyncEvent annotation present or null in case of non-async event
     */
    public <T extends Event> CompletableFuture<T> callEvent(T event) {
        EventHandler eventHandler = this.handlerMap.computeIfAbsent(event.getClass(), e -> new EventHandler(event.getClass(), this));
        return eventHandler.handle(event);
    }

    public ExecutorService getThreadedExecutor() {
        return this.threadedExecutor;
    }
}
