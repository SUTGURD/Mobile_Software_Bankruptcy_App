package domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import types.GroupMember
import types.MemberRole
import java.time.LocalDateTime

class GroupDataSeparationTest {
    private val groupMembers = listOf(
        GroupMember("group-1", "alice", MemberRole.ADMIN, LocalDateTime.now()),
        GroupMember("group-1", "bob", MemberRole.MEMBER, LocalDateTime.now()),
        GroupMember("group-2", "charlie", MemberRole.ADMIN, LocalDateTime.now())
    )

    @Test
    fun activeMemberIsPermittedToAccessGroupData() {
        assertTrue(isUserMemberOfGroup("alice", "group-1", groupMembers))
        assertTrue(isUserMemberOfGroup("bob", "group-1", groupMembers))
        assertTrue(isUserMemberOfGroup("charlie", "group-2", groupMembers))
    }

    @Test
    fun nonMemberIsDeniedAccessToGroupData() {
        assertFalse(isUserMemberOfGroup("charlie", "group-1", groupMembers))
        assertFalse(isUserMemberOfGroup("alice", "group-2", groupMembers))
        assertFalse(isUserMemberOfGroup("eve", "group-1", groupMembers))
    }

    private fun isUserMemberOfGroup(userId: String, groupId: String, members: List<GroupMember>): Boolean {
        return members.any { it.groupId == groupId && it.userId == userId }
    }
}
