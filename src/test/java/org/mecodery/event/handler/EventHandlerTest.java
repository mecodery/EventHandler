package org.mecodery.event.handler;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mecodery.event.LogoutEvent;
import org.mecodery.event.SayEvent;
import org.mecodery.event.listener.SayEventListener;
import org.mockito.Mockito;
import org.mecodery.event.LoginEvent;
import org.mecodery.event.listener.IEventListener;
import org.mecodery.event.listener.LoginEventListener;
import org.mecodery.event.listener.LogoutEventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@Tag( "event-handler" )
@DisplayName( "Event Handler Test" )
public class EventHandlerTest {

    @AfterEach
    void tearDown() {
        EventHandler.clear();
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| TESTS |------------------------------------------------------------------------------------------------ \\
// ------------------------------------------------------------------------------------------------------------------ \\

    @Test
    @DisplayName( "Instancia EventHandler: lanza \"IllegalStateException\"" )
    void givenPrivateConstructor_whenInstantiated_thenThrowIllegalStateException() {
        //WHEN
        final Executable executable = () -> {
            final Constructor<EventHandler> constructor = EventHandler.class.getDeclaredConstructor();
            constructor.setAccessible( true );
            try { constructor.newInstance(); }
            catch ( final InvocationTargetException e ) { throw e.getTargetException(); }
        };

        //THEN
        assertThrows( IllegalStateException.class, executable );
    }


    @Test
    @DisplayName( "Registro EventListener: Agregación con éxito" )
    void givenEventListener_whenAddedToEventHandler_thenCountsAreCorrect() {
        //GIVEN
        final IEventListener<LoginEvent> givenEventListener = new LoginEventListener();

        //WHEN
        EventHandler.addEventListener( givenEventListener );

        //THEN
        assertEquals( 1, EventHandler.getListenerCount( LoginEvent.class ) );
        assertEquals( 1, EventHandler.getListenerCount() );
    }


    @Test
    @DisplayName( "Registro EventListener sin Evento parametrizado: No se agrega y lanza \"IllegalArgumentException\"" )
    void givenInvalidEventListenerWithoutParameterizedEvent_whenAddedToEventHandler_thenIllegalArgumentExceptionAndEventListenerNotRegistered() {
        //GIVEN
        final IEventListener givenEventListener = Mockito.mock( IEventListener.class );

        //WHEN
        final Executable executable = () -> EventHandler.addEventListener( givenEventListener );

        //THEN
        assertThrows( IllegalArgumentException.class, executable );
        assertEquals( 0, EventHandler.getListenerCount() );

    }


    @Test
    @DisplayName( "Registro de varios EventListener y lanzamos un sólo evento: Se ejecuta el correspondiente EventListener" )
    void givenMultipleEventListenersAdded_whenOneEventIsTriggered_thenOneListenerIsCalled() {
        //GIVEN
        final LoginEventListener givenLoginEventListener = Mockito.mock( LoginEventListener.class );
        final SayEventListener givenSayEventListener = Mockito.mock( SayEventListener.class );
        final LogoutEventListener givenLogoutEventListener = Mockito.mock( LogoutEventListener.class );

        //WHEN
        EventHandler.addEventListener( givenLoginEventListener, givenSayEventListener, givenLogoutEventListener );
        EventHandler.trigger( new SayEvent( "test", "Hi World !" ) );

        //THEN
        Mockito.verify( givenLoginEventListener, Mockito.times( 0 ) ).run( any( LoginEvent.class ) );
        Mockito.verify( givenSayEventListener, Mockito.times( 1 ) ).run( any( SayEvent.class ) );
        Mockito.verify( givenLogoutEventListener, Mockito.times( 0 ) ).run( any( LogoutEvent.class ) );
    }


    @Test
    @DisplayName( "Registro de varios EventListener y lanzamos sus eventos: Se ejecutan los EventListeners" )
    void givenMultipleEventListenersAdded_whenEventsAreTriggered_thenListenersAreCalled() {
        //GIVEN
        final LoginEventListener givenLoginEventListener = Mockito.mock( LoginEventListener.class );
        final SayEventListener givenSayEventListener = Mockito.mock( SayEventListener.class );
        final LogoutEventListener givenLogoutEventListener = Mockito.mock( LogoutEventListener.class );

        //WHEN
        EventHandler.addEventListener( givenLoginEventListener, givenSayEventListener, givenLogoutEventListener );
        EventHandler.trigger( new LoginEvent( "test" ), new SayEvent( "test", "Hi World !" ), new LogoutEvent( "test" ) );

        //THEN
        Mockito.verify( givenLoginEventListener, Mockito.times( 1 ) ).run( any( LoginEvent.class ) );
        Mockito.verify( givenSayEventListener, Mockito.times( 1 ) ).run( any( SayEvent.class ) );
        Mockito.verify( givenLogoutEventListener, Mockito.times( 1 ) ).run( any( LogoutEvent.class ) );
    }


    @Test
    @DisplayName( "Lanzamos un evento que no esta registrado: No ocurre nada" )
    void givenThereAreNotEventListener_whenEventThatIsNotRegisteredInEventHandlerIsTrigger_thenHandlerCallNothing() {
        //WHEN
        final Executable executable = () -> EventHandler.trigger( new LogoutEvent( "test" ) );

        //THEN
        assertDoesNotThrow( executable );
    }


    @Test
    @DisplayName( "Registro de varios EventListener y los eliminamos varios de ellos: Solo estan los que no eliminamos" )
    void givenMultipleEventListeners_whenRemovingOneListener_thenListenerCountsAreCorrect() {
        //GIVEN
        final LoginEventListener givenLoginEventListener = Mockito.mock( LoginEventListener.class );
        final SayEventListener givenSayEventListener = Mockito.mock( SayEventListener.class );
        final LogoutEventListener givenLogoutEventListener = Mockito.mock( LogoutEventListener.class );

        //WHEN
        EventHandler.addEventListener( givenLoginEventListener, givenSayEventListener, givenLogoutEventListener );
        assertEquals( 3, EventHandler.getListenerCount() );
        EventHandler.removeEventListener( givenLoginEventListener, givenLogoutEventListener );

        //THEN
        assertEquals( 0, EventHandler.getListenerCount( LoginEvent.class, LogoutEvent.class ) );
        assertEquals( 1, EventHandler.getListenerCount( SayEvent.class ) );
        assertEquals( 1, EventHandler.getListenerCount() );
    }


    @Test
    @DisplayName( "Registro de varios EventListener y limpiamos todos los registros de todos los eventos: No hay EventListeners" )
    void givenEventListenersAdded_whenClearingAllListeners_thenListenerCountIsZero(){
        //GIVEN
        final LoginEventListener givenLoginEventListener = Mockito.mock( LoginEventListener.class );
        final SayEventListener givenSayEventListener = Mockito.mock( SayEventListener.class );
        final LogoutEventListener givenLogoutEventListener = Mockito.mock( LogoutEventListener.class );

        //WHEN
        EventHandler.addEventListener( givenLoginEventListener, givenSayEventListener, givenLogoutEventListener );
        assertEquals( 3, EventHandler.getListenerCount() );
        EventHandler.clear();

        //THEN
        assertEquals( 0, EventHandler.getListenerCount() );
    }


    @Test
    @DisplayName( "Registro de varios EventListener y limpiamos todos los registros de varios eventos: Solo hay EventListeners registrados en los eventos no limpiados" )
    void givenEventListenersAdded_whenClearingSpecificEventListeners_thenCountsAreCorrect(){
        //GIVEN
        final LoginEventListener givenLoginEventListener = Mockito.mock( LoginEventListener.class );
        final SayEventListener givenSayEventListener = Mockito.mock( SayEventListener.class );
        final LogoutEventListener givenLogoutEventListener = Mockito.mock( LogoutEventListener.class );

        //WHEN
        EventHandler.addEventListener( givenLoginEventListener, givenSayEventListener, givenLogoutEventListener );
        assertEquals( 3, EventHandler.getListenerCount() );
        EventHandler.clear( LoginEvent.class, LogoutEvent.class );

        //THEN
        assertEquals( 0, EventHandler.getListenerCount( LoginEvent.class, LogoutEvent.class ) );
        assertEquals( 1, EventHandler.getListenerCount( SayEvent.class ) );
        assertEquals( 1, EventHandler.getListenerCount() );
    }

// ------------------------------------------------------------------------------------------------------------------ \\

}
