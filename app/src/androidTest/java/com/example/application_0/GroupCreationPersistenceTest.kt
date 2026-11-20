package com.example.application_0

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import data.database.AppDatabase
import data.database.RoomDatabaseOperations
import data.database.toEntity
import java.time.LocalDateTime
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import types.Expense
import types.Group
import types.GroupMember
import types.MemberRole

@RunWith(AndroidJUnit4::class)
class GroupCreationPersistenceTest {
    @Test
    fun newGroupContainsOnlyItsAdminCreatorAndNoExpenses() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val room = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val now = LocalDateTime.now()
            val dao = room.appDao()
            val existingGroupId = "existing-group"
            val existingGroup = Group(
                id = existingGroupId,
                name = "Existing group",
                description = null,
                authorId = "creator",
                createdAt = now,
                allowAllMembersToAdd = true,
                allowAllMembersToRemove = false,
                currency = "DKK"
            )
            dao.insertGroups(listOf(existingGroup.toEntity()))
            dao.insertGroupMembers(
                listOf("creator", "other-member").map {
                    GroupMember(existingGroupId, it, MemberRole.MEMBER, now).toEntity()
                }
            )
            dao.insertExpenses(
                listOf(
                    Expense(
                        id = "existing-expense",
                        groupId = existingGroupId,
                        amount = 25.0,
                        description = "Existing expense",
                        createdAt = now,
                        paidBy = "creator",
                        receiptUri = null,
                        authorId = "creator"
                    ).toEntity()
                )
            )

            val newGroupId = "new-group"
            val newGroup = existingGroup.copy(
                id = newGroupId,
                name = "New group"
            )
            val creatorMembership = GroupMember(
                groupId = newGroupId,
                userId = "creator",
                role = MemberRole.ADMIN,
                joinedAt = now
            )
            val operations = RoomDatabaseOperations(room)

            assertTrue(operations.createGroup(newGroup, creatorMembership))
            assertEquals(
                listOf(creatorMembership),
                operations.getGroupMembers().filter { it.groupId == newGroupId }
            )
            assertTrue(operations.getExpenses().none { it.groupId == newGroupId })

            assertFalse(
                operations.createGroup(
                    newGroup.copy(name = "Replacement group"),
                    creatorMembership.copy(userId = "other-member")
                )
            )
            assertEquals(
                "New group",
                operations.getGroups().single { it.id == newGroupId }.name
            )
            assertEquals(
                listOf(creatorMembership),
                operations.getGroupMembers().filter { it.groupId == newGroupId }
            )
        } finally {
            room.close()
        }
    }

    @Test
    fun existingGroupIdCannotReplaceItsGroupOrMembers() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val room = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val now = LocalDateTime.now()
            val group = Group(
                id = "existing-group",
                name = "Existing group",
                description = null,
                authorId = "creator",
                createdAt = now,
                allowAllMembersToAdd = true,
                allowAllMembersToRemove = false,
                currency = "DKK"
            )
            val originalMembers = listOf(
                GroupMember(group.id, "creator", MemberRole.ADMIN, now),
                GroupMember(group.id, "other-member", MemberRole.MEMBER, now)
            )
            room.appDao().insertGroups(listOf(group.toEntity()))
            room.appDao().insertGroupMembers(originalMembers.map { it.toEntity() })

            assertFalse(
                RoomDatabaseOperations(room).createGroup(
                    group.copy(name = "Replacement group"),
                    originalMembers.first()
                )
            )
            assertEquals(group, RoomDatabaseOperations(room).getGroups().single())
            assertEquals(
                originalMembers.toSet(),
                RoomDatabaseOperations(room).getGroupMembers().toSet()
            )
        } finally {
            room.close()
        }
    }
}
