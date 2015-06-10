package cz.slezadav.backgroundTasks;

import java.util.ArrayList;
import java.util.Collections;

/**
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
        Object[] tmp = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            tmp[i] = params.get(i);
        }
        return tmp;
    }


}