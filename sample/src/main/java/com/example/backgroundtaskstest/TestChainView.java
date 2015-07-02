package com.example.backgroundtaskstest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.IBgTaskCallbacks;
import com.github.slezadav.backgroundTasks.TaskChain;

/**
 * View used for testing
 * Created by david.slezak on 2.7.2015.
 */
public class TestChainView extends Button implements IBgTaskCallbacks {
    public TestChainView(Context context) {
        super(context);
        init();
    }

    public TestChainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestChainView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void onTaskSuccess(Object tag, Object result) {
        Log.i("TAG","onTaskSuccess "+tag+"   "+result);
    }

    @Override
    public void onTaskFail(Object tag, Exception exception) {
        Log.i("TAG","onTaskFail "+tag);
    }




    private void init(){
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BgTasks.isTaskInProgress(TestChainView.this,MainActivity.CHAINTAG)){
                    BgTasks.cancelTask(TestChainView.this,MainActivity.CHAINTAG);
                }else{
                    TaskChain chain=new TaskChain(MainActivity.CHAINTAG);
                    chain.addTask(new TestTask());
                    chain.addTask(new TestTask());
                    chain.addTask(new TestTask());
                    BgTasks.startTaskChain(TestChainView.this, chain);
                }
            }
        });
    }
}
