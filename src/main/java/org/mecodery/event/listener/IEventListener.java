package org.mecodery.event.listener;

import org.mecodery.event.IEvent;

public interface IEventListener<T extends IEvent> {
    void run( T event );
}
