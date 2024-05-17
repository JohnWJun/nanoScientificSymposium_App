package com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogMessage {
    long userId;
     long listId;
     String status;
     String error;
}
