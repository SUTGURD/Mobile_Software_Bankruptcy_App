package data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import data.DummyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import types.Expense
import types.ExpenseSplit
import types.Group
import types.GroupInvitation
import types.GroupMember
import types.Notification
import types.Payment
import types.Reminder
import types.User

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        GroupInvitationEntity::class,
        ExpenseEntity::class,
        ExpenseSplitEntity::class,
        PaymentEntity::class,
        ReminderEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bankruptcy.db"
                ).addCallback(DatabaseSeedCallback(context.applicationContext))
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { instance = it }
            }
    }
}

private class DatabaseSeedCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(context)
            val dao = database.appDao()
            database.withTransaction {
                dao.insertUsers(DummyData.users.map { it.toEntity() })
                dao.insertGroups(DummyData.groups.map { it.toEntity() })
                dao.insertGroupMembers(DummyData.groupMembers.map { it.toEntity() })
                dao.insertGroupInvitations(DummyData.groupInvitations.map { it.toEntity() })
                dao.insertExpenses(DummyData.expenses.map { it.toEntity() })
                dao.insertExpenseSplits(DummyData.expenseSplits.map { it.toEntity() })
                dao.insertPayments(DummyData.payments.map { it.toEntity() })
                dao.insertReminders(DummyData.reminders.map { it.toEntity() })
                dao.insertNotifications(DummyData.notifications.map { it.toEntity() })
            }
        }
    }
}

class RoomDatabaseOperations(private val dao: AppDao) : DatabaseOperations {
    override suspend fun getUsers(): List<User> =
        dao.getUsers().map { it.toModel() }

    override suspend fun getUserById(id: String): User? =
        dao.getUserById(id)?.toModel()

    override suspend fun getUserByEmail(email: String): User? =
        dao.getUserByEmail(email)?.toModel()

    override suspend fun getGroups(): List<Group> =
        dao.getGroups().map { it.toModel() }

    override suspend fun getGroupMembers(): List<GroupMember> =
        dao.getGroupMembers().map { it.toModel() }

    override suspend fun getGroupInvitations(): List<GroupInvitation> =
        dao.getGroupInvitations().map { it.toModel() }

    override suspend fun getExpenses(): List<Expense> =
        dao.getExpenses().map { it.toModel() }

    override suspend fun getExpenseSplits(): List<ExpenseSplit> =
        dao.getExpenseSplits().map { it.toModel() }

    override suspend fun getPayments(): List<Payment> =
        dao.getPayments().map { it.toModel() }

    override suspend fun getReminders(): List<Reminder> =
        dao.getReminders().map { it.toModel() }

    override suspend fun getNotifications(): List<Notification> =
        dao.getNotifications().map { it.toModel() }
}

object DatabaseProvider {
    private var operations: DatabaseOperations? = null

    fun initialise(context: Context) {
        if (operations == null) {
            operations = RoomDatabaseOperations(AppDatabase.getInstance(context).appDao())
        }
    }

    fun current(): DatabaseOperations =
        checkNotNull(operations) { "The database provider must be initialised before use." }
}
