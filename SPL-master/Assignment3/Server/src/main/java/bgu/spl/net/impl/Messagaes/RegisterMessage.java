package bgu.spl.net.impl.Messagaes;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.RegisterACKMessage;

public class RegisterMessage extends Message {
    private String userName;
    private String password;
    private String birthday;
    short opcode = 1;

    public RegisterMessage(String user, String pass, String birth) {
        userName = user;
        password = pass;
        birthday = birth;
    }

    public void process(Connections connections, int connectionID, DataHolder dataHolder) {
        if(dataHolder.Register(connectionID , userName , password, birthday)) {
            RegisterACKMessage ackMessage = new RegisterACKMessage();
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
