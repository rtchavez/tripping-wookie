package com.pearson.lt.mobileplatform.android.core.tasks;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chavry on 9/3/13.
 */
public class TaskQueue extends Thread {

    /**
     * The queue of tasks to service.
     */
    private final BlockingQueue<Task<?>> queue;
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor responsePoster;
    /**
     * Used for telling us to die.
     */
    private volatile boolean quit = false;
    /**
     * Used for generating monotonically-increasing sequence numbers for tasks.
     */
    private AtomicInteger sequenceGenerator = new AtomicInteger();
    /**
     * Used for telling us to quit after a single task is run.
     */
    private final boolean isSingleTask;

    public TaskQueue() {
        this(false);
    }

    public TaskQueue(final boolean isSingleTask) {
        this.isSingleTask = isSingleTask;
        this.queue = new PriorityBlockingQueue<Task<?>>();

        final Handler handler = new Handler(Looper.getMainLooper());
        this.responsePoster = new Executor() {

            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Forces this dispatcher to finish immediately.  If any tasks are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void finish() {
        quit = true;
        interrupt();
    }

    /**
     * Adds a Task to the queue.
     *
     * @param task The task to service
     * @return The passed-in task
     */
    public TaskQueue addTask(Task task) {
        synchronized (queue) {
            // Process tasks in the order they are added.
            task.setSequence(getSequenceNumber());
            queue.add(task);
        }

        return this;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Task<?> task;

        while (true) {
            try {
                // Take a task from the queue.
                task = queue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to finish.
                if (quit) {
                    return;
                }
                continue;
            }

            try {
                // Perform the task.
                task.startTask();
                postResponse(task);
            } catch (Exception exception) {
                postError(task, exception);
            }

            if (isSingleTask()) {
                finish();
            }
        }
    }

    private void postResponse(final Task task) {
        responsePoster.execute(new Runnable() {

            @Override
            public void run() {
                task.postResponse();
            }
        });
    }

    private void postError(final Task task, final Exception exception) {
        responsePoster.execute(new Runnable() {

            @Override
            public void run() {
                task.postError(exception);
            }
        });
    }

    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return sequenceGenerator.incrementAndGet();
    }

    public boolean isSingleTask() {
        return isSingleTask;
    }

}
