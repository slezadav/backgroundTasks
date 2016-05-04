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
            startTaskInSameContext(new TestTask2(1));
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getTag() {
        return "task";
    }

    @Override
    protected void doOnPostExecute(Object result) {
        super.doOnPostExecute(result);
        startTaskInSameContext(new TestTask2(2));
    }
}
