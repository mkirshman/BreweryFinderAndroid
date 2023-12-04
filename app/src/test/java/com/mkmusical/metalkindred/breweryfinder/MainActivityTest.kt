package com.mkmusical.metalkindred.breweryfinder

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setUp() {
        // Initialize the activity scenario
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun validateSearchParameters() {
        // Test the case where no search parameter is entered
        onView(withId(R.id.findBreweryButton)).perform(click())
        onView(withText("At least one search parameter required")).check(matches(isDisplayed()))

        // Test the case where at least one search parameter is entered
        onView(withId(R.id.nameEditText)).perform(replaceText("TestName"))
        onView(withId(R.id.findBreweryButton)).perform(click())
    }
}