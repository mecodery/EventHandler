package org.mecodery.event.listener;

import org.mecodery.event.SayEvent;

import java.util.logging.Logger;

public class SayEventListener implements IEventListener<SayEvent> {
    private static final Logger LOGGER = Logger.getLogger( SayEventListener.class.getName() );

    @Override
    public void run( final SayEvent event ) {
        LOGGER.info( String.format( "The username \"%s\" say: %s", event.getUsername(), event.getMessage() ) );
    }
}
