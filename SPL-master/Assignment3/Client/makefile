# All Targets
all: run

# Tool invocations
# Executable "hello" depends on the files hello.o and run.o.

run: bin/main.o bin/Studio.o bin/Trainer.o bin/Customer.o bin/Workout.o bin/Action.o
	@echo 'Building target: run'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/run bin/main.o bin/Studio.o bin/Trainer.o bin/Workout.o bin/Customer.o bin/Action.o
	@echo 'Finished building target: run'
	@echo ' '

# Depends on the source and header files
bin/main.o: src/main.cpp 
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/main.o src/main.cpp 

bin/Studio.o: src/Studio.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Studio.o src/Studio.cpp 

bin/Trainer.o: src/Trainer.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Trainer.o src/Trainer.cpp

bin/Customer.o: src/Customer.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Customer.o src/Customer.cpp

bin/Action.o: src/Action.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Action.o src/Action.cpp

bin/Workout.o: src/Workout.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Workout.o src/Workout.cpp
	
#Clean the build directory
clean:
	@echo 'cleaning'
	rm -f bin/*
