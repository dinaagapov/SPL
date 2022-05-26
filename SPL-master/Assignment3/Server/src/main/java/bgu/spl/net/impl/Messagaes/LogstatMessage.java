package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.LogStatACKMessage;
import bgu.spl.net.impl.User;

import java.util.concurrent.ConcurrentHashMap;

public class LogstatMessage extends Message{
    short opcode = 7;

    public LogstatMessage(){};
    public void process(Connections connections, int connectionID, DataHolder dataHolder) {

        ConcurrentHashMap<String, User> users = dataHolder.getUsers();
        if (dataHolder.getUsers().containsKey(dataHolder.getUserName(connectionID))) {
            for (String username : users.keySet()) {
                if (dataHolder.LogstatMessage(connectionID,username)) {
                    LogStatACKMessage message = new LogStatACKMessage(dataHolder.userAge(username), dataHolder.numberOfPosts(username), dataHolder.numberOfFollowers(username), dataHolder.numberOfFollowing(username));
                    connections.send(connectionID, message);
                } else {
                    if (users.get(username).isLoggedIn())
                    {
                        ErrorMessage errorMessage = new ErrorMessage(opcode);
                        connections.send(connectionID, errorMessage);
                    }

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
