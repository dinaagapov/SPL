package bgu.spl.net.impl.Messagaes;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.DataHolder;

import java.util.LinkedList;
import java.util.List;

public class PostMessage extends Message {
    private String content;
    private String[]  usersNames;
    private short opcode = 5;

    public PostMessage(String s) {
        content = s;
        this.usersNames = getStrudelUsers();
    }

    public void process(Connections connections, int connectionID, DataHolder dataHolder) {
        LinkedList<String> RecivePost = dataHolder.PostMessage(connectionID ,content ,usersNames );
        if (RecivePost != null) {
            for (String user : RecivePost)
            {
                if (dataHolder.getUsers().containsKey(user)) {
                    NotificationMessage message = new NotificationMessage((byte) 01, dataHolder.getUserName(connectionID), content);
                    connections.send(dataHolder.getUserId(user), message);
                }
            }

        } else {
            ErrorMessage errorMessage = new ErrorMessage(opcode);
            connections.send(connectionID, errorMessage);
        }
    }
    public String[] getStrudelUsers()
    {
        boolean check=false;
        String newUser= new String();
        List<String> UsersStrudel = new LinkedList<String>();
        for (int i=5;i<content.length();i++)
        {
            if (check)
            {
                newUser = newUser + content.charAt(i);
            }
            if (content.charAt(i) == '@')
            {
                check = true;
            }
            if (content.charAt(i) == ' ')
            {
                check = false;
                if (!newUser.equals("")) {
                    newUser = newUser.substring(0, newUser.length() - 1);
                    UsersStrudel.add(newUser);
                }
                newUser = "";
            }
        }
        if (!newUser.equals("")) {
            UsersStrudel.add(newUser);
        }
        String[] returnedStringArray = new String[UsersStrudel.size()];
        for (int j=0;j< returnedStringArray.length;j++)
        {
            returnedStringArray[j] = UsersStrudel.get(j);
        }
        return  returnedStringArray;

    }

    @Override
    public String messageToString() {
        return null;
    }
}