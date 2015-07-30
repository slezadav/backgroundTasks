package com.github.slezadav.backgroundTasks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by david.slezak on 30.7.2015.
 */
public class BgTaskChain<V extends View & IBgTaskSimpleCallbacks, F extends Fragment & IBgTaskSimpleCallbacks, A
        extends FragmentActivity & IBgTaskSimpleCallbacks>{

    private ArrayList<BaseTask> tasks;
    private IBgTaskSimpleCallbacks clb;

    public BgTaskChain(V clb){
        this.clb=clb;
        tasks=new ArrayList<>();
    }
    public BgTaskChain(F clb){
        this.clb=clb;
        tasks=new ArrayList<>();
    }
    public BgTaskChain(A clb){
        this.clb=clb;
        tasks=new ArrayList<>();
    }
    public BgTaskChain addTask(Object tag,BaseTask task){
        task.setTag(tag);
        if(!tasks.isEmpty()){
            tasks.get(tasks.size()-1).addFollowingTask(tag,task);
        }
        tasks.add(task);
        return this;
    }

    public void run(){
        if(!tasks.isEmpty()){
            BgTasks.startFollowingTask(clb,tasks.get(0).getTag(),tasks.get(0),null);
        }
    }

}
