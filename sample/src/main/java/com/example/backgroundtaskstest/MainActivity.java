package com.example.backgroundtaskstest;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.slezadav.backgroundTasks.BackgroundTasks;
import com.github.slezadav.backgroundTasks.BaseTask;
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
    }


    @Override
    public void onTaskReady(Object tag) {

        Log.i("TAG","onTaskReady "+tag);
    }

    @Override
    public void onTaskProgressUpdate(Object tag, Object progress) {
        Log.i("TAG","onTaskProgress "+tag);
    }

    @Override
    public void onTaskCancelled(Object tag) {
        Log.i("TAG","onTaskCancel "+tag);
    }

    @Override
    public void onTaskSuccess(Object tag, Object result) {
        Log.i("TAG","onTaskSuccess "+tag);
    }

    @Override
    public void onTaskFail(Object tag, Exception exception) {
        Log.i("TAG","onTaskFail "+tag);
    }
}
