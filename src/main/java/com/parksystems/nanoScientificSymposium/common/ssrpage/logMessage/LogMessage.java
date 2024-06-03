package com.parksystems.nanoScientificSymposium.common.ssrpage.logMessage;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogMessage {
     String region;
     long userId;
     long listId;
     String status;
     String error;
}
