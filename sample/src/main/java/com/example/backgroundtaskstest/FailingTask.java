package com.example.backgroundtaskstest;

import com.github.slezadav.backgroundTasks.BaseTask;

/**
 * Created by david.slezak on 2.7.2015.
 */
public class FailingTask extends BaseTask {
    @Override
    protected Object doInBackground(Object... params) {
        return new Exception("Test Exception");
    }
}
