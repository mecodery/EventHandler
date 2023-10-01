package org.mecodery.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SayEvent implements IEvent {

    private String username;

    private String message;
}
