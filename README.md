# Cron Scheduler

An in-process scheduler that accepts a job and executes it periodically.

## Table of contents

1. [Solution Discription](#solution-discription)
2. [Technical Decisions](#technical-decisions)
3. [Trade-offs](#trade-offs)
4. [Example Usage](#example-usage)
5. [Possible Future Improvements](#possible-future-improvements)
6. [References](#references)

## Solution Discription

* A CronScheduler object is created with a number of threads, specified by the user, and a schedulerAgent thread responsible for monitoring the jobs in the queue, a priority blocking queue.
* The user adds a new job with expected single run expected interval, scheduling frequency, a job implementation and a unique ID.
* The scheduler makes sure the ID is unique and adds it to the queue. If it is not unique, the scheduler rejects the job.
* If the new added job has the closest next time execution, the scheduler wakes the agent to do its job.
* The agent's job is to always monitor the queue and check if the closest next time execution is now or not.
  * If it is, execute the job using serviceExecutor object for asynchronous execution then update its next execution time and add it back to the queue.
  * If it is not, add it back to the queue and sleep until the closest next time of execution.

## Technical Decisions

* Using UUID, a class that represents an immutable universally unique identifier, to represent each job id makes generating random ids easier, hence makes testing and creating jobs easier.
* Using Duration class, a time based amount of time, to represent both "singleRunExpectedInterval" and "schedulingFrequency".
* Using ExecutorService interface that allows us to execute tasks on threads asynchronously in addition to providing the facility to queue up tasks until there is a free thread available if the number of tasks is more than the threads available.
* Using java.util.loggin.Logger along with LogFormater class, extends Formatter, to write scheduler and agent logs to console and save it into two different files.

## Trade-offs

* Using Java to implement the scheduler is not the best decision to make when performance is a priority. But it is simpler to use comparing to implementation using C or C++ and put yourself in the danger of memory leak or security issues.

## Example Usage

* Creating a simple job that prints "Hello, World!" with expected run time interval = 3 seconds and scheduling frequency = 15.
```java
import java.util.UUID;
import src.CronScheduler;
import java.time.Duration;
```
```java
public class ExampleUsage {
    public static void main(String[] args) {
      int nThreads = 10;
      CronScheduler cronScheduler = new CronScheduler(nThreads);
      UUID id = UUID.randomUUID();
      cronScheduler.addNewJob(Duration.ofSeconds(3), Duration.ofSeconds(15),
              () -> System.out.println("Hello, World!"), id);
    }
}
```

* Logger output:
```text
[2022-01-03 20:54:14] [src.CronAgent] [INFO] - Cron Agent woke up successfully.
[2022-01-03 20:54:14] [src.CronScheduler] [INFO] - Cron Scheduler has been started with 10 threads available to run jobs in parallel
[2022-01-03 20:54:14] [src.CronScheduler] [INFO] - Successfully added a new job to the queue. Job ID: fdb344b2-6518-481a-88e0-e32995ec6ea3
[2022-01-03 20:54:14] [src.CronScheduler] [INFO] - The newly added job has the highest priority. 
[2022-01-03 20:54:14] [src.CronAgent] [INFO] - Getting the highly priority job..
```

## Possible Future Improvements

* Using a relational database to store the jobs.
* Saving jobs to be able to load them again without the need to reconfigure them one more time.
* Making the scheduler can start with defualt jobs, could be done using the load method mentioned before.
* Adding the ability to remove a job, by the client, from the queue.
* Adding the ability to kill a job, by the scheduler itself, if it uses much resources.

## References

* https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html
* https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html
* https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ExecutorService.html