package net.catrainbow.dolemgo.utils.exceptions;

import net.catrainbow.dolemgo.Server;

public class EventException extends RuntimeException {

    public EventException(String exp) {
        Server.getInstance().getLogger().error(exp, this);
    }

}
