package test;

import src.CronScheduler;
import org.junit.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.Assert.*;

public class CronFunctionalTest {
    int nThreads = 10;
    private final CronScheduler cronScheduler = new CronScheduler(nThreads);

    @Test
    public void fillUpTheQueue() {
        for(int i=0; i<2*nThreads; i++){
            int finalI = i;
            UUID id = UUID.randomUUID();
            while (cronScheduler.getAcceptedJobsIds().contains(id)){
                id = UUID.randomUUID();
            }
            cronScheduler.addNewJob(Duration.ofSeconds(5), Duration.ofSeconds(3),
                    () -> System.out.println("Running Job #"+ finalI +" ..."), id);
        }
        assertEquals(cronScheduler.getAcceptedJobsIds().size(), cronScheduler.getAcceptedJobs().size());
    }

}