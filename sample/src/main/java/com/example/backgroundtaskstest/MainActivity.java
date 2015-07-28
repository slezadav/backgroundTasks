package com.example.backgroundtaskstest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.IBgTaskCallbacks;
import com.github.slezadav.backgroundTasks.TaskChain;


public class MainActivity extends FragmentActivity implements IBgTaskCallbacks {

    public static final String TASKTAG="task";
    public static final String CHAINTAG="chain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button task= (Button) findViewById(R.id.button_task);
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.startTask(MainActivity.this, AsyncTask.SERIAL_EXECUTOR, TASKTAG, new TestTask());
            }
        });
        Button chain= (Button) findViewById(R.id.button_chain);
        chain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskChain chain=new TaskChain(CHAINTAG);
                chain.addTask(new TestTask());
                chain.addTask(new TestTask());
                chain.addTask(new TestTask());
                BgTasks.startTaskChain(MainActivity.this, chain);
            }
        });
        Button cchain= (Button) findViewById(R.id.button_cancel_chain);
        cchain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(MainActivity.this, CHAINTAG);
            }
        });
        Button ctask= (Button) findViewById(R.id.button_cancel_task);
        ctask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(MainActivity.this, TASKTAG);
            }
        });



    }


    @Override
    public void onTaskReady(Object tag) {
        Log.i("TAG","onTaskReady "+tag);
    }

    @Override
    public void onTaskProgressUpdate(Object tag, Object... progress) {
        Log.i("TAG","onTaskProgress "+tag+"   "+progress[0]);
    }
    @Override
    public void onTaskCancelled(Object tag,Object result) {
        Log.i("TAG","onTaskCancel "+tag+"   "+result);
    }

    @Override
    public void onTaskSuccess(BaseTask task, Object result) {
        Log.i("TAG","onTaskSuccess "+task.getTag()+"   "+result);
    }

    @Override
    public void onTaskFail(BaseTask task, Exception exception) {
        Log.i("TAG","onTaskFail "+task.getTag());
    }
}
