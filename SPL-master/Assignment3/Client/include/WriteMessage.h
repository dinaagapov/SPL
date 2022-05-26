//
// Created by tal93 on 28/12/2021.
//

#ifndef SP3_TRY_WRITEMESSAGE_H
#define SP3_TRY_WRITEMESSAGE_H


#include "ConnectionHandler.h"

class WriteMessage {

    ConnectionHandler* connectionHandler;
    bool* loggedIn;
public:
    WriteMessage(ConnectionHandler* handler, bool *logged);

    void run();
};


#endif //SP3_TRY_WRITEMESSAGE_H
