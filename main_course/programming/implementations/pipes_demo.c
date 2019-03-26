#define BUFFER_SIZE 32
#define READ_END 0
#define WRITE_END 1

#include <sys/types.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

int main() {
	// fd: file descriptors
	// fd[0]: read-end of the pipe
	// fd[1]: write-end of the pipe
	int fd[2];
	if(pipe(fd) == -1) {
		fprintf(stderr, "Pipe failed");
		return 1;
	}

	pid_t pid = fork();
	if(pid < 0) {
		fprintf(stderr, "Fork failed");
		return 1;
	} else if (pid > 0) { // parent process
		close(fd[READ_END]);

		char write_msg[BUFFER_SIZE] = "I'm your father.";
		write(fd[WRITE_END], write_msg, strlen(write_msg) + 1);
		
		close(fd[WRITE_END]);
	} else { // child process
		close(fd[WRITE_END]);

		char read_msg[BUFFER_SIZE];
		read(fd[READ_END], read_msg, BUFFER_SIZE);
		printf("from parent proc to child: %s\n", read_msg);

		close(fd[READ_END]);
	}

	return 0;
}

