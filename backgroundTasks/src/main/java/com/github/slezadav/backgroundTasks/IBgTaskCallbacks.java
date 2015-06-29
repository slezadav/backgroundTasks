package com.github.slezadav.backgroundTasks;

/**
 * Interface used to define the callbacks
 * Created by david.slezak on 19.6.2015.
 */
public interface IBgTaskCallbacks extends IBgTaskSimpleCallbacks{
    /**
     * Triggered when the task is ready to execute
     *
     * @param tag task's tag
     */
    void onTaskReady(Object tag);

    /**
     * Triggered when the task publishes progress. In case of TaskChain, this is triggered, when partial tasks
     * are completed with result as progress.
     *
     * @param tag      task or chain tag
     * @param progress published progress
     */
    void onTaskProgressUpdate(Object tag, Object... progress);

    /**
     * Triggered when the task or chain has been cancelled
     * @param tag task or chain tag
     * @param result result
     */
    void onTaskCancelled(Object tag,Object result);

}
