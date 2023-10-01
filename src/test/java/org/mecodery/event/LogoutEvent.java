package org.mecodery.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogoutEvent implements IEvent {

    private String username;

}
