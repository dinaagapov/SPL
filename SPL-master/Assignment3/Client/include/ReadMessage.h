//
// Created by tal93 on 28/12/2021.
//

#ifndef SP3_TRY_READMESSAGE_H
#define SP3_TRY_READMESSAGE_H
#include <thread>
#include <iostream>
#include "ConnectionHandler.h"

class ReadMessage {

    ConnectionHandler *connectionHandler;
    bool *loggedIn;
    thread* writeThread;

public:
    ReadMessage(ConnectionHandler *handler, bool *logged, std::thread *write);

    void run();
};


#endif //SP3_TRY_READMESSAGE_H
