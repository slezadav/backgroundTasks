package com.github.slezadav.backgroundTasks.executor;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * Created by david.slezak on 30.7.2015.
 */
public class SerialExecutor {
    public static Executor exec;

    public static Executor getExecutorInstance(){
        if(exec==null){
            exec= new SerialExecutorImpl();
        }
        return exec;
    }

    private static class SerialExecutorImpl implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;
        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                ParallelExecutor.getExecutorInstance().execute(mActive);
            }
        }
    }
}
