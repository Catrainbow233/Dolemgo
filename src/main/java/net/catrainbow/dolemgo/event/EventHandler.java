package net.catrainbow.dolemgo.event;

import net.catrainbow.dolemgo.Server;
import net.catrainbow.dolemgo.utils.exceptions.EventException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EventHandler {

    private final EventManager eventManager;
    private final Class<? extends Event> eventClass;

    private final Map<EventPriority, ArrayList<Consumer<Event>>> priority2handlers = new EnumMap<>(EventPriority.class);

    public EventHandler(Class<? extends Event> eventClass, EventManager eventManager) {
        this.eventClass = eventClass;
        this.eventManager = eventManager;
    }

    public <T extends Event> CompletableFuture<T> handle(T event) {
        if (!this.eventClass.isInstance(event)) {
            throw new EventException("Tried to handle invalid event type!");
        }

        if (!event.isAsync()) {
            return this.handleSync(event);
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        CompletableFuture.supplyAsync(() -> {
            for (EventPriority priority : EventPriority.values()) {
                this.handlePriority(priority, event);
            }
            return event;
        }, this.eventManager.getThreadedExecutor()).thenAccept(futureEvent -> futureEvent.completeFuture(future)).whenComplete((ignore, error) -> {
            if (error != null && !future.isDone()) {
                future.completeExceptionally(error);
                Server.getInstance().getLogger().error("Exception was thrown in event handler", error);
            }
        });
        return future;
    }

    private <T extends Event> CompletableFuture<T> handleSync(T event) {
        if (!event.isCompletable()) {
            for (EventPriority priority : EventPriority.values()) {
                this.handlePriority(priority, event);
            }
            // Non-completable events does not provide future.
            return null;
        }

        try {
            for (EventPriority priority : EventPriority.values()) {
                this.handlePriority(priority, event);
            }
        } catch (Exception e) {
            Server.getInstance().getLogger().error(e);
        }

        if (event.getCompletableFutures().isEmpty()) {
            return CompletableFuture.completedFuture(event);
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        event.completeFuture(future);
        return future;
    }

    private void handlePriority(EventPriority priority, Event event) {
        ArrayList<Consumer<Event>> handlerList = this.priority2handlers.get(priority);
        if (handlerList != null) {
            for (Consumer<Event> eventHandler : handlerList) {
                eventHandler.accept(event);
            }
        }
    }

    public void subscribe(Consumer<Event> handler, EventPriority priority) {
        List<Consumer<Event>> handlerList = this.priority2handlers.computeIfAbsent(priority, priority1 -> new ArrayList<>());
        // Check if event is already registered
        if (!handlerList.contains(handler)) {
            // Handler is not registered yet
            handlerList.add(handler);
        }
    }
}
