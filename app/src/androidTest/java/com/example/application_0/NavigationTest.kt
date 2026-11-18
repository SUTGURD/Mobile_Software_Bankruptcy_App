package com.example.application_0

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.doubleClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.application_0.ui.theme.Application_0Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Hey, Emma.").assertIsDisplayed()
        assertEquals(AuthenticatedUser("user-emma"), Authentication.currentUser.value)

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        composeRule.onNodeWithText("Create group").performClick()
        composeRule.onNodeWithText("Aarhus weekend").performClick()
        composeRule.onNodeWithText("Aarhus weekend").assertIsDisplayed()

        composeRule.onNodeWithText("Members →").performClick()
        composeRule.onNodeWithText("Invite a friend").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Expenses").performClick()
        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
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

        composeRule.onNodeWithText("Get started").performClick()
        composeRule.onNodeWithText("By continuing, you agree to our terms and privacy policy.")
            .assertIsDisplayed()
    }

    @Test
    fun invalidLoginDoesNotOpenHome() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("wrong-password")
        composeRule.onNodeWithText("Log in").performClick()

        composeRule.onNodeWithText("Email or password is incorrect.").assertIsDisplayed()
    }

    @Test
    fun logoutClearsCurrentUserAndReturnsToEntry() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Hey, Emma.").assertIsDisplayed()

        Authentication.logout()

        composeRule.onNodeWithText("Bankruptcy").assertIsDisplayed()
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

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onNodeWithText("Log in").performClick()

        composeRule.onNodeWithText("0 DKK").assertIsDisplayed()
        composeRule.onNodeWithText("300 DKK").assertIsDisplayed()
        val homeNotifications = composeRule.onNodeWithContentDescription("Notifications")
            .getBoundsInRoot()
        val homeGreeting = composeRule.onNodeWithText("Hey, Emma.").getBoundsInRoot()
        assertTrue(homeGreeting.top >= homeNotifications.bottom)

        composeRule.onNodeWithText("Profile").performClick()
        val profileNotifications = composeRule.onNodeWithContentDescription("Notifications")
            .getBoundsInRoot()
        assertEquals(homeNotifications, profileNotifications)
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()
        composeRule.onNodeWithText("emma.nielsen@example.com").assertIsDisplayed()
        composeRule.onNodeWithText("Default currency").assertIsDisplayed()
        composeRule.onNodeWithText("Verified").assertIsDisplayed()
        composeRule.onNodeWithText("Aug 2026").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Notifications").performClick()
        composeRule.onNodeWithTag("bottom_nav_profile").assertIsSelected()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Notifications").performClick()
        composeRule.onNodeWithTag("bottom_nav_profile").performClick()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Notifications").performClick()
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

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Hey, Emma.").assertIsDisplayed()
        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        composeRule.onNodeWithTag("group_card_group-weekend").performClick()
        composeRule.onNodeWithText("Debt summary").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Back").performTouchInput {
            doubleClick()
        }

        composeRule.onNodeWithText("Create group").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsNotDisplayed()
    }

    @Test
    fun expenseNotificationOpensItsRelatedGroupExpenses() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("jonas.madsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1232")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithContentDescription("Notifications").performClick()
        composeRule.onNodeWithTag("notification_notification-dinner").performClick()

        composeRule.onNodeWithText("Expenses").assertIsDisplayed()
        composeRule.onNodeWithText("Aarhus weekend").assertIsDisplayed()
        composeRule.onNodeWithText("Dinner at the harbour").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("TOTAL GROUP SPENDING").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("bottom_nav_home").performClick()
        composeRule.onNodeWithText("Hey, Jonas.").assertIsDisplayed()
    }

    @Test
    fun groupSelectionAndNotificationsKeepTheGroupsStack() {
        loginAs("jonas.madsen@example.com", "1232")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        composeRule.onNodeWithTag("group_card_group-flat").performClick()
        composeRule.onNodeWithText("Nørrebro flat").assertIsDisplayed()

        composeRule.onNodeWithText("Debt summary").performClick()
        composeRule.onNodeWithText("PAYMENTS TO YOU").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        composeRule.onNodeWithTag("bottom_nav_groups").assertIsSelected()
        composeRule.onNodeWithText("Back to group").performClick()
        composeRule.onNodeWithText("Nørrebro flat").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Create group").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsNotDisplayed()
    }

    @Test
    fun groupScreensOpenedFromHomeUseTheGroupsStack() {
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithText("Aarhus weekend").performClick()
        composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        composeRule.onNodeWithTag("bottom_nav_groups").assertIsSelected()
        composeRule.onNodeWithText("Add expense").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Create group").assertIsDisplayed()
    }

    @Test
    fun logoutResetsMenuStacksForTheNextUser() {
        loginAs("jonas.madsen@example.com", "1232")
        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        composeRule.onNodeWithTag("group_card_group-flat").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("bottom_nav_profile").performClick()
        composeRule.onNodeWithText("Log out").performClick()

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithTag("bottom_nav_groups").performClick()

        composeRule.onNodeWithText("Create group").assertIsDisplayed()
        composeRule.onNodeWithTag("group_card_group-weekend").assertIsDisplayed()
        composeRule.onNodeWithTag("group_card_group-flat").assertIsNotDisplayed()
        composeRule.onNodeWithContentDescription("Back").assertIsNotDisplayed()
    }

    @Test
    fun debtScreenShowsOneNetBalanceCardWithoutTheOldSubtitle() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("emma.nielsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1231")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Debt").performClick()

        composeRule.onNodeWithText("YOUR NET BALANCE · DKK").assertIsDisplayed()
        assertEquals(
            0,
            composeRule.onAllNodesWithText("Outstanding balances calculated from your expenses.")
                .fetchSemanticsNodes().size
        )
    }

    @Test
    fun groupCardsHaveSafeTextInsetsAndVisibleSeparation() {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput("jonas.madsen@example.com")
        composeRule.onNodeWithTag("login_password").performTextInput("1232")
        composeRule.onNodeWithText("Log in").performClick()
        composeRule.onNodeWithText("Groups").performClick()

        val firstCard = composeRule.onNodeWithTag("group_card_group-weekend").getBoundsInRoot()
        val firstTitle = composeRule.onNodeWithText(
            "Aarhus weekend",
            useUnmergedTree = true
        ).getBoundsInRoot()
        val secondCard = composeRule.onNodeWithTag("group_card_group-flat").getBoundsInRoot()
        assertTrue(firstTitle.left - firstCard.left >= 20.dp)
        assertTrue(firstTitle.top - firstCard.top >= 20.dp)
        assertTrue(secondCard.top - firstCard.bottom >= 11.dp)
    }

    @Test
    fun debtGroupsAreSortedByBalanceAndOpenTheSelectedGroup() {
        loginAs("jonas.madsen@example.com", "1232")

        composeRule.onNodeWithTag("bottom_nav_debt").performClick()
        composeRule.onNodeWithText("-30").assertIsDisplayed()
        composeRule.onNodeWithText("YOU ARE OWED").assertIsDisplayed()
        composeRule.onNodeWithText("YOU OWE").assertIsDisplayed()
        val owedHeading = composeRule.onNodeWithText("YOU ARE OWED").getBoundsInRoot()
        val owingHeading = composeRule.onNodeWithText("YOU OWE").getBoundsInRoot()
        assertTrue(owedHeading.top < owingHeading.top)
        composeRule.onNodeWithTag("debt_group_card_group-flat").performClick()
        composeRule.onNodeWithText("Nørrebro flat").assertIsDisplayed()
        composeRule.onNodeWithText("Debt summary").performClick()
        composeRule.onNodeWithText("YOU ARE OWED · DKK").assertIsDisplayed()
        composeRule.onNodeWithText("+120").assertIsDisplayed()
    }

    private fun loginAs(email: String, password: String) {
        Authentication.logout()
        composeRule.setContent {
            Application_0Theme {
                AppNavigation()
            }
        }

        composeRule.onNodeWithText("I already have an account").performClick()
        composeRule.onNodeWithTag("login_email").performTextInput(email)
        composeRule.onNodeWithTag("login_password").performTextInput(password)
        composeRule.onNodeWithText("Log in").performClick()
    }
}
