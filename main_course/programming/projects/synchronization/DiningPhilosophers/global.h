//
// Created by samaritan on 4/10/19.
//

#ifndef DININGPHILOSOPHERS_GLOBAL_H
#define DININGPHILOSOPHERS_GLOBAL_H


#include "config.h"

#include <random>
#include <functional>

using namespace std;


// Shared Variables
struct Fork {
	bool is_free; // state
	pthread_mutex_t pickup_lock; // mutex pickup
	pthread_cond_t notifier; // to sync philosopher threads
	Fork() {
		is_free = true;
		pthread_mutex_init(&pickup_lock, nullptr);
		pthread_cond_init(&notifier, nullptr);
	}
};
// philosopher sits between forks[phi_id] & forks[(phi_id + 1) % PHILOSOPHERS_NUM]
vector<Fork> forks;
pthread_mutex_t left_cond_lock;
pthread_mutex_t right_cond_lock;

// Utilities
// random numbers generator for simulate thinking
random_device rd; // seed
default_random_engine generator(rd());
uniform_int_distribution<int> distribution(MIN_DURATION, MAX_DURATION);
auto get_duration = bind(distribution, generator);


#endif //DININGPHILOSOPHERS_GLOBAL_H
