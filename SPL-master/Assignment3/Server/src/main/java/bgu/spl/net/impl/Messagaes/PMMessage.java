package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.ErrorPMMessage;

public class PMMessage extends Message{
    String username;
    String content;
    String SendingDataAndTime;
    short opcode = 6;

    public PMMessage(String u , String c , String dateAndTime)
    {
        this.username=u;
        this.content=c;
        this.SendingDataAndTime=dateAndTime;
    }


    public void process(Connections connections, int connectionID, DataHolder dataHolder){
        if(!dataHolder.getUsers().containsKey(username))
        {
            ErrorPMMessage message = new ErrorPMMessage(username);
            connections.send(connectionID, message);
        }
        else {
            if (dataHolder.PrivateMessage(connectionID, username, content)) {
                NotificationMessage message = new NotificationMessage((byte) 0, dataHolder.getUserName(connectionID), content);
                connections.send(dataHolder.getUserId(username), message);
            } else {
                ErrorMessage errorMessage = new ErrorMessage(opcode);
                connections.send(connectionID, errorMessage);
            }
        }
    }
    @Override
    public String messageToString() {
        return null;
    }

}
