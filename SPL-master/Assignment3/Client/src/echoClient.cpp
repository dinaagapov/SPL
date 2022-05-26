//
// Created by tal93 on 27/12/2021.
//

#include "../include/echoClient.h"
//
// Created by tal93 on 27/12/2021.
//

#ifndef SP3_TRY_ECHOCLIENT_H
#define SP3_TRY_ECHOCLIENT_H


#include <iostream>
#include "../include/ConnectionHandler.h"
#include "../include/WriteMessage.h"
#include "../include/ReadMessage.h"


int main (int argc, char *argv[]) {
//    if (argc < 3) {
//        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
//        return -1;
//    }
    short port = 7777;
    std::string host = "127.0.0.1";
    //std::string host = argv[1];
    //short port = atoi(argv[2]);
    ConnectionHandler* connectionHandler = new ConnectionHandler(host, port);
    if (!connectionHandler->connect()) {
        std::cerr << "Cann->t connect to " << host << ":" << port << std::endl;
        return 1;
    }
    bool* loggedIn = new bool(false);
    WriteMessage writeMessage(connectionHandler, loggedIn);
    thread* writeThread = new thread(&WriteMessage::run, &writeMessage);
    ReadMessage readMessage(connectionHandler, loggedIn, writeThread);
    thread* readThread = new thread(&ReadMessage::run, &readMessage);
    readThread->join();
    //writeThread->join();
    delete connectionHandler;
    delete readThread;
    delete writeThread;
    delete loggedIn;
    return 0;

};


#endif //SP3_TRY_ECHOCLIENT_H
