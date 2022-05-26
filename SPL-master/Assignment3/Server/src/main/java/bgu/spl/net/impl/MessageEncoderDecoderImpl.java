package bgu.spl.net.impl;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Messagaes.*;

import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private int len = 0;
    private byte[] opcodeBytes = new byte[2];
    private Short opcode;
    private byte[] bytes = new byte[1 << 10];
    private int zeroCount = -1;
    private boolean isReset = false;
    private boolean sendMessage=false;


    @Override
    public Message decodeNextByte(byte nextByte) {
        if (!isReset()) {
            if (len < 2) {
                if (len>0){
                    opcodeBytes[len] = nextByte;
                }
                len++;
            }
            if (len == 2) {
                opcode = bytesToShort(opcodeBytes);
                switch (Short.valueOf(opcode)) {
                    case 1:
                    case 2: {
                        zeroCount = 3;
                        len++;
                        break;
                    }
                    case 3: {
                        len++;
                        Message temp = popMessage();
                        sendMessage = true;
                        return temp;

                    }
                    case 4: {
                        zeroCount = 2;
                        len++;
                        break;
                    }
                    case 6:
                    case 5:
                    {
                        zeroCount = -1;
                        len++;
                        break;

                        //;
                    }
                    case 12:
                    case 8: {
                        zeroCount = 1;
                        len++;
                        break;
                    }
                    case 7: {
                        Message temp = popMessage();
                        sendMessage = true;
                        return temp;
                    }
                }
            } else {
                if (len > 2) {
                    pushByte(nextByte);
                }
            }
            if (zeroCount == 0 && Short.valueOf(opcode) != 5 && Short.valueOf(opcode) != 6) {
                Message temp = popMessage();
                sendMessage = true;
                return temp;
            }
            if (len > 2 && (Short.valueOf(opcode) == 5 || Short.valueOf(opcode) == 6))
            {
                if (nextByte == ';'){
                    sendMessage = true;
                    return popMessage();
                }

            }
        }
        return null;
    }

    private void pushByte(byte nextByte) {
        if(nextByte == '\0')
            zeroCount--;
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    public Message popMessage() {
        String message= new String(bytes);;
        String[] splitMessage= message.split("\0");;
        if (opcode ==1) //register
        {
            return new RegisterMessage(splitMessage[3],splitMessage[4],splitMessage[5]);

        }
        if (opcode ==2) //login
        {
            return new LoginMessage(splitMessage[3],splitMessage[4],splitMessage[5]);
        }
        if (opcode ==3)//logout
        {
            return new LogoutMessage();
        }
        if (opcode ==4)//follow
        {
            return new FollowMessage(splitMessage[3],splitMessage[4]);
        }
        if (opcode ==5)//post
        {
            String s = "";
            for (int i=3;i< splitMessage.length-1;i++)
            {
                s=s+splitMessage[i]+" ";
            }
            s=s.substring(0,s.length()-2);
            return new PostMessage(s);
        }
        if (opcode ==6)//pm
        {
            String cont = "";
            for (int i=5;i< splitMessage.length-2;i++)
            {
                cont=cont+splitMessage[i]+" ";
            }
            return new PMMessage(splitMessage[4],cont,splitMessage[splitMessage.length-2]);
        }
        if (opcode ==7)//logstat
        {
            return new LogstatMessage();
        }
        if (opcode ==8)//stat
        {
            String[] ListOfUsers= splitMessage[3].split("\\|");
            return new StatMessage(ListOfUsers);
        }
        if (opcode ==12)
        {
            return new BlockMessage(splitMessage[3]);
        }
        bytes = new byte[1 << 10];
        len = 0;
        opcode=-1;
        zeroCount=-1;
        return null;
    }


    @Override
    public byte[] encode(Message message) {
        String result = message.messageToString();
        return result.getBytes();
    }


    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;

    }
    @Override
    public void reset()
    {
         isReset = false;
         len = -2;
         opcodeBytes = new byte[2];
         opcode = null;
         bytes = new byte[1 << 10];
         zeroCount = -1;
    }

    public boolean isReset() {
        return isReset;
    }

    public void setReset(boolean reset) {
        isReset = reset;
    }


}