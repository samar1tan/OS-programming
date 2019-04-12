// Overview
//      main thread              perform as program's entry & exit
//      philosopher thread       enter->{eat<->think}->leave_and_cleanup


#include "global.h" // including shared variables (forks) and simulation config

#include <pthread.h>
#include <unistd.h> // including sleep()

#include <vector>
#include <iostream>


void enter(const int phi_id) {
	printf("Philosopher %d enters the table.\n", phi_id);
}

// may include strategy to prevent deadlock
// TODO: try to prevent starvation
void pickup_forks(const int phi_id) {
	Fork& lefthand = forks[phi_id];
	Fork& righthand = forks[(phi_id + 1) % PHILOSOPHERS_NUM];

	// strategy: taking both or none
	while(true) {
		// looking & waiting
		int smirk_cnt = 0; // for reducing printing
		// TODO: can we fix sync delay on forks' states here?
		//  Namely, left (right) -hand is set free after querying in where-clause and it leads to meaningless waiting,
		//  and then we need a "cleanup" procedure (additional signal()) in leave_and_cleanup() to fix ending deadlock.
		while (!lefthand.is_free || !righthand.is_free) {
			// TODO: can't we wait for 2 signals simultaneously?
			if (!lefthand.is_free && !righthand.is_free) { // just looking, performed as busy-looping
				if((++smirk_cnt) == 1) { // reduce printing
					printf("Philosopher %d finds no forks available and smirks.\n", phi_id);
				}

//				printf("Philosopher %d is waiting for 2 forks...\n", phi_id);
//				pthread_mutex_lock(&left_cond_lock);
//				pthread_cond_wait(&lefthand.notifier, &left_cond_lock);
//				pthread_mutex_unlock(&left_cond_lock);
//
//				pthread_mutex_lock(&right_cond_lock);
//				pthread_cond_wait(&righthand.notifier, &right_cond_lock);
//				pthread_mutex_unlock(&right_cond_lock);
			} else if (!lefthand.is_free) {
				printf("Philosopher %d is waiting for left-hand fork...\n", phi_id);
				pthread_mutex_lock(&left_cond_lock);
				pthread_cond_wait(&lefthand.notifier, &left_cond_lock);
				pthread_mutex_unlock(&left_cond_lock);
			} else {
				printf("Philosopher %d is waiting for right-hand fork...\n", phi_id);
				pthread_mutex_lock(&right_cond_lock);
				pthread_cond_wait(&righthand.notifier, &right_cond_lock);
				pthread_mutex_unlock(&right_cond_lock);
			}
		}

		// mutex pickup
		pthread_mutex_lock(&lefthand.pickup_lock);
		pthread_mutex_lock(&righthand.pickup_lock);
		if(lefthand.is_free && righthand.is_free) { // pick forks up successfully
			lefthand.is_free = righthand.is_free = false;
			pthread_mutex_unlock(&righthand.pickup_lock);
			pthread_mutex_unlock(&lefthand.pickup_lock);
			break;
		} else { // retry, going back to look & wait
			pthread_mutex_unlock(&righthand.pickup_lock);
			pthread_mutex_unlock(&lefthand.pickup_lock);
		}
	}

	printf("Philosopher %d has picked up forks.\n", phi_id);
}

void eat(const int phi_id) {
	printf("Philosopher %d is eating.\n", phi_id);
	sleep(get_duration());
}

void return_forks(const int phi_id) {
	Fork& lefthand = forks[phi_id];
	Fork& righthand = forks[(phi_id + 1) % PHILOSOPHERS_NUM];

	pthread_mutex_lock(&left_cond_lock);
	lefthand.is_free = true;
	pthread_cond_signal(&lefthand.notifier);
	pthread_mutex_unlock(&left_cond_lock);

	pthread_mutex_lock(&right_cond_lock);
	righthand.is_free = true;
	pthread_cond_signal(&righthand.notifier);
	pthread_mutex_unlock(&right_cond_lock);

	printf("Philosopher %d has returned forks.\n", phi_id);
}

void think(const int phi_id) {
	printf("Philosopher %d is thinking.\n", phi_id);
	sleep(get_duration());
}

// TODO: cleanup is unreliable because it takes in effect by waiting indefinite amount of seconds in think()
//  Furthermore, is cleanup necessary? Can we fix sync delay in return_forks()? 
void leave_and_cleanup(const int phi_id) {
	// these asynchronized signal()
	//  may become invalid confronted with OS dispatch in rare situation, like 
	//  neighboring philosopher queries but doesn't enter waiting until this philosopher leaves even after think()
	Fork& lefthand = forks[phi_id];
	pthread_cond_signal(&lefthand.notifier);

	Fork& righthand = forks[(phi_id + 1) % PHILOSOPHERS_NUM];
	pthread_cond_signal(&righthand.notifier);

	printf("Philosopher %d leaves the table.\n", phi_id);
}

// according to pthread runnable template
void* perform_philosophers(void* philosophers_id) {
	const int phi_id = *((int*)philosophers_id);

	enter(phi_id);
	// life of philosophers
	for(int i = 0; i < LONGEVITY; ++i) {
		pickup_forks(phi_id);
		eat(phi_id);
		printf("Philosopher %d has eaten for %d times.\n", phi_id, i + 1);
		return_forks(phi_id);
		think(phi_id);
	}
	leave_and_cleanup(phi_id);

	return nullptr;
}

int main() {
	// Initialize
	// forks
	for(int i = 0; i < PHILOSOPHERS_NUM; ++i) {
		forks.push_back(*(new Fork())); // fork is true if free; false otherwise
	}
	pthread_mutex_init(&left_cond_lock, nullptr);
	pthread_mutex_init(&right_cond_lock, nullptr);
	// philosophers & a waiter
	pthread_t tid[PHILOSOPHERS_NUM]; // philosophers & a waiter
	pthread_attr_t attr[PHILOSOPHERS_NUM];
	for(auto& i : attr) {
		pthread_attr_init(&i);
	}

	// Simulate
	cout << "---------------------SIMULATION CONFIG---------------------" << endl;
	cout << "Amount of philosophers (N): " << PHILOSOPHERS_NUM << endl;
	cout << "Times of eating & thinking: " << LONGEVITY << endl;
	cout << "Duration of thinking & eating: " << MIN_DURATION << "s to " << MAX_DURATION << "s" << endl;
	cout << "Philosopher X need fork[X] & fork[(X+1)%N] before eating" << endl;
	cout << "---------------------------TRACE---------------------------" << endl;

	vector<int> phi_ids; // starting from 0
	phi_ids.reserve(PHILOSOPHERS_NUM);
	for(int i = 0; i < PHILOSOPHERS_NUM; ++i) {
		phi_ids.push_back(i);
	}
	for(int i = 0; i < PHILOSOPHERS_NUM; ++i) {
		pthread_create(&tid[i], &attr[i], perform_philosophers, &phi_ids[i]);
	}

	for(auto i : tid) { // main thread will be terminated at last
		pthread_join(i, nullptr);
	}

	printf("\n------------------------HAPPY ENDING-----------------------\n");

	return 0;
}
