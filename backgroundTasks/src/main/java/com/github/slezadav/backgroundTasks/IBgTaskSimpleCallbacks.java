package com.github.slezadav.backgroundTasks;

/**
 * Basic callbacks for background tasks
 * Created by david.slezak on 29.6.2015.
 */
public interface IBgTaskSimpleCallbacks  {
    /**
     * Triggered when the task or chain finishes successfully
     * @param tag task or chain tag
     * @param result result returned from task
     */
    void onTaskSuccess(Object tag, Object result);

    /**
     * Triggered when the task or chain failed
     * @param tag task or chain tag
     * @param exception failing exception
     */
    void onTaskFail(Object tag, Exception exception);
}
