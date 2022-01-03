package src;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.*;

public class CronScheduler {
    /**
    * Src.CronScheduler class is responsible for accepting a job and executes it periodically.
    * */
    private final PriorityBlockingQueue<CronJob> acceptedJobs;
    private final Set<UUID> acceptedJobsIds;
    private final Thread cronSchedulerAgent;
    private final Logger logger = Logger.getLogger(CronScheduler.class.getName());
    private static boolean isLoggerInit = false;

    /**
     * scheduler constructor
     *
     * @param nThreads  number of available threads
     */
    public CronScheduler(int nThreads) {
        isLoggerInit = LogFormatter.loggerInit(logger, isLoggerInit, "logs/cronSchedulerLogs.log");
        this.acceptedJobs = new PriorityBlockingQueue<>(nThreads,
                (o1, o2) -> (int) (o1.getNextExecTime().getEpochSecond()-o2.getNextExecTime().getEpochSecond()));
        this.acceptedJobsIds = new HashSet<>();
        this.cronSchedulerAgent = new Thread(new CronAgent(acceptedJobs, nThreads));
        cronSchedulerAgent.start();
        logger.log(new LogRecord(Level.INFO,"Cron Scheduler has been started with "+nThreads+
                " threads available to run jobs in parallel"));
    }


    /**
     * creates a new job and adds it to the priority queue
     *
     * @param singleRunExpectedInterval A single run expected interval, e.g. `30m`
     * @param schedulingFrequency   Scheduling frequency, e.g. `1hr` for a job that should run every one hour
     * @param function  The job implementation, e.g. a function
     * @param jobId  A unique job identifier
     * @throws RuntimeException if the unique identifier already exists
     */
    public void addNewJob(Duration singleRunExpectedInterval, Duration schedulingFrequency, Runnable function,
                          UUID jobId)throws RuntimeException{
        if(acceptedJobsIds.contains(jobId)){
            logger.log(new LogRecord(Level.WARNING,"Failed to add a new job to the queue.\nJob ID: "+ jobId+
                    " already exists."));
            throw new RuntimeException("This id already exists.");
        }
        CronJob cronJob = new CronJob(singleRunExpectedInterval, schedulingFrequency, function, jobId);
        this.acceptedJobs.add(cronJob);
        this.acceptedJobsIds.add(jobId);
        logger.log(new LogRecord(Level.INFO,"Successfully added a new job to the queue. Job ID: "+ jobId));
        if (acceptedJobs.peek() == cronJob) {
            logger.log(new LogRecord(Level.INFO,"The newly added job has the highest priority. "));
            cronSchedulerAgent.interrupt();
        }
    }
    public PriorityBlockingQueue<CronJob> getAcceptedJobs(){
        return acceptedJobs;
    }

    public Set<UUID> getAcceptedJobsIds() {
        return acceptedJobsIds;
    }

}
