package com.xenodev.xeno.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TeleportRequest {
    private UUID requester;
    private UUID target;
    private long timestamp;
    
    public boolean isExpired(long timeout) {
        return System.currentTimeMillis() - timestamp > timeout * 1000;
    }
}