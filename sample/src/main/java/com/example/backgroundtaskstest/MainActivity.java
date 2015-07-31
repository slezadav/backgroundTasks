package com.example.backgroundtaskstest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BgTaskChain;
import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.IBgTaskCallbacks;


public class MainActivity extends FragmentActivity implements IBgTaskCallbacks {

    public static final String TASKTAG = "task";
    public static final String CHAINTAG = "chain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button task = (Button) findViewById(R.id.button_task);
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.startTask(MainActivity.this, TASKTAG,new TestTask());
            }
        });
        Button chain = (Button) findViewById(R.id.button_chain);
        chain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BgTaskChain(MainActivity.this).addTask(CHAINTAG, new TestTask()).addTask(CHAINTAG,
                        new TestTask()).run();
            }
        });
        Button cchain = (Button) findViewById(R.id.button_cancel_chain);
        cchain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(MainActivity.this, CHAINTAG);
            }
        });
        Button ctask = (Button) findViewById(R.id.button_cancel_task);
        ctask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(MainActivity.this, TASKTAG);
            }
        });
    }


    @Override
    public void onTaskReady(BaseTask task) {
        Log.i("TAG", "onTaskReady " + task.getTag());
    }

    @Override
    public void onTaskProgressUpdate(BaseTask task, Object... progress) {
        Log.i("TAG", "onTaskProgress " + task.getTag() + "   " +
                     progress[0]);
    }

    @Override
    public void onTaskCancelled(BaseTask task, Object result) {
        Log.i("TAG", "onTaskCancel " + task.getTag() + "   " + result);
    }

    @Override
    public void onTaskSuccess(BaseTask task, Object result) {
        Log.i("TAG", "onTaskSuccess " + task.getTag() + "   " + result);
    }

    @Override
    public void onTaskFail(BaseTask task, Exception exception) {
        Log.i("TAG", "onTaskFail " + task.getTag());
    }
}
