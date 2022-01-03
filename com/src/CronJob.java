package src;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class CronJob implements Runnable {
    private final Duration singleRunExpectedInterval;
    private final Duration schedulingFrequency;
    private Instant nextExecTime;
    private final Runnable function;
    private final UUID jobId;

    public CronJob(Duration singleRunExpectedInterval, Duration schedulingFrequency, Runnable function, UUID jobId) {
        this.singleRunExpectedInterval = singleRunExpectedInterval;
        this.schedulingFrequency = schedulingFrequency;
        this.function = function;
        this.jobId = jobId;
        updateNextExecTime(Instant.now());
    }

    @Override
    public void run() {
        function.run();
        updateNextExecTime(Instant.now());
    }

    public void updateNextExecTime(Instant currentTime) {
        this.nextExecTime = currentTime.plus(schedulingFrequency);
    }

    public Instant getNextExecTime() {
        return nextExecTime;
    }

    public Duration getSingleRunExpectedInterval() {
        return singleRunExpectedInterval;
    }

    public Duration getSchedulingFrequency() {
        return schedulingFrequency;
    }

    public UUID getJobId() {
        return jobId;
    }

    public Runnable getFunction() {
        return function;
    }
}
