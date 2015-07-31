package com.github.slezadav.backgroundTasks;

/**
 * Interface used to define the callbacks
 * Created by david.slezak on 19.6.2015.
 */
public interface IBgTaskCallbacks extends IBgTaskSimpleCallbacks{
    /**
     * Triggered when the task is ready to execute
     *
     * @param task task
     */
    void onTaskReady(BaseTask task);

    /**
     * Triggered when the task publishes progress. In case of TaskChain, this is triggered, when partial tasks
     * are completed with result as progress.
     *
     * @param task      task
     * @param progress published progress
     */
    void onTaskProgressUpdate(BaseTask task, Object... progress);

    /**
     * Triggered when the task or chain has been cancelled
     * @param task task tag
     * @param result result
     */
    void onTaskCancelled(BaseTask task,Object result);

}
