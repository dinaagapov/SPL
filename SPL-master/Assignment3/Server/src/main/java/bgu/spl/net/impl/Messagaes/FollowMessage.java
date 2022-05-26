package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.FollowACKMessage;

public class FollowMessage extends Message{
    byte followUnfollow;
    String username;
    short opcode = 4;

    public FollowMessage(String followUnfollow , String user){
        this.username = user;
        this.followUnfollow = followUnfollow.getBytes()[0];
    }
    public void process(Connections connections, int connectionID, DataHolder dataHolder) {
        if(dataHolder.FollowOrUnFollow(connectionID , username, followUnfollow)) {
            FollowACKMessage ackMessage = new FollowACKMessage(username,followUnfollow);
            connections.send(connectionID , ackMessage);
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
