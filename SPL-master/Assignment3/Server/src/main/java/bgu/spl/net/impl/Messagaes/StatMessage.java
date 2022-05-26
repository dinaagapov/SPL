package bgu.spl.net.impl.Messagaes;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.StatACKMessage;

import java.util.LinkedList;

public class StatMessage extends Message{
    short opcode = 8;
    private LinkedList<String> usersname;

    public StatMessage(String [] usernames)
    {
        for(String username : usernames)
            usersname.add(username);
    }

    public void process(Connections connections, int connectionID, DataHolder dataHolder) {

        if (dataHolder.getUsers().containsKey(dataHolder.getUserName(connectionID))) {
            for (String username : usersname) {
                if (dataHolder.statMessage(connectionID, username)) {
                    StatACKMessage ackMessage = new StatACKMessage(dataHolder.userAge(username), dataHolder.numberOfPosts(username), dataHolder.numberOfFollowers(username)
                            , dataHolder.numberOfFollowers(username));
                    connections.send(dataHolder.getUserId(username), ackMessage);
                } else {
                    ErrorMessage errorMessage = new ErrorMessage(opcode);
                    connections.send(dataHolder.getUserId(username), errorMessage);

                }
            }
        }
        else
        {
            ErrorMessage errorMessage = new ErrorMessage(opcode);
            connections.send(connectionID, errorMessage);
        }
    }
    @Override
    public String messageToString() {
        return null;
    }
}
