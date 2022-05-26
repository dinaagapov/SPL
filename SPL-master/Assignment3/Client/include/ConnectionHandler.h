#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <sstream>
#include <vector>

using boost::asio::ip::tcp;
using namespace std;

class ConnectionHandler {
private:
    const string host_;
    const short port_;
    boost::asio::io_service io_service_;  
    tcp::socket socket_;

public:
    ConnectionHandler(std::string host, short port);

    virtual ~ConnectionHandler();


    bool connect();

    bool getBytes(char bytes[], unsigned int bytesToRead);

  
    bool sendBytes(const char bytes[], int bytesToWrite);

    bool getLine(std::string& line);

    bool sendLine(std::string& line);



    void close();


    bool getFrameAscii(string &frame, char delimiter);

    bool sendFrameAscii(const string &frame, char delimiter);

    void shortToBytes(short num, char *bytesArr);

    short bytesToShort(char *bytesArr);

    void shortToBytes(short num, vector<char> &bytesVec);


    short bytesToShort(vector<char> &bytesVec);

    short Convertopcode(vector<char> &bytesVec);

    short opcodeFinder(vector<char> &bytesVec);

    string figureDat(tm *time);

    string figureDat();

    string getErrorMesg();
}; 

#endif
