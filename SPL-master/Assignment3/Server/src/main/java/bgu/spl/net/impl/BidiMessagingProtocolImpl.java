package bgu.spl.net.impl;


import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.Message;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private Connections<Message> connections = new ConnectionsImpl<>();
    private int connectionID;
    private boolean shouldTerminate = false;
    private DataHolder data = DataHolder.getInstance();

    //public bgu.spl.net.impl.BidiMessagingProtocolImpl(){};

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connections = connections;
        connectionID = connectionId;
    }

    @Override
    public void process(Message message) {
        message.process(connections,connectionID,data);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

}
