# Build checklist

Ordered, commit-sized. Check off as you go. "Later" items are intentionally deferred.

## Session 1: Data shapes

- [x] Create data type for users
- [x] Create data type for groups
- [x] Create data type for group members (type relating users to groups)
- [x] Create data type for expenses
- [x] Create data type for split expenses
- [x] Create data type for payments
- [x] Create data type for reminders
- [x] Create data type for notifications
- [x] Commit `add types for users, groups, expenses, split expenses, payments, reminders, and notifications`
- [x] Create dummy data
- [x] Commit `add dummy data for users, groups, expenses, split expenses, payments, reminders, and notifications`
- [x] Add hashed password field to user data type
- [x] Implement password hashing
- [x] Update dummy data for users to include hashed password values
- [x] Commit `add hashed password field to user data type and implement password hashing`

## Session 2: Debt calculation

- [x] Implement debt calculation for how much a person owes in a group
- [x] Implement debt calculation for how much a person is owed in a group
- [x] Implement debt calculation for how much a person owes to another person
- [x] Implement debt calculation for how much a person owes in total
- [x] Implement debt calculation for how much a person is owed in total
- [x] Commit `add debt calculation for how much a person owes and is owed in a group, in total, and to and by another person`

## Session 3: Screen structure and navigation

- [x] Create mockup screens based on the navigation model and low-fi wireframe
- [x] Route the screens according to the navigation model
- [x] Commit `add mockup screens and navigation`

## Session 4: Integrate dummy data and debt calculation

- [x] Implement login functionality and user authentication using dummy data
- [x] Commit `add login authentication using dummy data`
- [ ] Create ViewModels for:
  - [ ] UserProfileViewModel
  - [ ] GroupsViewModel
  - [ ] ExpensesViewModel
  - [ ] NotificationViewModel
  - [ ] DebtSummaryViewModel
- [ ] Commit `add ViewModels for user profiles, groups, expenses, notifications, and debt summaries`
- [ ] Replace placeholder UI with ViewModels fetching dummy data
- [ ] Commit `replace placeholder UI with ViewModels fetching dummy data`

## Later

- [ ] Create viewModels for the homescreen and group details screen when the UI prototype has been developed
- [ ] Update all UI and add styling when the UI prototype has been developed
- [ ] Switch from dummy data to persistent data storage
- [ ] Add syncStatus field (pending, synced, or failed) and synchronisation queue with retry and backoff