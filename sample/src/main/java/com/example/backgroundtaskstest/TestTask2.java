package com.example.backgroundtaskstest;

import com.github.slezadav.backgroundTasks.BaseTask;

/**
 * Created by Davo on 12.6.2015.
 */
public class TestTask2 extends BaseTask {
    @Override
    protected Object doInBackground(Object... params) {
        try {
            Thread.sleep(1000);
            publishProgress(true);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


}
