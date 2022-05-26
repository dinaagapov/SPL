package bgu.spl.net.impl.Messagaes.ACKMessage;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.Message;

public class FollowACKMessage extends Message {
    private final short opcode = 4;
    private String username;
    byte followUnfollow;

    public FollowACKMessage(String s , byte followUnfollow )
    {
        this.username = s;
        this.followUnfollow = followUnfollow;
    }
    public void process(Connections connections, int connectionID, DataHolder dataHolder){}
    public String messageToString(){
        return 10+"ACK 0"+ opcode + " " + (followUnfollow - 48)+ " " + username+";";
    }

}
