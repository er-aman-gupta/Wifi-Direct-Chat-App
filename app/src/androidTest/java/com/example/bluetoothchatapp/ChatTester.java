package com.example.bluetoothchatapp;
/*
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

import androidx.test.filters.LargeTest;


public class ChatTester {

    @Rule
    public ActivityTestRule<ChatActivity> activityActivityTestRule=new ActivityTestRule<>(ChatActivity.class);

    @Test
    public void tester{
        assertTrue(true);
    }
}
*/
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ChatTester {

    @Rule
    public ActivityTestRule<ChatActivity> activityTestingActivityTestRule=new ActivityTestRule<>(ChatActivity.class);

    @Test
    public void ensureTextChange(){

        onView(withId(R.id.writeMsg)).perform(typeText("YO"),closeSoftKeyboard());
    }


}
