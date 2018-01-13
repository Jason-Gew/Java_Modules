package gew.server.websocket.service;

import gew.server.websocket.entity.ClientInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketStorage
{

    private static WebSocketStorage instance;
    private static AtomicInteger connectingClients;
    private static ConcurrentMap<String, ClientInfo> clients;

    public static WebSocketStorage getInstance()
    {
        if(instance == null)
            synchronized (WebSocketStorage.class)
            {
                if(instance == null)
                    instance = new WebSocketStorage();
            }
        return instance;
    }

    private WebSocketStorage()
    {
        connectingClients = new AtomicInteger(0);
        clients = new ConcurrentHashMap<>(500);
    }

    public int getConnectingClientsNum()
    {
        return connectingClients.get();
    }

    int increaseClientAndGet()
    {
        return connectingClients.incrementAndGet();
    }

    int decreaseClientAndGet()
    {
        return connectingClients.decrementAndGet();
    }

    public boolean addClient(final String sessionId, final ClientInfo client)
    {
        if(client != null && client.getUsername()!= null && !client.getUsername().isEmpty())
        {
            clients.put(sessionId, client);
            return true;
        }
        else
        {
            clients.put(sessionId, client);
            return false;
        }
    }

    void deleteClient(final String sessionId)
    {
        clients.remove(sessionId);
    }


    public List<ClientInfo> getClients()
    {
        return new ArrayList<>(clients.values());
    }

}
