package com.github.slezadav.backgroundTasks;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class containing info about a task, that will run in future
 * Created by david.slezak on 10.6.2015.
 */
public class FutureTask {
    Object tag;
    ArrayList<Object> params = new ArrayList<>();
    BaseTask task;
    boolean useHistoryParam;

    public FutureTask(Object tag, BaseTask task, boolean useHistoryParam, Object[] params) {
        this.tag = tag;
        this.useHistoryParam = useHistoryParam;
        this.task = task;
        Collections.addAll(this.params, params);
    }

    public Object[] getParams() {
        return params.toArray(new Object[params.size()]);
    }


}