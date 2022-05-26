#include "Studio.h"
#include <fstream>
#include <sstream>
#include <cstring>

using namespace std;

Studio::Studio() :open(true),ids(0),workouts(""),trainers(),workout_options(),actionsLog(),numberOfTrainers(0)
{

}

Studio::Studio(const std::string& configFilePath) : open(true),ids(0),workouts(""),trainers(),workout_options(),actionsLog(),numberOfTrainers(0)
{
    std::ifstream file(configFilePath);
    if (file.is_open())
    {
        std::string line;
        int flag=0;
        int numOfWorkout=0;
        while (getline(file, line))
        {
            if (!line.empty() && line[0] != '#')
            {
                if (flag == 2)
                {
                    vector<string> WorkoutInfo;
                    std::string delimiter = ", ";
                    size_t pos = 0;
                    while ((pos = line.find(delimiter)) != std::string::npos) {
                        WorkoutInfo.push_back(line.substr(0, pos));
                        line.erase(0, pos + delimiter.length());
                    }
                    WorkoutInfo.push_back(line);
                    string s = WorkoutInfo.at(1);
                    WorkoutType wt;
                    if (s == "Mixed") wt = MIXED;
                    if (s == "Anaerobic")wt = ANAEROBIC;
                    if (s == "Cardio") wt = CARDIO;
                    Workout *w = new Workout(numOfWorkout,WorkoutInfo.at(0),std::stoi(WorkoutInfo.at(2)),wt);
                    workout_options.push_back(*w);
                    numOfWorkout++;
		    delete w;
                }
                if (flag == 1)
                {
                    vector<string> capacities;
                    std::string delimiter = ",";
                    size_t pos = 0;
                    while ((pos = line.find(delimiter)) != std::string::npos) {
                        capacities.push_back(line.substr(0, pos));
                        line.erase(0, pos + delimiter.length());
                    }
                    capacities.push_back(line);
                    vector<string>::iterator it;
                    for (it=capacities.begin(); it < capacities.end();++it)
                    {
                        Trainer* t = new Trainer(stoi(*it));
                        t->closeTrainer();
                        trainers.push_back(t);

                    }
                    flag++;
		   
                }
                if (flag == 0)
                {
                    numberOfTrainers = std::stoi(line);
                    flag++;
                }

            }
        }

    }
}

Studio::~Studio()
{
  for (unsigned i = 0; i < trainers.size(); i++)
	{
        delete trainers[i];
	}
	trainers.clear();
    for (unsigned i = 0; i < actionsLog.size(); i++)
	{
        delete actionsLog[i];
	}
	actionsLog.clear();
	trainers.clear();
	workout_options.clear();	
}




Studio::Studio(const Studio& other):open(other.open),ids(other.ids),workouts(other.workouts)
,trainers(),workout_options(),actionsLog(),numberOfTrainers(other.numberOfTrainers)
{

    workout_options = other.workout_options;


    for(BaseAction* act: other.actionsLog){
        actionsLog.push_back(act->clone());
    }
    for(Trainer* trainer: other.trainers){
        trainers.push_back(new Trainer(*trainer));
    }
}

const Studio& Studio::operator=(const Studio& other)
{
if(this!=&other){
        clear();
        open = other.open;
        workout_options = other.workout_options;
        for (Trainer* trainer: other.trainers) {
            trainers.push_back(new Trainer(*trainer));
        }
        for (BaseAction* action: other.actionsLog) {
            actionsLog.push_back(action->clone());
        }
    }
    return *this;
}

Studio::Studio(Studio&& other):open(other.open),ids(other.ids),workouts(other.workouts)
,trainers(),workout_options(),actionsLog(),numberOfTrainers(other.numberOfTrainers)
{
    open = other.open;
    workout_options = other.workout_options;
    trainers = other.trainers;
    actionsLog = other.actionsLog;

}

const Studio& Studio::operator=(Studio&& other)
{
    clear();
    open = other.open;
    workout_options=other.workout_options;
    trainers = other.trainers;
    actionsLog = other.actionsLog;
    return *this;
}

void Studio::start()
{

    open = true;
    std::cout<<"Studio is now open!"<<std::endl;
    string input;
    while (input != "closeall")
    {
        getline(cin,input);
        BaseAction* action = configureTheAction(input);
        if (action != nullptr)
        {
          action->setCommand(input);
          action->act(*this);
          actionsLog.push_back(action);
        }
    }
    CloseAll* closeAll = new CloseAll();
    closeAll->act(*this);
    open = false;
    delete closeAll;
}


int Studio::getNumOfTrainers() const
{
    return numberOfTrainers;
}

Trainer* Studio::getTrainer(int tid)
{
    if((unsigned)tid > trainers.size())
	return nullptr;
    else
	return trainers[tid];

}
const std::vector<BaseAction*>& Studio::getActionsLog() const
{
    return actionsLog;
}

std::vector<Workout>& Studio::getWorkoutOptions()
{
    return workout_options;

}

BaseAction* Studio::configureTheAction(std::string input)
{


    if (input.substr(0, 4) == "open")
    {
        string numOfTrainerString = input.substr(5, 1);
        int numOfTrainer = stoi(numOfTrainerString);
        string costumers = input.substr(7);
        vector<string> result;
        stringstream s_stream(costumers);
        while (s_stream.good()) // separate the line to <name1>,<strategy1>
        {
            string substr;
            getline(s_stream, substr, ' ');
            result.push_back(substr);
        }

        std::vector<Customer*> customerList;
        // here we want to find the index of the comma in <name1>,<strategy1>
        vector<string>::iterator it;
        for (it=result.begin(); it < result.end();it++)
        {
            vector<string> Pair;
            std::string delimiter = ",";
            size_t pos = 0;
            while ((pos = (*it).find(delimiter)) != std::string::npos) {
                Pair.push_back((*it).substr(0, pos));
                (*it).erase(0, pos + delimiter.length());
            }
            Pair.push_back((*it));
            Customer* customer = createCustomer(ids, Pair.at(0), Pair.at(1));
            customerList.push_back(customer);

            ids++;
        }
        OpenTrainer* openTrainer = new OpenTrainer(numOfTrainer, customerList);
        return openTrainer;
    }

    else if (input.substr(0, 5) == "order")
    {
        string numOfTrainerString = input.substr(6, 1);
        int numOfTrainer = stoi(numOfTrainerString);
        Order* order = new Order(numOfTrainer);
        return order;

    }

    else if (input.substr(0, 4) == "move")
    {
        string numOfTrainerOriginString = input.substr(5, 1);
        string numOfTrainerDesString = input.substr(7, 1);
        string numOfCustomerString = input.substr(9, 1);
        int numOfTrainerOrigin = stoi(numOfTrainerOriginString);
        int numOfTrainerDes = stoi(numOfTrainerDesString);
        int numOfTrainerCustomer = stoi(numOfCustomerString);

        MoveCustomer* moveCustomer = new MoveCustomer(numOfTrainerOrigin,
                                                      numOfTrainerDes, numOfTrainerCustomer);
        return moveCustomer;

    }

    else if (input.substr(0, 5) == "close")
    {
    if (input != "closeall"){
        string numOfTrainerString = input.substr(6, 1);
        int numOfTrainer = stoi(numOfTrainerString);
        Close* close = new Close(numOfTrainer);
        return close;
        }
    }
    else if (input.substr(0, 6) == "status")
    {
        string numOfTrainerString = input.substr(7, 1);
        int numOfTrainer = stoi(numOfTrainerString);
        PrintTrainerStatus* printTrainerStatus = new PrintTrainerStatus(numOfTrainer);
        return printTrainerStatus;
    }

    else if (input.substr(0, 15) == "workout_options")
    {

        PrintWorkoutOptions* printWorkOutOptions = new PrintWorkoutOptions();
        return printWorkOutOptions;
    }
    else if (input.substr(0, 3) == "log")
    {
        PrintActionsLog* printActionsLog = new PrintActionsLog();
        return printActionsLog;
    }
    else if (input.substr(0, 6) == "backup")
    {
        BackupStudio* backup = new BackupStudio();
        return backup;
    }
    else if (input.substr(0, 7) == "restore") {
        RestoreStudio* restore = new RestoreStudio();
        return restore;

    }
    return nullptr;
}

Customer* Studio::createCustomer(int id, std::string name, std::string type)
{
    if (type == "swt") {
        SweatyCustomer* sweat = new SweatyCustomer(name, id);
        return sweat;


    }
    else if (type == "chp") {
        CheapCustomer* cheap = new CheapCustomer(name, id);
        return cheap;


    }
    else if (type == "mcl") {
        HeavyMuscleCustomer* muscle = new HeavyMuscleCustomer(name, id);
        return muscle;


    }
    else if (type == "fbd") {
        FullBodyCustomer* fullBody = new FullBodyCustomer(name, id);
        return fullBody;


    }
    return nullptr;
}

string Studio::printWorkouts() {
    return workouts;
}

void Studio::printActionLog() {
    std::vector<BaseAction*>::iterator iterator;
    for (iterator = actionsLog.begin();iterator < actionsLog.end();iterator++)
    {
        std::cout << (*iterator)->getCommand() <<std::endl; //check what is printing ?
    }
}
void Studio::clear() {
    for(Trainer* trainer:trainers){
        if(trainer) {
            delete trainer;
            trainer = nullptr;
        }
    }
    trainers.clear();
    for(BaseAction* act:actionsLog){
        if(act) {
            delete act;
            act = nullptr;
        }
    }
    actionsLog.clear();
    workout_options.clear();	
}



