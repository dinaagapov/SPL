#include "Customer.h"
#include<iostream>
#include<vector>
#include<iterator>
#include <algorithm>

Customer::Customer(std::string c_name, int c_id):name(c_name),id(c_id)
{
}

std::string Customer::getName() const
{
    return name;
}


int Customer::getId() const
{
    return id;
}

Customer::~Customer(){};


FullBodyCustomer::FullBodyCustomer(std::string name, int id) : Customer(name,id)
{
}
Customer* FullBodyCustomer::clone() 
{
	return new FullBodyCustomer(*this);
}

std::vector<int> FullBodyCustomer::order(const std::vector<Workout>& workout_options)
{

    int priceCardio = 0;
    int idCardio = -1;
    int priceMix = 0;
    int idMix = -1;
    int priceAna = 0;
    int idAna = -1;
    std::vector<int> output;
    auto iter = workout_options.begin();
    for (; iter != workout_options.end(); iter++)
    {
        if ((*iter).getType() == CARDIO &&(idCardio == -1 || ((*iter).getPrice() < priceCardio)))
        {
            priceCardio = ((*iter).getPrice());
            idCardio = ((*iter).getId());
        }
        else if ((*iter).getType() == MIXED && (idMix == -1 || ((*iter).getPrice() > priceMix)))
        {
            priceMix = ((*iter).getPrice());
            idMix = ((*iter).getId());
        }
        else if((*iter).getType() == ANAEROBIC && (idAna == -1 || ((*iter).getPrice() < priceAna)))
        {
            priceAna = ((*iter).getPrice());
            idAna = ((*iter).getId());
        }

    }
    if (idCardio != -1)
        output.push_back(idCardio);
    if (idMix != -1)
        output.push_back(idMix);
    if (idAna!= -1)
        output.push_back(idAna);
    return output;
}

std::string FullBodyCustomer::toString() const
{

    return std::to_string(getId()) +","+  getName();
}

HeavyMuscleCustomer::HeavyMuscleCustomer(std::string name, int id) : Customer(name, id)
{
}
Customer* HeavyMuscleCustomer::clone() 
{
	return new HeavyMuscleCustomer(*this);
}

std::vector<int> HeavyMuscleCustomer::order(const std::vector<Workout>& workout_options)
{

    std::vector<int> prices;
    std::vector<int> ids;
    auto iter = workout_options.begin();
    for (; iter != workout_options.end(); iter++)
    {
        if ((*iter).getType() == ANAEROBIC)
        {
            prices.push_back((*iter).getPrice());
        }
    }
    std::sort(prices.begin(),prices.end());
    std::reverse(prices.begin(),prices.end());
    auto PriceIter = prices.begin();
    for (; PriceIter != prices.end(); PriceIter++) {
        auto iter2 = workout_options.begin();
        for (;  iter2!= workout_options.end(); iter2++) {
            if ((*iter2).getPrice() == (*PriceIter) && iter2->getType()== ANAEROBIC)
                ids.push_back(iter2->getId());
        }
    }

    return ids;
}

std::string HeavyMuscleCustomer::toString() const
{

    return std::to_string(getId()) +","+  getName();
}
CheapCustomer::CheapCustomer(std::string name, int id) : Customer(name, id)
{
}
Customer* CheapCustomer::clone() 
{
	return new CheapCustomer(*this);
}

std::vector<int> CheapCustomer::order(const std::vector<Workout>& workout_options)
{
    std::vector<int> Prices;
    std::vector<int> Ids;
    auto iter = workout_options.begin();
    Prices.push_back(iter->getPrice());
    Ids.push_back(iter->getId());
    iter++;
    for (; iter != workout_options.end(); iter++)
    {
        if (iter->getPrice() < Prices.at(0))
        {
            Prices.pop_back();
            Ids.pop_back();
            Prices.push_back(iter->getPrice());
            Ids.push_back(iter->getId());

        }
    }
    return Ids;
}

std::string CheapCustomer::toString() const
{

    return std::to_string(getId()) +","+  getName();
}
SweatyCustomer::SweatyCustomer(std::string name, int id) : Customer(name, id)
{
}
Customer* SweatyCustomer::clone() 
{
	return new SweatyCustomer(*this);
}

std::vector<int> SweatyCustomer::order(const std::vector<Workout>& workout_options)
{
    std::vector<int> output;
    auto iter = workout_options.begin();
    for (; iter != workout_options.end(); iter++)
    {
        if ((*iter).getType() == CARDIO)
        {
            output.push_back((*iter).getId());
        }
    }
    return output;
}

std::string SweatyCustomer::toString() const
{

    return std::to_string(getId()) +","+  getName();
}
