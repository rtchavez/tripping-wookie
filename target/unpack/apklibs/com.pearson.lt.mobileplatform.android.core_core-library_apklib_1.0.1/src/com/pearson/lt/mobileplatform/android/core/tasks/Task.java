package com.pearson.lt.mobileplatform.android.core.tasks;

public abstract class Task<T> implements Comparable<Task<?>> {

    /**
     * Sequence number of this request, used to enforce FIFO ordering.
     */
    private Integer sequence;
    private TaskCallback<T> callback;
    private T response;

    public Task(TaskCallback<T> callback) {
        this.callback = callback;
    }

    void startTask() throws Exception {
        this.response = performTask();
    }

    public T getResponse() {
        return response;
    }

    public abstract T performTask() throws Exception;

    public TaskPriority getTaskPriority() {
        return TaskPriority.NORMAL;
    }

    /**
     * Returns the sequence number of this request.
     */
    public final int getSequence() {
        if (sequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return sequence;
    }

    /**
     * Sets the sequence number of this request.  Used by {@link TaskQueue}.
     */
    public final void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * Our comparator sorts from high to low priority, and secondarily by
     * sequence number to provide FIFO ordering.
     */
    @Override
    public int compareTo(Task<?> other) {
        TaskPriority left = this.getTaskPriority();
        TaskPriority right = other.getTaskPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.sequence - other.sequence :
                right.ordinal() - left.ordinal();
    }

    void postResponse() {
        callback.onResponse(getResponse());
    }

    void postError(Exception exception) {
        callback.onException(exception);
    }

}
