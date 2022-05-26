

#include <boost/asio/ip/tcp.hpp>
#include <iostream>
#include "ConnectionHandler.h"

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // 
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, ';');
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    short opcode = -1;
    int opcodeCounter = 0;
    int msgOpcodeCounter = 0;
    short msgOpcode = -1;
    ///char* Name;
    //char* opcodeFrom;
   // int counterForSpace=0;
    bool checkStart = false;
    vector<char> opcodeBytes;
    vector<char> msgOpcodeBytes;
    int ackCounter=4;
    string msg="";
    int PMPO=-1;
    try {
        do{
            if(opcode == -1) {
                getBytes(&ch, 1);
                //opcodeFrom =opcodeFrom+ch;
                opcodeBytes.push_back(ch);
                opcodeCounter++;
                if(opcodeCounter == 2){
                    //opcode = bytesToShort(opcodeBytes);
                    opcode = opcodeFinder(opcodeBytes);
                }
            }
            else {
                if (opcode == 9) //notification
                {

                    if (!checkStart) {
                        frame = frame+ "NOTIFICATION ";
                        getBytes(&ch, 1);
                        int pmOrPublic = ch - '0';
                        if (pmOrPublic == 0) {
                            frame = frame+ "PM ";
                            PMPO = 1;
                        } else {
                            frame = frame+ "PUBLIC ";
                            PMPO=0;
                        }
                        checkStart = true;
                    } else {
                        if (PMPO==0) {
                            while (ch != 'P') {
                                getBytes(&ch, 1);
                            }
                            getBytes(&ch, 1);
                            getBytes(&ch, 1);
                            getBytes(&ch, 1);
                            getBytes(&ch, 1);
                            while (ch != ';') {
                                frame = frame + ch;
                                getBytes(&ch, 1);
                            }
                        }
                        else
                        {
                            getBytes(&ch, 1);
                            getBytes(&ch, 1);
                            while (ch != ';') {
                                frame = frame + ch;
                                getBytes(&ch, 1);
                            }
                        }
                        PMPO=-1;
                        return true;
                    }
                }
                if (opcode == 10)//ACK
                {
                    getBytes(&ch, 1);
                    if (ackCounter == 0) {
                        if (msgOpcode == -1) {
                            msgOpcodeBytes.push_back(ch);
                            msgOpcodeCounter++;
                            if (msgOpcodeCounter == 2) {
                                msgOpcode = opcodeFinder(msgOpcodeBytes);
                                msg = msg + to_string(msgOpcode);
                            }
                        } else {
                            if (msgOpcode == 4) //FOLLOW
                            {
                                //getBytes(&ch, 1);
                                if (ch=='\0')
                                    msg = msg + " ";
                                msg = msg + ch;
                            }
                            if (msgOpcode == 7 || msgOpcode == 8) //LOGSTAT
                            {
                                while (ch != ';') {
                                    frame = frame + ch;
                                    getBytes(&ch, 1);
                                }
                                frame = msg + frame.substr(0,frame.length()-2);
                                return true;
                            }
                        }
                    } else {
                        if (ackCounter == 4) msg = msg + "ACK ";
                        ackCounter--;
                    }
                }
                if (opcode == 11)//ERROR
                {
                    if (msgOpcode == -1) {
                        getBytes(&ch, 1);
                        if (ch=='@')
                        {
                            frame = frame + "Error ";
                            while (ch != ';') {
                                frame = frame + ch;
                                getBytes(&ch, 1);
                            }
                            return true;
                        }
                        else
                        {
                            msgOpcodeBytes.push_back(ch);
                            msgOpcodeCounter++;
                            if (msgOpcodeCounter == 2) {
                                msgOpcode = opcodeFinder(msgOpcodeBytes);
                                msg = msg + "ERROR "+ to_string(msgOpcode);
                                frame = msg;
                                return true;
                            }
                        }
                    }
                }
            }
            //getBytes(&ch, 1);
            //frame.append(1, ch);
        }while (delimiter != ch);

    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    frame = msg;
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    istringstream toSend(frame);
    string current;
    int len = 2;
    vector<string> vars;
    //char* messageToByte = new char[2];
    vector<char> messageByte;

    short opcode;
    while(toSend >> current) {
        vars.push_back(current);
    }
    string command = vars[0];
    if(command == "REGISTER"){
        opcode=1;
        string username = vars[1];
        string password = vars[2];
        string birthday = vars[3];
        shortToBytes(opcode,messageByte);
        for(char i : username){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        for(char i : password){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        for(char i : birthday){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        len++;
        len++;
    }
    if(command == "LOGIN"){
        opcode=2;
        string username = vars[1];
        string password = vars[2];
        shortToBytes(opcode,messageByte);
        for(char i : username){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        for(char i : password){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        for(char i : vars[3]){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        len++;
        len++;

    }
    else if(command == "LOGOUT"){
        opcode = 3;
        shortToBytes(opcode, messageByte);
    }
    else if(command == "FOLLOW") {
        opcode = 4;
        shortToBytes(opcode, messageByte);
        messageByte.push_back(vars[1].at(0));
        messageByte.push_back('\0');
        for(char i : vars[2]){
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        len++;
        len++;
    }
    else if(command == "POST"){
        opcode = 5;
        shortToBytes(opcode, messageByte);
        string content;
        for (unsigned i=0; i<vars.size();i++) {
            for (char c: vars[i]) {
                len++;
                messageByte.push_back(c);
            }
            messageByte.push_back('\0');
            len++;
        }
        messageByte.push_back('\0');
        len++;
        len++;
    }
    else if(command == "PM"){
        opcode = 6;
        shortToBytes(opcode, messageByte);
        for (unsigned i=0; i<vars.size();i++) {
            for (char c: vars[i]) {
                len++;
                messageByte.push_back(c);
            }
            messageByte.push_back('\0');
            len++;
        }
        //
        time_t tt;
        struct tm * ti;
        time (&tt);
        ti = localtime(&tt);
        string curDate = figureDat(ti);
        for (char c : curDate)
        {
            len++;
            messageByte.push_back(c);
        }
        messageByte.push_back('\0');
        len++;
        len++;
    }
    else if(command == "LOGSTAT") {
        opcode = 7;
        shortToBytes(opcode, messageByte);
    }
    else if(command == "STAT"){
        opcode = 8;
        shortToBytes(opcode, messageByte);
        for(char i : vars[1]) {
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        len++;
        len++;
    }
    else if(command == "BLOCK"){
        opcode = 12;
        shortToBytes(opcode, messageByte);
        for(char i : vars[1]) {
            len++;
            messageByte.push_back(i);
        }
        messageByte.push_back('\0');
        len++;
        len++;
    }
    messageByte.push_back(';');
    len++;
    char sendMes[len+1];
    for(unsigned int i=0; i<=(unsigned)len+1; i++){
        sendMes[i]=messageByte[i];
    }
    bool result=sendBytes(sendMes,len+1);
    if(!result) return false;
    return sendBytes(&delimiter,1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

void ConnectionHandler::shortToBytes(short num, vector<char> &bytesArr) {
    bytesArr.push_back((num >> 8) & 0xFF);
    bytesArr.push_back(num & 0xFF);
}

short ConnectionHandler::bytesToShort(vector<char> &bytesVec) {
    short result = (short)((bytesVec[0] & 0xff) << 8);
    result += (short)(bytesVec[1] & 0xff);
    bytesVec.clear();
    return result;
}
short ConnectionHandler::opcodeFinder(vector<char> &bytesVec) {
    short temp = 10*(bytesVec[0]-'0') + bytesVec[1]-'0';
    return temp;
}
string ConnectionHandler::figureDat(struct tm * ti)
{
    string currentDate="";
    if (ti->tm_mday < 10)
        currentDate = currentDate + "0"+to_string(ti->tm_mday)+ "-";
    else
        currentDate = currentDate +to_string(ti->tm_mday)+ "-";;
    if (ti->tm_mon+1 < 10)
        currentDate = currentDate + "0"+to_string(ti->tm_mon+1)+ "-";
    else
        currentDate = currentDate +to_string(ti->tm_mon+1)+ "-";
    //year
    string year =  asctime(ti);
    currentDate = currentDate +year.substr(year.length()-5,year.length()-1);
    currentDate=currentDate.substr(0,currentDate.length()-1)+" ";
    if (ti->tm_hour < 10)
        currentDate = currentDate + "0"+to_string(ti->tm_hour)+ ":";
    else
        currentDate = currentDate +to_string(ti->tm_hour)+ ":";
    if (ti->tm_min < 10)
        currentDate = currentDate + "0"+to_string(ti->tm_min);
    else
        currentDate = currentDate +to_string(ti->tm_min);
    return currentDate;

}





