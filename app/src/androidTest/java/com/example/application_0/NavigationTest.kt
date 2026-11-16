package com.example.application_0

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.doubleClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.application_0.ui.theme.Application_0Theme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginAndGroupScreensFollowTheNavigationModel() {
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Welcome back. Enter your details below.").assertIsDisplayed()
        composeRule.onAllNodesWithText("Log in").get(1).performClick()
        composeRule.onNodeWithText("Good morning, Alex").assertIsDisplayed()

        composeRule.onNodeWithText("Groups").performClick()
        composeRule.onNodeWithText("Create group").performClick()
        composeRule.onAllNodesWithText("Create group").get(1).performClick()
        composeRule.onNodeWithText("Copenhagen Weekend").assertIsDisplayed()

        composeRule.onNodeWithText("Members").performClick()
        composeRule.onNodeWithText("Manage members").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Expenses").performClick()
        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Debt summary").performClick()
        composeRule.onNodeWithText("Debt summary").assertIsDisplayed()
    }

    @Test
    fun createAccountOpensHomeWithoutFormInput() {
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Create account").performClick()
        composeRule.onAllNodesWithText("Create account").get(1).performClick()
        composeRule.onNodeWithText("Good morning, Alex").assertIsDisplayed()
    }

    @Test
    fun doubleBackTapDoesNotNavigatePastHome() {
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onAllNodesWithText("Log in").get(1).performClick()
        composeRule.onNodeWithText("Good morning, Alex").assertIsDisplayed()
        composeRule.onNodeWithText("Groups").performClick()

        composeRule.onNodeWithText("Back").performTouchInput {
            doubleClick()
        }

        composeRule.onNodeWithText("Good morning, Alex").assertIsDisplayed()
    }
}
