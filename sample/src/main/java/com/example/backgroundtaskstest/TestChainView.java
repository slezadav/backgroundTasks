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
 * View used for testing Created by david.slezak on 2.7.2015.
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
    public void onTaskReady(BaseTask task) {

        Log.i("TAG", "onTaskReady " + task.getTag() + " " + task.getTaskNumber());


    }

    @Override
    public void onTaskProgressUpdate(BaseTask task, Object... progress) {

        Log.i("TAG", "onTaskProgress " + task.getTag()+" "+task.getTaskNumber() + "   " +
                     progress[0]);

    }

    @Override
    public void onTaskCancelled(BaseTask task, Object result) {

        Log.i("TAG", "onTaskCancel " + task.getTag()+" "+task.getTaskNumber() + "   " + result);

    }

    @Override
    public void onTaskSuccess(BaseTask task, Object result) {

        Log.i("TAG", "onTaskSuccess " + task.getTag()+" "+task.getTaskNumber() + "   " + result);

    }

    @Override
    public void onTaskFail(BaseTask task, Exception exception) {

        Log.i("TAG", "onTaskFail " + task.getTag()+" "+task.getTaskNumber());

    }
    private void init() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BgTasks.isTaskInProgress(TestChainView.this, MainActivity.CHAINTAG)) {
                    BgTasks.cancelTask(TestChainView.this, MainActivity.CHAINTAG);
                } else {
                    BgTasks.startTask(TestChainView.this,
                            new TestChain().addTask(new TestTask()).addTask(new TestTask()).addTag(
                                    MainActivity.CHAINTAG));
                }
            }
        });
    }
}
