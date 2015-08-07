package com.example.backgroundtaskstest;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

/**
 * Test cases for sample application
 * Created by Davo on 14.6.2015.
 */
public class BackgroundTasksTest
        extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public BackgroundTasksTest() {
        super(MainActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        solo.sleep(5000);
    }

    public void testCase(){

        singleAsync();
        singleAsyncCancel();
        chainAsync();
        chainAsyncCancel();
        singleAsyncFragment();
        chainAsyncFragment();
        singleAsyncFragmentCancel();
        chainAsyncFragmentCancel();
        singleAsyncView();
        singleAsyncViewFragment();
        singleAsyncViewCancel();
        singleAsyncViewFragmentCancel();
        chainAsyncView();
        chainAsyncViewCancel();
        chainAsyncViewFragment();
        chainAsyncFragmentViewCancel();
    }


    public void singleAsync() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class, 5000);
        solo.clickOnButton("Task");
        assertTrue("Task not started", solo.waitForLogMessage("onTaskReady task 0", 1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress", solo.waitForLogMessage("onTaskProgress task 0", 3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task 0",5000));

    }

    public void chainAsync() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 0", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 0", 3000));
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 0", 3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 1", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 1", 3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 1", 3000));
    }

    public void singleAsyncCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task");
        assertTrue("Task not started", solo.waitForLogMessage("onTaskReady task 0", 1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }

    public void chainAsyncCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }



    public void singleAsyncFragment() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class, 5000);
        solo.clickOnButton("Frg Task");
        assertTrue("Task not started", solo.waitForLogMessage("onTaskReady task 0", 1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress", solo.waitForLogMessage("onTaskProgress task 0", 3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task 0",5000));
    }

    public void chainAsyncFragment() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsyncFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg Chain");
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 0", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 0", 3000));
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 0", 3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 1", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 1", 3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 1", 3000));
    }

    public void singleAsyncFragmentCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task 0",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }

    public void chainAsyncFragmentCancel() {
        Log.d("BackgroundTasksTest", "chainAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg Chain");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }

    public void singleAsyncView() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncView");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task 0",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task 0",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task 0",5000));
    }

    public void singleAsyncViewFragment() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncViewFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Frg task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task 0",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task 0",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task 0",5000));
    }

    public void singleAsyncViewCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncViewCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task view");
        assertTrue("Task not started", solo.waitForLogMessage("onTaskReady task 0", 1000));
        solo.clickOnButton("Task view");
        //solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }

    public void singleAsyncViewFragmentCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "singleAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task 0",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg task view");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }


    public void chainAsyncViewFragment() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsyncView");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg chain view");
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 0", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 0", 3000));
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 0", 3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 1", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 1", 3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 1", 3000));
    }

    public void chainAsyncFragmentViewCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg chain view");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg chain view");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }

    public void chainAsyncView() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain view");
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 0", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 0", 3000));
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 0", 3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not ready", solo.waitForLogMessage("onTaskReady task 1", 3000));
        assertTrue("Chain did not publish progress", solo.waitForLogMessage("onTaskProgress task 1", 3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue", solo.waitForLogMessage("onTaskSuccess task 1", 3000));
    }

    public void chainAsyncViewCancel() {
        solo.clearLog();
        Log.d("BackgroundTasksTest", "chainAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain view");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Chain view");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel task 0",3000));
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}