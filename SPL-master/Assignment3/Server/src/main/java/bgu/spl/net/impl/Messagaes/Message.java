package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;

public abstract class Message {
    int zero=0;
    public void process(Connections connections, int conID, DataHolder dataHolder){};

    public abstract String messageToString();
}
