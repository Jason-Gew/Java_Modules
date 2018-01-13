package gew.server.websocket.controller;


import gew.server.websocket.service.WebSocketStorage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/server")
public class ServerStatusController
{

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z/z");

    private static String currentDateTime()
    {
        String formattedTime;
        ZonedDateTime now = ZonedDateTime.now();
        formattedTime = FORMATTER.format(now);
        return formattedTime;
    }

    @GetMapping(value = "status", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getClientConnections()
    {
        Map<String, Object> results = new HashMap<>();

        WebSocketStorage socketStorage = WebSocketStorage.getInstance();
        results.put("Total", socketStorage.getConnectingClientsNum());
        results.put("Clients", socketStorage.getClients());
        results.put("Timestamp", currentDateTime());

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
