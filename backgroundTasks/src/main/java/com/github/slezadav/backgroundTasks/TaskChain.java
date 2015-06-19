package com.github.slezadav.backgroundTasks;

import java.util.ArrayList;

/**
 * Class containing info about a chain of tasks
 *
 * Created by david.slezak on 9.6.2015.
 */
public class TaskChain {
    /**
     * List of tasks included in this chain
     */
    ArrayList<BaseTask> tasks = new ArrayList<>();
    /**
     * List of params associated with the tasks in this chain
     */
    ArrayList<Object[]> params = new ArrayList<>();
    /**
     * List of flags used with the tasks in this chain
     */
    ArrayList<Boolean> useHistoryParam = new ArrayList<>();
    /**
     * Tag of the final task in this chain
     */
    Object finalTag;

    /**
     * Constructor
     *
     * @param tag tag used for this chain
     */
    public TaskChain(Object tag) {
        this.finalTag = tag;
    }

    /**
     * Adds the task to the chain
     *
     * @param task              task to be added
     * @param usePreviousResult flag if this task should use previous task's result
     * @param params            params of the task
     * @return chain instance
     */
    public TaskChain addTask(BaseTask task, boolean usePreviousResult, Object... params) {
        this.tasks.add(task);
        this.params.add(params);
        this.useHistoryParam.add(usePreviousResult);
        return this;
    }

    /**
     * Adds the task to the chain
     *
     * @param task   task to be added
     * @param params params of the task
     * @return chain instance
     */
    public TaskChain addTask(BaseTask task, Object... params) {
        this.tasks.add(task);
        this.params.add(params);
        this.useHistoryParam.add(false);
        return this;
    }
}