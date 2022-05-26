package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;
import bgu.spl.net.impl.Messagaes.ACKMessage.LoginACKMessage;

public class LoginMessage extends Message{
    private String userName;
    private String password;
    byte Captcha;
    short opcode = 2;

    public LoginMessage(String username, String password , String c){
        userName = username;
        this.password = password;
        this.Captcha = c.getBytes()[0];
    }
    public void process(Connections connections, int connectionID, DataHolder dataHolder) {
        if(dataHolder.Login(connectionID , userName , password, Captcha)) {
            LoginACKMessage ackMessage = new LoginACKMessage();
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
