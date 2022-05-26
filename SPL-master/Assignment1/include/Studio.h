#ifndef STUDIO_H_
#define STUDIO_H_

#include <vector>
#include <string>
#include "Workout.h"
#include "Trainer.h"
#include "Action.h"


class Studio{
public:
    Studio();
    Studio(const std::string &configFilePath); // constructor
    virtual ~Studio(); // destructor;
    Studio(const Studio& other); // copy constructor
    const Studio& operator=(const Studio& other); // assignment operator
    Studio(Studio&& other); // move constructor
    const Studio& operator=(Studio&& other); // move assignment operator
    void start();
    int getNumOfTrainers() const;
    Trainer* getTrainer(int tid);
    const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
    std::vector<Workout>& getWorkoutOptions();
    std::string printWorkouts();
    void printActionLog();
    void clear();


private:
    bool open;
    int ids;
    std::string workouts;
    std::vector<Trainer*> trainers;
    std::vector<Workout> workout_options;
    std::vector<BaseAction*> actionsLog;
    int numberOfTrainers;

    BaseAction *configureTheAction(std::string input);

    Customer *createCustomer(int id, std::string name, std::string type);


};


#endif
