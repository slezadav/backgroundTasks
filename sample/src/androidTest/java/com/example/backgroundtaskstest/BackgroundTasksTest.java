package com.example.backgroundtaskstest;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

/**
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
    }

    public void testCase(){
        singleAsync();
        singleAsyncCancel();
        singleAsyncService();
        singleAsyncCancelService();
        singleAsyncServiceRemote();
        singleAsyncCancelServiceRemote();
        chainAsync();
        chainAsyncCancel();
        chainAsyncService();
        chainAsyncCancelService();
        chainAsyncServiceRemote();
        chainAsyncCancelServiceRemote();
    }


    public void singleAsync() {
        Log.d("BackgroundTasksTest", "singleAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task",5000));
    }

    public void chainAsync() {
        Log.d("BackgroundTasksTest", "chainAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("Continuing chain",3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("Continuing chain",3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess chain",10000));
    }

    public void singleAsyncCancel() {
        Log.d("BackgroundTasksTest", "singleAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task",3000));
    }

    public void chainAsyncCancel() {
        Log.d("BackgroundTasksTest", "chainAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel chain",3000));
    }

    public void singleAsyncService() {
        Log.d("BackgroundTasksTest", "singleAsyncService");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task Service");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task",5000));
    }

    public void chainAsyncService() {
        Log.d("BackgroundTasksTest", "chainAsyncService");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain Service");
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("Continuing chain",3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("Continuing chain",3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess chain",10000));
    }

    public void singleAsyncCancelService() {
        Log.d("BackgroundTasksTest", "singleAsyncCancelService");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task Service");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task",3000));
    }

    public void chainAsyncCancelService() {
        Log.d("BackgroundTasksTest", "chainAsyncCancelService");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain Service");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel chain",3000));
    }


    public void singleAsyncServiceRemote() {
        Log.d("BackgroundTasksTest", "singleAsyncServiceRemote");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task ServiceRemote");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task",5000));
    }

    public void chainAsyncServiceRemote() {
        Log.d("BackgroundTasksTest", "chainAsyncServiceRemote");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain ServiceRemote");
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("Continuing chain",3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("Continuing chain",3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress chain",3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess chain",10000));
    }

    public void singleAsyncCancelServiceRemote() {
        Log.d("BackgroundTasksTest", "singleAsyncCancelService");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task ServiceRemote");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task",3000));
    }

    public void chainAsyncCancelServiceRemote() {
        Log.d("BackgroundTasksTest", "chainAsyncCancelService");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain ServiceRemote");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel chain",3000));
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}