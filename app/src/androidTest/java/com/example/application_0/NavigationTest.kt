package com.example.application_0

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.doubleClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import data.database.DatabaseProvider
import com.example.application_0.ui.theme.Application_0Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.runBlocking
import types.MemberRole
import types.NotificationType

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun initialiseDatabase() {
        DatabaseProvider.initialise(ApplicationProvider.getApplicationContext())
        runBlocking {
            DatabaseProvider.current().resetDatabaseToDummyData()
        }
    }

    @Test
    fun successfulGroupCreationUsesCreateGroupBackNavigation() {
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        composeRule.onNodeWithText("Create group").performClick()
        composeRule.onNodeWithText("Enter a group name").performTextInput("Test group")
        composeRule.onAllNodesWithText("Create group")[1].performClick()

        composeRule.onNodeWithTag("bottom_nav_groups").assertIsSelected()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Test group").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Test group").assertIsDisplayed()

        val group = runBlocking {
            DatabaseProvider.current().getGroups().single { it.name == "Test group" }
        }
        val members = runBlocking {
            DatabaseProvider.current().getGroupMembers().filter { it.groupId == group.id }
        }
        assertEquals(1, members.size)
        assertEquals("user-emma", members.single().userId)
        assertEquals(MemberRole.ADMIN, members.single().role)
        assertTrue(
            runBlocking {
                DatabaseProvider.current().getExpenses().none { it.groupId == group.id }
            }
        )

        openGroup(group.id)
        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Test group · 1 active members").assertIsDisplayed()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()
        composeRule.onNodeWithText("Jonas Madsen").assertIsNotDisplayed()
        composeRule.onNodeWithText("You · Group admin").assertIsDisplayed()
        composeRule.onNodeWithText("Invite a friend").assertIsDisplayed()
    }

    @Test
    fun newlyCreatedUserCanCreateGroupAndOpenMembers() {
        Authentication.logout()
        assertEquals(
            AccountCreationResult.CREATED,
            runBlocking {
                Authentication.createAccount(
                    "New Group Creator",
                    "new.group.creator@example.com",
                    "testpassword123"
                )
            }
        )
        loginAs("new.group.creator@example.com", "testpassword123")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("You are not a member of any groups.")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("You are not a member of any groups.").assertIsDisplayed()
        composeRule.onNodeWithText("Create group").performClick()
        composeRule.onNodeWithText("Enter a group name").performTextInput("First group")
        composeRule.onAllNodesWithText("Create group")[1].performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("First group").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("First group").assertIsDisplayed()

        val group = runBlocking {
            DatabaseProvider.current().getGroups().single { it.name == "First group" }
        }
        openGroup(group.id)
        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("New Group Creator").assertIsDisplayed()
        composeRule.onNodeWithText("You · Group admin").assertIsDisplayed()
        composeRule.onNodeWithText("Invite a friend").assertIsDisplayed()
    }

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
        openGroup("group-weekend")
        composeRule.onNodeWithText("Aarhus weekend").assertIsDisplayed()

        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Invite a friend").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Expenses", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Expenses", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Debt summary", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Debt summary", useUnmergedTree = true).performClick()
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

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("0 DKK").fetchSemanticsNodes().isNotEmpty() &&
                composeRule.onAllNodesWithText("300 DKK").fetchSemanticsNodes().isNotEmpty()
        }
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
    fun settingsReturnToTheirEntryScreenAndPreserveNotificationsBackTarget() {
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithTag("bottom_nav_profile").performClick()
        composeRule.onNodeWithText("Profile settings").performClick()
        composeRule.onNodeWithText("Email address").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()

        composeRule.onNodeWithText("Notification settings").performClick()
        composeRule.onNodeWithTag("notification_payments").performClick()
        composeRule.onNodeWithTag("notification_invitations").performClick()
        composeRule.waitUntil(5_000) {
            runBlocking {
                DatabaseProvider.current().getUserById("user-emma")
                    ?.notificationSettings?.invitations == false
            }
        }
        composeRule.onNodeWithText("Notification settings").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Notifications").performClick()
        composeRule.onNodeWithText("Notification settings").performClick()
        composeRule.onNodeWithTag("notifications_enabled").performClick()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Emma Nielsen").assertIsDisplayed()
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
        openGroup("group-weekend")
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Debt summary").fetchSemanticsNodes().isNotEmpty()
        }
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

        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Dinner at the harbour")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Expenses").assertIsDisplayed()
        composeRule.onNodeWithText("Aarhus weekend").assertIsDisplayed()
        composeRule.onNodeWithText("Dinner at the harbour").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("TOTAL GROUP SPENDING")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("TOTAL GROUP SPENDING").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithTag("bottom_nav_home").performClick()
        composeRule.onNodeWithText("Hey, Jonas.").assertIsDisplayed()
    }

    @Test
    fun groupSelectionAndNotificationsKeepTheGroupsStack() {
        loginAs("jonas.madsen@example.com", "1232")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-flat")
        composeRule.onNodeWithText("Nørrebro flat").assertIsDisplayed()

        composeRule.onNodeWithText("Debt summary", useUnmergedTree = true).performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("PAYMENTS TO YOU")
                .fetchSemanticsNodes().isNotEmpty()
        }
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
        openGroup("group-flat")
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

        waitForTag("group_card_group-flat")
        val firstCard = composeRule.onNodeWithTag(
            "group_card_group-weekend",
            useUnmergedTree = true
        ).getBoundsInRoot()
        val firstTitle = composeRule.onNodeWithText(
            "Aarhus weekend",
            useUnmergedTree = true
        ).getBoundsInRoot()
        val secondCard = composeRule.onNodeWithTag(
            "group_card_group-flat",
            useUnmergedTree = true
        ).getBoundsInRoot()
        assertTrue(firstTitle.left - firstCard.left >= 20.dp)
        assertTrue(firstTitle.top - firstCard.top >= 20.dp)
        assertTrue(secondCard.top - firstCard.bottom >= 11.dp)
    }

    @Test
    fun debtGroupsAreSortedByBalanceAndOpenTheSelectedGroup() {
        loginAs("jonas.madsen@example.com", "1232")

        composeRule.onNodeWithTag("bottom_nav_debt").performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("-30").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("-30").assertIsDisplayed()
        composeRule.onNodeWithText("YOU ARE OWED").assertIsDisplayed()
        composeRule.onNodeWithText("YOU OWE").assertIsDisplayed()
        val owedHeading = composeRule.onNodeWithText("YOU ARE OWED").getBoundsInRoot()
        val owingHeading = composeRule.onNodeWithText("YOU OWE").getBoundsInRoot()
        assertTrue(owedHeading.top < owingHeading.top)
        composeRule.onNodeWithTag("debt_group_card_group-flat").performClick()
        composeRule.onNodeWithText("Nørrebro flat").assertIsDisplayed()
        composeRule.onNodeWithText("Debt summary").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("YOU ARE OWED · DKK")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("YOU ARE OWED · DKK").assertIsDisplayed()
        composeRule.onNodeWithText("+120").assertIsDisplayed()
    }

    @Test
    fun invitationsAreVisibleToGroupAdminsAndOpenManagement() {
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-weekend")
        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Jason Manden").assertIsDisplayed()
        composeRule.onNodeWithText("Jason Manden").performClick()
        composeRule.onNodeWithText("Cancel Jason's invitation?").assertIsDisplayed()
        composeRule.onNodeWithText("Keep invitation").performClick()
        composeRule.onNodeWithText("Members").assertIsDisplayed()
    }

    @Test
    fun invitationsAreHiddenFromMembersWhoDidNotSendThem() {
        loginAs("jonas.madsen@example.com", "1232")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-weekend")
        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Jason Manden").assertIsNotDisplayed()
        composeRule.onNodeWithContentDescription("Manage Emma Nielsen").assertIsNotDisplayed()
    }

    @Test
    fun sendingInvitationPersistsInvitationAndRecipientNotification() {
        runBlocking {
            assertTrue(
                DatabaseProvider.current().removeGroupInvitation(
                    "invitation-weekend-mikkel",
                    "user-emma"
                ) == data.database.MembershipMutationResult.SUCCESS
            )
        }
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-weekend")
        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Invite a friend").performClick()
        composeRule.onNodeWithTag("group_invitation_email")
            .performTextInput("mikkel.sorensen@example.com")
        composeRule.onNodeWithText("Send invitation").performClick()
        composeRule.onNodeWithText("Members").assertIsDisplayed()

        runBlocking {
            val invitation = DatabaseProvider.current().getGroupInvitations().first {
                it.groupId == "group-weekend" && it.inviteeUserId == "user-mikkel"
            }
            val notification = DatabaseProvider.current().getNotifications().first {
                it.type == NotificationType.INVITATIONS &&
                    it.relatedEntityId == invitation.id
            }
            assertEquals("user-mikkel", notification.userId)
        }
    }

    @Test
    fun savingExpensePersistsItAndShowsItInGroupExpenses() {
        loginAs("emma.nielsen@example.com", "1231")
        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-weekend")
        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithTag("expense_description")
            .performTextInput("Dinner at ÆDEHUS")
        composeRule.onNodeWithText("Attach a receipt").performClick()
        composeRule.onNodeWithText("Attach sample receipt").performClick()
        composeRule.onNodeWithText("Receipt attached", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Save expense").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Dinner at ÆDEHUS has been added to Aarhus weekend.")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(
            "Dinner at ÆDEHUS has been added to Aarhus weekend."
        ).assertIsDisplayed()

        composeRule.onNodeWithText("View group expenses").performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Dinner at ÆDEHUS")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Dinner at ÆDEHUS").assertIsDisplayed()
        composeRule.onNodeWithText("Dinner at ÆDEHUS").performClick()
        composeRule.onNodeWithText("View receipt").assertIsDisplayed()
        composeRule.onNodeWithText("ÆDEHUS").assertIsDisplayed()

        runBlocking {
            val database = DatabaseProvider.current()
            val expense = database.getExpenses().single {
                it.groupId == "group-weekend" &&
                    it.description == "Dinner at ÆDEHUS" &&
                    it.authorId == "user-emma"
            }
            val splits = database.getExpenseSplits().filter { it.expenseId == expense.id }
            val groupMemberIds = database.getGroupMembers()
                .filter { it.groupId == expense.groupId }
                .map { it.userId }
                .toSet()
            assertEquals(480.0, expense.amount, 0.0)
            assertEquals("user-emma", expense.paidBy)
            assertEquals("sample://expense-receipt", expense.receiptUri)
            assertEquals(groupMemberIds, splits.map { it.userId }.toSet())
            assertEquals(expense.amount, splits.sumOf { it.amount }, 0.000001)
        }
    }

    @Test
    fun groupInvitationsRemainVisibleWhenInvitationNotificationsAreDisabledAndCanBeAccepted() {
        runBlocking {
            val database = DatabaseProvider.current()
            val user = checkNotNull(database.getUserById("user-mikkel"))
            database.updateUser(
                user.copy(
                    notificationsEnabled = true,
                    notificationSettings = user.notificationSettings.copy(invitations = false)
                )
            )
        }
        loginAs("mikkel.sorensen@example.com", "1234")

        composeRule.onNodeWithTag("bottom_nav_profile").performClick()
        composeRule.onNodeWithText("Group invitations").performClick()
        composeRule.onNodeWithTag("group_invitation_invitation-weekend-mikkel")
            .assertIsDisplayed()
            .performClick()
        composeRule.onNodeWithText("You are invited to join").assertIsDisplayed()
        composeRule.onNodeWithText("mikkel.sorensen@example.com").assertIsNotDisplayed()
        composeRule.onNodeWithText("Accept invitation").performClick()
        composeRule.onNodeWithText("Group invitations").assertIsDisplayed()
        composeRule.onNodeWithTag("group_invitation_invitation-weekend-mikkel")
            .assertIsNotDisplayed()

        runBlocking {
            val database = DatabaseProvider.current()
            assertTrue(
                database.getGroupMembers().any {
                    it.groupId == "group-weekend" && it.userId == "user-mikkel"
                }
            )
            assertTrue(
                database.getGroupInvitations().none {
                    it.id == "invitation-weekend-mikkel"
                }
            )
            val notifications = database.getNotifications()
            assertTrue(
                notifications.none {
                    it.type == NotificationType.INVITATIONS &&
                        it.relatedEntityId == "invitation-weekend-mikkel"
                }
            )
            assertTrue(
                notifications.any {
                    it.id ==
                        "notification-group-joined-group-weekend-user-mikkel-invitation-weekend-mikkel"
                }
            )
            assertTrue(
                notifications.any {
                    it.id ==
                        "notification-group-joined-group-weekend-user-emma-invitation-weekend-mikkel"
                }
            )
            assertTrue(
                notifications.any {
                    it.id ==
                        "notification-group-joined-group-weekend-user-jonas-invitation-weekend-mikkel"
                }
            )
            assertTrue(
                notifications.none {
                    it.id ==
                        "notification-group-joined-group-weekend-user-freja-invitation-weekend-mikkel"
                }
            )
        }
    }

    @Test
    fun notificationsAndGroupInvitationsShowNewestEntriesFirst() {
        runBlocking {
            val database = DatabaseProvider.current()
            val user = checkNotNull(database.getUserById("user-mikkel"))
            database.updateUser(
                user.copy(
                    notificationsEnabled = true,
                    notificationSettings = user.notificationSettings.copy(
                        groupActivity = true,
                        invitations = true
                    )
                )
            )
        }
        loginAs("mikkel.sorensen@example.com", "1234")

        composeRule.onNodeWithContentDescription("Notifications").performClick()
        waitForTag("notification_notification-invitation-weekend-mikkel")
        waitForTag("notification_notification-group")
        val newestNotificationTop = composeRule
            .onNodeWithTag("notification_notification-invitation-weekend-mikkel")
            .getBoundsInRoot().top
        val olderNotificationTop = composeRule
            .onNodeWithTag("notification_notification-group")
            .getBoundsInRoot().top
        assertTrue(newestNotificationTop < olderNotificationTop)

        composeRule.onNodeWithTag("bottom_nav_profile").performClick()
        composeRule.onNodeWithText("Group invitations").performClick()
        waitForTag("group_invitation_invitation-weekend-mikkel")
        waitForTag("group_invitation_group-flat")
        val newestInvitationTop = composeRule
            .onNodeWithTag("group_invitation_invitation-weekend-mikkel")
            .getBoundsInRoot().top
        val olderInvitationTop = composeRule
            .onNodeWithTag("group_invitation_group-flat")
            .getBoundsInRoot().top
        assertTrue(newestInvitationTop < olderInvitationTop)
    }

    @Test
    fun invitationNotificationReturnsThroughNotificationsToItsOriginalScreen() {
        runBlocking {
            val database = DatabaseProvider.current()
            val user = checkNotNull(database.getUserById("user-mikkel"))
            database.updateUser(
                user.copy(
                    notificationsEnabled = true,
                    notificationSettings = user.notificationSettings.copy(invitations = true)
                )
            )
        }
        loginAs("mikkel.sorensen@example.com", "1234")

        composeRule.onNodeWithTag("bottom_nav_debt").performClick()
        composeRule.onNodeWithContentDescription("Notifications").performClick()
        composeRule.onNodeWithTag("notification_notification-invitation-weekend-mikkel")
            .performClick()
        composeRule.onNodeWithText("You are invited to join").assertIsDisplayed()
        composeRule.onNodeWithText("Dismiss invitation").performClick()
        composeRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("YOUR NET BALANCE · DKK").assertIsDisplayed()

        runBlocking {
            val database = DatabaseProvider.current()
            assertTrue(
                database.getGroupInvitations().none {
                    it.id == "invitation-weekend-mikkel"
                }
            )
            assertTrue(
                database.getNotifications().none {
                    it.type == NotificationType.INVITATIONS &&
                        it.relatedEntityId == "invitation-weekend-mikkel"
                }
            )
        }
    }

    @Test
    fun groupMemberRemovalIsPersisted() {
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-weekend")
        composeRule.onNodeWithText("Members →", useUnmergedTree = true).performClick()
        composeRule.onNodeWithText("Jonas Madsen").performClick()
        composeRule.onAllNodesWithText("Remove member")[1].performClick()
        composeRule.onNodeWithText("Members").assertIsDisplayed()

        runBlocking {
            assertTrue(
                DatabaseProvider.current().getGroupMembers().none {
                    it.groupId == "group-weekend" && it.userId == "user-jonas"
                }
            )
        }
    }

    @Test
    fun expenseFlowSupportsSplittingReceiptAttachmentAndReceiptViewing() {
        loginAs("emma.nielsen@example.com", "1231")

        composeRule.onNodeWithTag("bottom_nav_groups").performClick()
        openGroup("group-weekend")
        composeRule.onNodeWithText("Expenses").performClick()
        composeRule.onNodeWithText("Add expense").performClick()
        composeRule.onNodeWithText("Split equally").performClick()
        composeRule.onNodeWithText("Split expense").assertIsDisplayed()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Split equally between 3 people", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Split equally between 3 people", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Attach a receipt").performClick()
        composeRule.onNodeWithText("Attach sample receipt").performClick()
        composeRule.onNodeWithText("✓  Receipt attached").assertIsDisplayed()
        composeRule.onNodeWithText("Save expense").performClick()
        composeRule.onNodeWithText("Expense saved").assertIsDisplayed()
        composeRule.onNodeWithText("View group expenses").performClick()
        composeRule.onNodeWithText(
            "Dinner at the harbour",
            useUnmergedTree = true
        ).performClick()
        composeRule.onNodeWithText("View receipt").assertIsDisplayed()
        composeRule.onNodeWithText("ÆDEHUS").assertIsDisplayed()
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
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithTag("bottom_nav_home").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun openGroup(groupId: String) {
        val tag = "group_card_$groupId"
        waitForTag(tag)
        composeRule.onNodeWithTag(tag, useUnmergedTree = true).performClick()
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText("Members →", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithTag(tag, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }
}
