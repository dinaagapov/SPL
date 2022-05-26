package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.LogoutACKMessage;

public class LogoutMessage extends Message{
    short opcode = 3;

    public LogoutMessage(){}
    public void process(Connections connections, int connectionID, DataHolder dataHolder) {
        if(dataHolder.Logout(connectionID)){
           LogoutACKMessage ackMessage = new LogoutACKMessage();
           connections.send(connectionID,ackMessage);
           connections.disconnect(connectionID);
        }
        else {
            ErrorMessage errorMessage = new ErrorMessage(opcode);
            connections.send(connectionID , errorMessage);
        }
    }

    @Override
    public String messageToString() {
        return null;
    }
}
