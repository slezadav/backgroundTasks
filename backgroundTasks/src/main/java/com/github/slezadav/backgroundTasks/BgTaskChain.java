package com.github.slezadav.backgroundTasks;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by david.slezak on 30.7.2015.
 */
public class BgTaskChain {

    private ArrayList<BaseTask> tasks;
    private String mTag;

    public BgTaskChain(){
        tasks=new ArrayList<>();
        this.mTag= UUID.randomUUID().toString();
    }

    public final BgTaskChain addTask(BaseTask task){
        task.setChainTag(mTag);
        if(!tasks.isEmpty()){
            tasks.get(tasks.size()-1).addFollowingTask(task);
        }
        tasks.add(task);
        return this;
    }

    public final BgTaskChain addTag(String tag){
        this.mTag=tag;
        for (BaseTask task:tasks){
            task.setChainTag(mTag);
        }
        return this;
    }

    public String getTag() {
        return mTag;
    }

    final void run(IBgTaskSimpleCallbacks clb){
        for (BaseTask task:tasks){
            task.setChainTag(getTag());
        }
        if(!tasks.isEmpty()){
            BgTasks.startFollowingTask(clb,tasks.get(0),null);
        }
    }

}
