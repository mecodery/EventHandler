package org.mecodery.event.handler;

import org.mecodery.event.IEvent;
import org.mecodery.event.listener.IEventListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class EventHandler {

    private static final Map<Class<? extends IEvent>, Set<IEventListener>> registry = new HashMap<>();

// ------------------------------------------------------------------------------------------------------------------ \\

    private EventHandler(){
        throw new IllegalStateException( "Utility class" );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| ADD |-------------------------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    public static <T extends IEventListener> void addEventListener( final T listener ){
        final Class<? extends IEvent> event = EventHandler.getEventClassFromListener( listener );
        final Set<IEventListener> listeners = EventHandler.registry.computeIfAbsent( event, k -> new HashSet<>());
        listeners.add(listener);
    }

    public static <T extends IEventListener> void addEventListener( final T... listeners ){
        EventHandler.addEventListener( Arrays.asList( listeners ) );
    }

    public static <T extends IEventListener> void addEventListener( final List<T> listeners ){
        Set.copyOf( listeners ).stream().forEach( EventHandler::addEventListener );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| REMOVE |----------------------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    public static <T extends IEventListener> void removeEventListener( final T listener ){
        final Class<? extends IEvent> event = EventHandler.getEventClassFromListener( listener );
        final Set<IEventListener> listeners = EventHandler.registry.computeIfAbsent( event, k -> new HashSet<>());
        listeners.remove( listener );
    }

    public static <T extends IEventListener> void removeEventListener( final T... listeners ){
        EventHandler.removeEventListener( Arrays.asList( listeners ) );
    }

    public static <T extends IEventListener> void removeEventListener( final List<T> listeners ){
        Set.copyOf( listeners ).stream().forEach( EventHandler::removeEventListener );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| CLEAR |------------------------------------------------------------------------------------------------ \\
// ------------------------------------------------------------------------------------------------------------------ \\

    public static void clear(){
        EventHandler.registry.clear();
    }

    public static void clear( final Class<? extends IEvent> event ){
        EventHandler.registry.remove( event );
    }

    public static void clear( final Class<? extends IEvent>... event ){
       EventHandler.clear( Arrays.asList( event ) );
    }

    public static void clear( final List<Class<? extends IEvent>> events ){
       Set.copyOf( events ).stream().forEach( EventHandler::clear );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| TRIGGER |---------------------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    public static void trigger( final IEvent event ){
        if( EventHandler.registry.containsKey( event.getClass() ) ){
            final Set<? extends IEventListener> listeners = EventHandler.registry.get( event.getClass() );
            listeners.stream().forEach( listener -> listener.run( event ) );
        }
    }

    public static void trigger( final IEvent... events ){
        EventHandler.trigger( Arrays.asList( events ) );
    }

    public static void trigger( final List<IEvent> events ){
        Set.copyOf( events ).stream().forEach( EventHandler::trigger );
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| COUNT |------------------------------------------------------------------------------------------------ \\
// ------------------------------------------------------------------------------------------------------------------ \\

    public static int getListenerCount(){
        return EventHandler.registry.values().stream().mapToInt( Set::size ).sum();
    }

    public static int getListenerCount( final Class<? extends IEvent> eventClass ){
        return EventHandler.registry.getOrDefault( eventClass, Collections.emptySet() ).size();
    }

    public static int getListenerCount( final Class<? extends IEvent>... eventClass ){
        return EventHandler.getListenerCount( Arrays.asList( eventClass ) );
    }

    public static int getListenerCount( final List<Class<? extends IEvent>> eventClasses ){
        return eventClasses.stream().mapToInt( EventHandler::getListenerCount ).sum();
    }

// ------------------------------------------------------------------------------------------------------------------ \\
// ---------| UTIL |------------------------------------------------------------------------------------------------- \\
// ------------------------------------------------------------------------------------------------------------------ \\

    public static <T extends IEventListener> Class<? extends IEvent> getEventClassFromListener( final T listener ){
        final Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
        Optional <Class<? extends IEvent>> result = Optional.empty();

        for( int i = 0 ; i < genericInterfaces.length && result.isEmpty() ; i++ ){
            final Type genericInterface = genericInterfaces[ i ];

            if( genericInterface instanceof ParameterizedType parameterizedType && parameterizedType.getRawType() == IEventListener.class ) {
                final Type[] listenerTypeArguments = parameterizedType.getActualTypeArguments();

                if ( listenerTypeArguments.length == 1 ) {
                    final Type eventType = listenerTypeArguments[ 0 ];
                    final Class<? extends IEvent> eventClass = (Class<? extends IEvent>) eventType;
                    result = Optional.of( eventClass );
                }
            }
        }

        return result.orElseThrow( () -> new IllegalArgumentException( "The listener must implement IEventListener<T extends IEvent>" ) );
    }

// ------------------------------------------------------------------------------------------------------------------ \\

}
