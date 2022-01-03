package test;

import src.CronScheduler;
import org.junit.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.Assert.*;

public class CronSchedulerTest {
    int nThreads = 10;
    private final CronScheduler cronScheduler = new CronScheduler(nThreads);

    @Test
    public void addNewJob() {
        UUID id = UUID.randomUUID();
        cronScheduler.addNewJob(Duration.ofSeconds(3), Duration.ofSeconds(15),
                () -> System.out.println("Running Job..."), id);
        assertTrue(cronScheduler.getAcceptedJobsIds().contains(id));
        assertEquals(1, cronScheduler.getAcceptedJobs().size());
        assertEquals(1, cronScheduler.getAcceptedJobsIds().size());
    }

    @Test
    public void addTwoDifferentJobsWithTheSameFrequency() {
        UUID id = UUID.randomUUID();
        cronScheduler.addNewJob(Duration.ofSeconds(3), Duration.ofSeconds(15),
                () -> System.out.println("Running Job..."), id);
        assertTrue(cronScheduler.getAcceptedJobsIds().contains(id));
        assertEquals(1, cronScheduler.getAcceptedJobs().size());

        id = UUID.randomUUID();
        cronScheduler.addNewJob(Duration.ofSeconds(3), Duration.ofSeconds(15),
                () -> System.out.println("Running Job..."), id);

        assertEquals(2, cronScheduler.getAcceptedJobs().size());
        assertEquals(2, cronScheduler.getAcceptedJobsIds().size());
    }

    @Test
    public void addJobWithExistingId() {
        UUID id = UUID.randomUUID();
        cronScheduler.addNewJob(Duration.ofSeconds(3), Duration.ofSeconds(15),
                () -> System.out.println("Running Job..."), id);
        assertTrue(cronScheduler.getAcceptedJobsIds().contains(id));
        assertEquals(1, cronScheduler.getAcceptedJobs().size());

        try {
            cronScheduler.addNewJob(Duration.ofSeconds(5), Duration.ofSeconds(20),
                    () -> System.out.println("Running Job..."), id);
            fail("A new job has been added with an existing id.");
        } catch (RuntimeException runtimeException) {
            System.out.println(runtimeException.getMessage());
        }
        assertEquals(1, cronScheduler.getAcceptedJobs().size());
        assertEquals(1, cronScheduler.getAcceptedJobsIds().size());
    }

    @Test
    public void addNewJobsWithSmallFrequencyAndHighRunInterval() {
        UUID id = UUID.randomUUID();
        cronScheduler.addNewJob(Duration.ofSeconds(15), Duration.ofSeconds(3),
                () -> System.out.println("Running Job..."), id);
        assertTrue(cronScheduler.getAcceptedJobsIds().contains(id));
        assertEquals(1, cronScheduler.getAcceptedJobs().size());
    }
}