#ifndef TRAINER_H_
#define TRAINER_H_

#include <vector>
#include "Customer.h"
#include "Workout.h"

typedef std::pair<int, Workout> OrderPair;

class Trainer{
public:
    Trainer(int t_capacity); // constructor
    virtual ~Trainer(); // destructor 
    Trainer(const Trainer& other); // copy constuctor
    Trainer& operator=(const Trainer& other); // assignment operator
    Trainer(Trainer&& other); // move constructor
    const Trainer& operator=(Trainer&& other); // move assignment operator
    int getCapacity() const;
    void addCustomer(Customer* customer);
    void removeCustomer(int id);
    Customer* getCustomer(int id);
    std::vector<Customer*>& getCustomers();
    std::vector<OrderPair>& getOrders();
    void order(const int customer_id, const std::vector<int> workout_ids, const std::vector<Workout>& workout_options);
    void openTrainer();
    void closeTrainer();
    int getSalary();
    bool isOpen();
    void setSalary(int WageIncrease);
    void decreaseSalary(int WageDecrease);
    void setAccumulatedSalary();
    void setAccumulatedSalary(int movingSalary);
    int getAccumulatedSalary();
    void resetSalary();
    void changeOrdersAfterMoving(std::vector<OrderPair> nol);
    void printTrainerWorkouts();
private:
    int capacity;
    bool open;
    int salary;
    int accumulatedSalary;
    std::vector<OrderPair> orderList;
    std::vector<Customer*> customersList;
    //A list of pairs for each order for the trainer - (customer_id, Workout)
};


#endif
