package com.github.slezadav.backgroundTasks;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class containing info about a task, that will run in future. Used with the TaskChain.
 *
 * Created by david.slezak on 10.6.2015.
 */
public class FutureTask {
    /**
     * Tag of the task
     */
    Object tag;
    /**
     * Params used with the task
     */
    ArrayList<Object> params = new ArrayList<>();
    /**
     * Task that will run in future
     */
    BaseTask task;
    /**
     * Flag if this task should use params passed from previous task
     */
    boolean useHistoryParam;

    /**
     * Constructor
     *
     * @param tag             tag of the future task
     * @param task            task that will run as a part of chain
     * @param useHistoryParam flag if the task will use previous task result
     * @param params          params used by this task
     */
    public FutureTask(Object tag, BaseTask task, boolean useHistoryParam, Object[] params) {
        this.tag = tag;
        this.useHistoryParam = useHistoryParam;
        this.task = task;
        Collections.addAll(this.params, params);
    }

    /**
     * Method to get the params that will be used with this task
     * @return params that will be used with this task
     */
    public Object[] getParams() {
        return params.toArray(new Object[params.size()]);
    }


}