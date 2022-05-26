#include "Trainer.h"
#include<iostream>
#include<vector> // for vectors
#include<iterator> // for iterators
#include <algorithm>

Trainer::Trainer(int t_capacity):capacity(t_capacity),open(false),salary(0),accumulatedSalary(0),orderList(),customersList()
{
   
}

Trainer::~Trainer()
{
    for (unsigned i = 0; i < customersList.size(); i++){
        delete customersList[i];
}
	customersList.clear();

}

Trainer::Trainer(const Trainer& other) :
capacity(other.capacity),open(other.open),salary(other.salary),accumulatedSalary(other.accumulatedSalary),
orderList(other.orderList),customersList()
{
 
    auto iter = other.customersList.begin();
    std::vector<Customer*>::iterator it;
    for (it = getCustomers().begin(); it < getCustomers().end(); it++)
    {
        Customer* c = (*iter)->clone();
        customersList.push_back(c);
    }
    
}

Trainer& Trainer::operator=(const Trainer& other)
{

    if (this != &other)
    {
        for (unsigned i = 0; i < customersList.size(); i++)
            delete customersList[i];
        capacity = other.getCapacity();
        open = other.open;
        salary = other.salary;
        accumulatedSalary = other.accumulatedSalary;
        orderList = other.orderList;
        auto iter = other.customersList.begin();
        std::vector<Customer*>::iterator it;
        for (it = getCustomers().begin(); it < getCustomers().end(); it++)
        {
            Customer* c = (*iter)->clone();
            customersList.push_back(c);
        }
    }
    return *this;
}

Trainer::Trainer(Trainer&& other) : capacity(other.getCapacity()), open(other.open), salary(other.salary), accumulatedSalary(other.accumulatedSalary),orderList(other.orderList),customersList()

{

    for (unsigned i = 0; i < other.customersList.size(); i++) 
    {
        customersList[i] = other.customersList[i];
        other.customersList[i] = nullptr;
    }
	other.customersList.clear();

}

const Trainer& Trainer::operator=(Trainer&& other) 

{
    for (unsigned i = 0; i < customersList.size(); i++)
        delete customersList[i];
    capacity = other.getCapacity();
    open = other.open;
    salary = other.salary;
    accumulatedSalary = other.accumulatedSalary;
    orderList = other.orderList;
    for (unsigned i = 0; i < other.customersList.size(); i++)
    {
        customersList[i] = other.customersList[i];
        other.customersList[i] = nullptr;
    }
    return *this;
}


int Trainer::getCapacity() const
{

    return capacity;
}

void Trainer::addCustomer(Customer* customer)
{
    capacity--;
    customersList.push_back(customer);
}

void Trainer::removeCustomer(int id)
{
    std::vector<Customer *>temp;
    std::vector<Customer *>::iterator it;
    for (it =getCustomers().begin(); it < getCustomers().end();it++) {
        if((*it)->getId()!=id)
        {
            temp.push_back(*it);
        }
    }
    customersList.clear();
    std::vector<Customer *>::iterator it2;
    for (it2 = temp.begin(); it2 < temp.end();it2++)
    {
        customersList.push_back(*it2);
    }
}

Customer* Trainer::getCustomer(int id)
{
    std::vector<Customer *>::iterator it;
    for (it = customersList.begin(); it < customersList.end();it++)
    {
        if ((*it)->getId() == id)
            return (*it);
    }
    return nullptr;
}

//std::vector<Customer*>& Trainer::getCustomers()
//{
//    // TODO: insert return statement here
//}
//
//std::vector<OrderPair>& Trainer::getOrders()
//{
//    // TODO: insert return statement here
//}

void Trainer::order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options)
{
    auto iter = workout_ids.begin();
    for (; iter != workout_ids.end(); iter++)
    {
        setSalary(workout_options[(*iter)].getPrice());
        OrderPair *o = new OrderPair (customer_id,workout_options[(*iter)]);
        std::vector<std::pair<int, Workout>>::iterator it;
        bool check=false;
        for (it = orderList.begin();it < orderList.end();it++)
        {

            if (o->first == it->first && o->second.getName() ==it->second.getName())
                check =true;
        }
        if (!check)
            orderList.push_back(*o);
	delete o;
    }
}

void Trainer::openTrainer()
{
    open = true;
}

void Trainer::closeTrainer()
{
    open = false;
}

int Trainer::getSalary()
{
    return salary;
}

bool Trainer::isOpen()
{
    return open;
}

void Trainer::setSalary(int WageIncrease) {
    salary+=WageIncrease;
}

std::vector<Customer *> &Trainer::getCustomers() {
    return customersList;
}

std::vector<OrderPair> &Trainer::getOrders() {
    return orderList;
}

void Trainer::setAccumulatedSalary() {
    accumulatedSalary+=salary;
}

void Trainer::resetSalary() {
    salary=0;

}

void Trainer::decreaseSalary(int WageDecrease) {
    salary -=WageDecrease;
}

void Trainer::changeOrdersAfterMoving(std::vector<std::pair<int, Workout>> nol) {
    //std::vector<std::pair<int, Workout>> Trainer::orderList
    orderList.clear();
    std::vector<std::pair<int, Workout>>::iterator it;
    for (it = nol.begin();it < nol.end();it++)
    {
        orderList.push_back(*it);
    }


}

void Trainer::printTrainerWorkouts() {
    std::vector<std::pair<int, Workout>>::iterator it;
    for (it = orderList.begin();it < orderList.end();it++)
    {
        std::cout << getCustomer(it->first)->getName() << " is doing " << it->second.getName()<<std::endl;
    }
}

void Trainer::setAccumulatedSalary(int movingSalary) {
    accumulatedSalary+=movingSalary;
}

int Trainer::getAccumulatedSalary() {
    return accumulatedSalary;
}




