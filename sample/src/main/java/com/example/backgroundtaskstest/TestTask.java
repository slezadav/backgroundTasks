package com.example.backgroundtaskstest;

import com.github.slezadav.backgroundTasks.BaseTask;

/**
 * Created by Davo on 12.6.2015.
 */
public class TestTask extends BaseTask {
    @Override
    protected Object doInBackground(Object... params) {
        try {
            Thread.sleep(2000);
            publishProgress(true);
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


}
