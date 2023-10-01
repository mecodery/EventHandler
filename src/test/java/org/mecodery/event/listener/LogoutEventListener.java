package org.mecodery.event.listener;

import org.mecodery.event.LogoutEvent;

import java.util.logging.Logger;

public class LogoutEventListener implements IEventListener<LogoutEvent> {
    private static final Logger LOGGER = Logger.getLogger( LogoutEventListener.class.getName() );

    @Override
    public void run( final LogoutEvent event ) {
        LOGGER.info( String.format( "The user \"%s\" has been logout !", event.getUsername() ) );
    }
}
