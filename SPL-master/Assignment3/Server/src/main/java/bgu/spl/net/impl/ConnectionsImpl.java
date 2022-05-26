package bgu.spl.net.impl;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer , ConnectionHandler<T>> connectionsHandlers = new ConcurrentHashMap<>();
    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionsHandlers.containsKey(connectionId))
        {
            if (connectionsHandlers.get(connectionId)!=null) {
                connectionsHandlers.get(connectionId).send(msg);
                return true;
            }
        }
        return false;

    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler<T> client : connectionsHandlers.values()) {
            client.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if(connectionsHandlers.contains(connectionId))
            connectionsHandlers.remove(connectionId);
    }


    public void addConnection(int connectionId, ConnectionHandler<T> handler){
        connectionsHandlers.putIfAbsent(connectionId, handler);
    }

}
