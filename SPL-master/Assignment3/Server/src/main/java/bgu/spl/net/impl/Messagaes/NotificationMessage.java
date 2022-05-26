package bgu.spl.net.impl.Messagaes;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;

public class NotificationMessage extends Message{
    Byte notificationType;
    private String postingUser;
    private String content;

    public NotificationMessage(Byte nt , String postingUser, String content) {
        this.postingUser = postingUser;
        this.content = content;
        this.notificationType = nt;
    }
    public void process(Connections connections, int connectionID, DataHolder dataHolder){}
    public String messageToString(){
        return "09" + notificationType + " " +postingUser+ " "+content +";";
    }

}
