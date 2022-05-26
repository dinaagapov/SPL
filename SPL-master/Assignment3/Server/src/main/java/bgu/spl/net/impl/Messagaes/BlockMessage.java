package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.BlockACKMessage;

public class BlockMessage extends Message{
    String username;

    public BlockMessage(String username){
        this.username = username;
    }

    @Override
    public void process(Connections connections, int conID, DataHolder dataHolder) {
        if (!dataHolder.blockUser(conID,username))
        {
            short mo = 12;
            ErrorMessage message = new ErrorMessage(mo);
            connections.send(conID , message);
        }
        else
        {
            BlockACKMessage message = new BlockACKMessage();
            connections.send(conID , message);
        }

    }

    @Override
    public String messageToString() {
        return null;
    }
}
