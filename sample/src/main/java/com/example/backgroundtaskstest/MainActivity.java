package com.example.backgroundtaskstest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.slezadav.backgroundTasks.BackgroundTasks;
import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BaseTask.ExecutionType;
import com.github.slezadav.backgroundTasks.TaskChain;


public class MainActivity extends FragmentActivity implements BaseTask.IBaseTaskCallbacks {

    private static final String TASKTAG="task";
    private static final String CHAINTAG="chain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button task= (Button) findViewById(R.id.button_task);
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTasks.startTask(MainActivity.this, TASKTAG, new TestTask());
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
                BackgroundTasks.startTaskChain(MainActivity.this, chain);
            }
        });
        Button cchain= (Button) findViewById(R.id.button_cancel_chain);
        cchain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTasks.cancelTask(MainActivity.this, CHAINTAG);
            }
        });
        Button ctask= (Button) findViewById(R.id.button_cancel_task);
        ctask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTasks.cancelTask(MainActivity.this, TASKTAG);
            }
        });

        Button tasks= (Button) findViewById(R.id.button_task_service);
        tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTasks.startTask(MainActivity.this,TASKTAG,new TestTask(), BaseTask.ExecutionType.SERVICE_LOCAL);
            }
        });
        Button tasksr= (Button) findViewById(R.id.button_task_service_remote);
        tasksr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTasks.startTask(MainActivity.this,TASKTAG,new TestTask(), ExecutionType.SERVICE_REMOTE);
            }
        });
        Button chains= (Button) findViewById(R.id.button_chain_service);
        chains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskChain chain=new TaskChain(CHAINTAG);
                chain.addTask(new TestTask());
                chain.addTask(new TestTask());
                chain.addTask(new TestTask());
                BackgroundTasks.startTaskChain(MainActivity.this,chain, BaseTask.ExecutionType.SERVICE_LOCAL);
            }
        });
        Button chainsr= (Button) findViewById(R.id.button_chain_service_remote);
        chainsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskChain chain=new TaskChain(CHAINTAG);
                chain.addTask(new TestTask());
                chain.addTask(new TestTask());
                chain.addTask(new TestTask());
                BackgroundTasks.startTaskChain(MainActivity.this,chain, ExecutionType.SERVICE_REMOTE);
            }
        });
    }


    @Override
    public void onTaskReady(Object tag) {

        Log.i("TAG","onTaskReady "+tag);
    }

    @Override
    public void onTaskProgressUpdate(Object tag, Object... progress) {
       // Object[] pr= (Object[]) progress[0];
        Log.i("TAG","onTaskProgress "+tag+"   "+progress[0]);
    }

    @Override
    public void onTaskCancelled(Object tag) {
        Log.i("TAG","onTaskCancel "+tag);
    }

    @Override
    public void onTaskSuccess(Object tag, Object result) {
        Log.i("TAG","onTaskSuccess "+tag+"   "+result);
    }

    @Override
    public void onTaskFail(Object tag, Exception exception) {
        Log.i("TAG","onTaskFail "+tag);
    }
}
