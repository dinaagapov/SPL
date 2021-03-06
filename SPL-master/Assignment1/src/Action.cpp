

#include "Action.h"
#include <fstream>
#include <sstream>
#include <cstring>
#include "Studio.h"
#include<vector> // for vectors
#include<iterator> // for iterators
#include <algorithm>

extern Studio* backup;

BaseAction::BaseAction():command(""),errorMsg(""),status(ActionStatus::ERROR) {

}
BaseAction::BaseAction(const BaseAction& other):command(other.command),errorMsg(other.errorMsg),status(other.status){
}

ActionStatus BaseAction::getStatus() const {
    return ERROR;
}

void BaseAction::complete() {
    status = COMPLETED;
}

void BaseAction::error(std::string errorMsg2) {
    status = ERROR;
    std::cout <<"Error: " + errorMsg2<<std::endl;
    errorMsg=errorMsg2;
}

std::string BaseAction::getErrorMsg() const {
    return std::string();
}

void BaseAction::setCommand(std::string c){
    command = c;
}

std::string BaseAction::getCommand() {
  if (status == COMPLETED)
    return command + "Completed";
  return command + "ERROR: " + errorMsg;
}

BaseAction* BaseAction::clone()
{
    return this;
}

std::string OpenTrainer::toString() const {
    return std::string();
}
BaseAction::~BaseAction(){};

void OpenTrainer::deleteOT()
{
  for (unsigned i=0; i< customers.size();i++)
    {
      delete customers[i];
    }
  customers.clear();
}

OpenTrainer::OpenTrainer(int id, std::vector<Customer *> &customersList) : trainerId(id),customers(customersList) {
}
void OpenTrainer::act(Studio &studio) {

int left = studio.getTrainer(trainerId)->getCapacity() - studio.getTrainer(trainerId)->getCustomers().size(); 

    if (studio.getTrainer(trainerId) == nullptr || studio.getTrainer(trainerId)->isOpen() || left <= 0)
    {
        error("Trainer does not exist or is not open.");
	deleteOT();
    }

    else
    {
	bool flag = left > 0; 
        studio.getTrainer(trainerId)->openTrainer();
        std::vector<Customer *>::iterator it;
        for (it =customers.begin(); it < (customers.end());it++) {
	    if(flag > 0){
            studio.getTrainer(trainerId)->addCustomer(*it);
	    left--;
		}
            else{
		delete *it;
		}

        }
	
        complete();
    }
}
BaseAction* OpenTrainer::clone() 
{
	return new OpenTrainer(*this);
}


std::string Order::toString() const {
    return std::string();
}

void Order::act(Studio &studio) {
    if (studio.getTrainer(trainerId) == nullptr || !studio.getTrainer(trainerId)->isOpen())
    {
        error("Trainer does not exist or is not open." );
    }
    else
    {
        std::vector<Customer *>::iterator it;
        for (it =studio.getTrainer(trainerId)->getCustomers().begin(); it < studio.getTrainer(trainerId)->getCustomers().end();it++) {
            std::vector<int> workouts = (*it)->order(studio.getWorkoutOptions());
            studio.getTrainer(trainerId)->order((*it)->getId(),workouts,studio.getWorkoutOptions());
        }
        studio.getTrainer(trainerId)->setAccumulatedSalary();
        studio.getTrainer(trainerId)->printTrainerWorkouts();
        complete();
    }

}

Order::Order(int id) : trainerId(id) {

}
BaseAction* Order::clone() 
{
	return new Order(*this);
}

MoveCustomer::MoveCustomer(int src, int dst, int customerId) : srcTrainer(src) , dstTrainer(dst), id(customerId)  {

}

void MoveCustomer::act(Studio &studio) {
    if (studio.getTrainer(srcTrainer) == nullptr || studio.getTrainer(dstTrainer) == nullptr ||!studio.getTrainer(srcTrainer)->isOpen() || !studio.getTrainer(dstTrainer)->isOpen() ||studio.getTrainer(srcTrainer)->getCustomer(id) ==
                                                                                                                                                                                              nullptr || studio.getTrainer(dstTrainer)->getCapacity()<1)
    {
        error("Cannot move customer");
    }
    else
    {
        //create moving customer
        Customer* moving = studio.getTrainer(srcTrainer)->getCustomer(id);

        std::vector<std::pair<int, Workout>>::iterator it;

        std::vector<std::pair<int, Workout>> remainOrders;

        std::vector<int> workouts;

        int movingSalary=0;

        for (it =studio.getTrainer(srcTrainer)->getOrders().begin(); it < studio.getTrainer(srcTrainer)->getOrders().end();it++) {
            if ((*it).first == id)
            {
                //orders didnt stay in src trainer
                movingSalary+=(*it).second.getPrice();
                workouts.push_back((*it).second.getId());
            }
            else
            //orders that stay in trainer src
                remainOrders.push_back(*it);
        }
        //remove salary from src
        studio.getTrainer(srcTrainer)->decreaseSalary(movingSalary);
        //enter salary to dsc
        studio.getTrainer(dstTrainer)->setAccumulatedSalary(movingSalary);
        studio.getTrainer(dstTrainer)->setSalary(movingSalary);
        //remove all orders relate to moving customer
        studio.getTrainer(srcTrainer)->changeOrdersAfterMoving(remainOrders);
        //enter the new orders to new one
        studio.getTrainer(dstTrainer)->order(id,workouts,studio.getWorkoutOptions());
        //remove customer from src
        studio.getTrainer(srcTrainer)->removeCustomer(id);
        //add customer to src
        studio.getTrainer(dstTrainer)->addCustomer(moving);

        //from here

        if(studio.getTrainer(srcTrainer)->getCustomers().empty())
        {
            Close close(srcTrainer);
            close.act(studio);
        }
        complete();

    }
}

std::string MoveCustomer::toString() const {
    return std::string();
}
BaseAction* MoveCustomer::clone() 
{
	return new MoveCustomer(*this);
}

Close::Close(int id) : trainerId(id) {

}

void Close::act(Studio &studio) {
    if (studio.getTrainer(trainerId) == nullptr || !studio.getTrainer(trainerId)->isOpen())
    {
        error("Trainer does not exist or is not open.");
    }
    else
    {
        studio.getTrainer(trainerId)->closeTrainer();
        std::cout<<"Trainer " << trainerId << " closed. Salary "<< studio.getTrainer(trainerId)->getAccumulatedSalary()<<"NIS"<<std::endl;
        studio.getTrainer(trainerId)->setAccumulatedSalary();
        studio.getTrainer(trainerId)->resetSalary();
        complete();
    }

}

std::string Close::toString() const {
    return std::string();
}
BaseAction* Close::clone() 
{
	return new Close(*this);
}

void CloseAll::act(Studio &studio) {
    for (int i=0; i< studio.getNumOfTrainers();i++)
    {
        if(studio.getTrainer(i) != nullptr && studio.getTrainer(i)->isOpen()) {
            Close c(i);
            c.act(studio);
        }
    }
    complete();
}

std::string CloseAll::toString() const {
    return std::string();
}

CloseAll::CloseAll() {

}
BaseAction* CloseAll::clone() 
{
	return new CloseAll(*this);
}

PrintWorkoutOptions::PrintWorkoutOptions() {

}

void PrintWorkoutOptions::act(Studio &studio) {
std::cout <<studio.printWorkouts()<< std::endl;
complete();
}

std::string PrintWorkoutOptions::toString() const {
    return std::string();
}
BaseAction* PrintWorkoutOptions::clone() 
{
	return new PrintWorkoutOptions(*this);
}

void PrintTrainerStatus::act(Studio &studio) {

    if (studio.getTrainer(trainerId)->isOpen())
    {
        std::cout << "Trainer " << trainerId << "status:  open."<<std::endl;
        std::vector<Customer *>::iterator customerIterator;
        std::cout<<"Customers:"<<std::endl;
        for (customerIterator = studio.getTrainer(trainerId)->getCustomers().begin(); customerIterator < studio.getTrainer(trainerId)->getCustomers().end();customerIterator++)
        {
            std::cout<<(*customerIterator)->toString()<<std::endl;
        }
        std::vector<std::pair<int, Workout>>::iterator ordersIterator;
        std::cout<<"Orders:"<<std::endl;
        for (ordersIterator = studio.getTrainer(trainerId)->getOrders().begin();ordersIterator <studio.getTrainer(trainerId)->getOrders().end(); ordersIterator++)
        {
            (*ordersIterator).second.printWorkout((*ordersIterator).first);
        }
        std::cout << "Current Trainer's Salary: " << studio.getTrainer(trainerId)->getSalary() << "NIS."<<std::endl;
        complete();

    }
    else {
        std::cout << "Trainer " << trainerId << "status:  close." << std::endl;
        complete();
    }


}

std::string PrintTrainerStatus::toString() const {
    return std::string();
}


PrintTrainerStatus::PrintTrainerStatus(int id) : trainerId(id) {

}
BaseAction* PrintTrainerStatus::clone() 
{
	return new PrintTrainerStatus(*this);
}


PrintActionsLog::PrintActionsLog() {

}

void PrintActionsLog::act(Studio &studio) {
    studio.printActionLog();
    complete();
}

std::string PrintActionsLog::toString() const {
    return std::string();
}
BaseAction* PrintActionsLog::clone() 
{
	return new PrintActionsLog(*this);
}

BackupStudio::BackupStudio() {

}

void BackupStudio::act(Studio &studio) {
  if (backup != nullptr)
    delete backup;
     backup = new Studio(studio);
     complete();
}

std::string BackupStudio::toString() const {
    return std::string();
}
BaseAction* BackupStudio::clone() 
{
	return new BackupStudio(*this);
}

void RestoreStudio::act(Studio &studio) {
    if (backup == nullptr)
    {
      error("No backup available");
    }
    else
    {
      Studio *temp = backup;
      backup = new Studio(studio);
      delete temp;
      complete();
     //studio = *backup;
       // complete();
    }
}

std::string RestoreStudio::toString() const {
    return std::string();
}

RestoreStudio::RestoreStudio() {

}
BaseAction* RestoreStudio::clone() 
{
	return new RestoreStudio(*this);
}



