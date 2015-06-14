package com.example.backgroundtaskstest;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

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


    public void testSingleAsync() {
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task",5000));
    }

    public void testChainAsync() {
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

    public void testSingleAsyncCancel() {
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task",3000));
    }

    public void testChainAsyncCancel() {
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel chain",3000));
    }

    public void testSingleAsyncService() {
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task Service");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress task",3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess task",5000));
    }

    public void testChainAsyncService() {
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

    public void testSingleAsyncCancelService() {
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task Service");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady task",1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel task",3000));
    }

    public void testChainAsyncCancelService() {
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain Service");
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