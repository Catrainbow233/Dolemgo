package net.catrainbow.dolemgo.event;
public interface CancellableEvent {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

    void setCancelled();
}
