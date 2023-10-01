package org.mecodery.event.listener;

import org.mecodery.event.LoginEvent;

import java.util.logging.Logger;

public class LoginEventListener implements IEventListener<LoginEvent> {
    private static final Logger LOGGER = Logger.getLogger( LoginEventListener.class.getName() );

    @Override
    public void run( final LoginEvent event ) {
        LOGGER.info( String.format( "The user \"%s\" has been logged !", event.getUsername() ) );
    }
}
