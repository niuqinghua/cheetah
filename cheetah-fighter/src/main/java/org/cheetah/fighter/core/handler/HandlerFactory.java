package org.cheetah.fighter.core.handler;

/**
 * Created by Max on 2016/2/21.
 */
public interface HandlerFactory {
    Handler createDomainEventHandler();
}