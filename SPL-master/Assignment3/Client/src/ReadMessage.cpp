//
// Created by tal93 on 28/12/2021.
//

#include "../include/ReadMessage.h"
#include "../include/ConnectionHandler.h"

ReadMessage::ReadMessage(ConnectionHandler* handler, bool* logged, thread* write) :
        connectionHandler(handler), loggedIn(logged), writeThread(write){}

void ReadMessage::run() {
    while (1) {
        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
        string line(buf);
        if (!connectionHandler -> sendLine(line)) {
            break;
        }
        if(line == "LOGOUT" && *loggedIn) {
            writeThread -> join();
            break;
        }
    }
}