package com.example.application_0

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.doubleClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.application_0.ui.theme.Application_0Theme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginAndGroupScreensFollowTheNavigationModel() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Welcome back. Enter your details below.").assertIsDisplayed()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onAllNodesWithText("Log in").get(1).performClick()
        composeRule.onNodeWithText("Good morning, Emma Nielsen").assertIsDisplayed()
        assertEquals(AuthenticatedUser("user-emma"), Authentication.currentUser.value)

        composeRule.onNodeWithText("Groups").performClick()
        composeRule.onNodeWithText("Create group").performClick()
        composeRule.onNodeWithText("Aarhus weekend").performClick()
        composeRule.onNodeWithText("Aarhus weekend").assertIsDisplayed()

        composeRule.onAllNodesWithText("Members").get(1).performClick()
        composeRule.onNodeWithText("Manage members").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onAllNodesWithText("Expenses").get(1).performClick()
        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Back").performClick()
        composeRule.onNodeWithText("Debt summary").performClick()
        composeRule.onNodeWithText("Debt summary").assertIsDisplayed()
    }

    @Test
    fun createAccountCannotOpenHomeWithoutAuthentication() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Create account").performClick()
        composeRule.onNodeWithText("Account creation is not available with dummy data.").assertIsDisplayed()
    }

    @Test
    fun invalidLoginDoesNotOpenHome() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("wrong-password")
        composeRule.onAllNodesWithText("Log in").get(1).performClick()

        composeRule.onNodeWithText("Email or password is incorrect.").assertIsDisplayed()
        composeRule.onNodeWithText("Welcome back. Enter your details below.").assertIsDisplayed()
    }

    @Test
    fun logoutClearsCurrentUserAndReturnsToEntry() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onAllNodesWithText("Log in").get(1).performClick()
        composeRule.onNodeWithText("Good morning, Emma Nielsen").assertIsDisplayed()

        Authentication.logout()

        composeRule.onNodeWithText("Shared expenses, made simple").assertIsDisplayed()
        assertEquals(null, Authentication.currentUser.value)
    }

    @Test
    fun profileAndNotificationsShowDataForTheAuthenticatedUser() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onAllNodesWithText("Log in").get(1).performClick()

        composeRule.onNodeWithText("DKK 0.00").assertIsDisplayed()
        composeRule.onNodeWithText("DKK 300.00").assertIsDisplayed()
        composeRule.onNodeWithText("Profile").performClick()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()
        composeRule.onNodeWithText("emma.nielsen@example.com").assertIsDisplayed()

        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("View notifications").performClick()
        composeRule.onNodeWithText("Jonas paid his dinner share.").assertIsDisplayed()
        assertEquals(
            0,
            composeRule.onAllNodesWithText("Emma added a DKK 900.00 dinner expense.")
                .fetchSemanticsNodes().size
        )
    }

    @Test
    fun doubleBackTapDoesNotNavigatePastHome() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onAllNodesWithText("Log in").get(1).performClick()
        composeRule.onNodeWithText("Good morning, Emma Nielsen").assertIsDisplayed()
        composeRule.onNodeWithText("Groups").performClick()

        composeRule.onNodeWithText("Back").performTouchInput {
            doubleClick()
        }

        composeRule.onNodeWithText("Good morning, Emma Nielsen").assertIsDisplayed()
    }
}
