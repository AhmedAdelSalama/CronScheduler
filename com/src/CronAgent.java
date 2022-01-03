package src;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class CronAgent implements Runnable{
    private final Logger logger = Logger.getLogger(CronAgent.class.getName());
    private static boolean isLoggerInit = false;
    private final PriorityBlockingQueue<CronJob> acceptedJobs;
    /**
     * The Java ExecutorService is the interface which allows us to execute tasks on threads asynchronously.
     * It helps in maintaining a pool of threads and assigns them tasks.
     * It also provides the facility to queue up tasks until there is a free thread available if the number of tasks
     * is more than the threads available.
     */
    private final ExecutorService executorService;

    /**
     * scheduler constructor
     *
     * @param acceptedJobs  priority queue with successfully added jobs
     * @param nThreads  number of available threads
     */
    public CronAgent(PriorityBlockingQueue<CronJob> acceptedJobs, int nThreads) {
        isLoggerInit = LogFormatter.loggerInit(logger, isLoggerInit, "logs/cronAgentLogs.log");
        this.acceptedJobs = acceptedJobs;
        this.executorService = Executors.newFixedThreadPool(nThreads);
        logger.log(new LogRecord(Level.INFO,"Cron Agent woke up successfully."));
    }

    /**
     *  keep watching the priority queue with the sleeping added jobs, if the peek job's next execution time is now,
     *  run the job in a new thread. Else sleep until its next execution time.
     */
    @Override
    public void run(){
        while(true){
            try {
                if(acceptedJobs.size()>0) {
                    CronJob cronJob = acceptedJobs.peek();
                    if (cronJob.getNextExecTime().isAfter(Instant.now())) {
                        Thread.sleep(cronJob.getNextExecTime().getEpochSecond() - Instant.now().getEpochSecond());
                    } else {
                        logger.log(new LogRecord(Level.INFO, "Executing job with ID: .." + cronJob.getJobId()));
                        executorService.execute(Objects.requireNonNull(acceptedJobs.poll()));
                        cronJob.updateNextExecTime(Instant.now());
                        acceptedJobs.add(cronJob);
                    }
                }
            }catch (InterruptedException exception){
                logger.log(new LogRecord(Level.INFO,"Getting the highly priority job.."));
            }
        }
    }
}
