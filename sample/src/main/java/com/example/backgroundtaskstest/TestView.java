package com.example.backgroundtaskstest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.IBgTaskCallbacks;

/**
 * View used for testing
 * Created by david.slezak on 2.7.2015.
 */
public class TestView extends Button implements IBgTaskCallbacks {
    public TestView(Context context) {
        super(context);
        init();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onTaskReady(Object tag) {
        Log.i("TAG", "onTaskReady " + tag);
    }

    @Override
    public void onTaskProgressUpdate(Object tag, Object... progress) {
        Log.i("TAG","onTaskProgress "+tag+"   "+progress[0]);
    }

    @Override
    public void onTaskCancelled(Object tag, Object result) {
        Log.i("TAG","onTaskCancel "+tag+"    "+result);
    }

    @Override
    public void onTaskSuccess(BaseTask task, Object result) {
        Log.i("TAG","onTaskSuccess "+task.getTag()+"   "+result);
    }

    @Override
    public void onTaskFail(BaseTask task, Exception exception) {
        Log.i("TAG","onTaskFail "+task.getTag());
    }




    private void init(){
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BgTasks.isTaskInProgress(TestView.this,MainActivity.TASKTAG)){
                    BgTasks.cancelTask(TestView.this,MainActivity.TASKTAG);
                }else{
                    BgTasks.startTask(TestView.this,MainActivity.TASKTAG,new TestTask());
                }
            }
        });
    }
}
