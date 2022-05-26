//
// Created by tal93 on 28/12/2021.
//

#include <string>
#include "../include/WriteMessage.h"
#include "../include/ConnectionHandler.h"

WriteMessage::WriteMessage(ConnectionHandler* handler, bool* logged) : connectionHandler(handler), loggedIn(logged){}

void WriteMessage::run() {
    while (1) {
        std::string answer ="";
        if (!connectionHandler -> getLine(answer)) {
            break;
        }
        if (answer != "")
            cout << answer << endl;
        if(answer == "ACK 2") {
            *loggedIn = true;
        }
        if (answer == "ACK 3") {
            std::cout << "Exiting...\n" << std::endl;
            break;
        }
    }
}
