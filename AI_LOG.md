# AI Interaction and Failure Log

## Interaction log

| ID  | Date       | Tool and mode        | Task                         | Prompt or link                                                                    | Verification                         | Decision |
|-----|------------|----------------------|------------------------------|-----------------------------------------------------------------------------------|--------------------------------------|----------|
| A-1 | 2026-11-15 | GitHub Copilot (Auto) | Add the user data type       | "Create a User data type with the specified fields; update AI_LOG.md and TODO.md." | Gradle lint, unit tests, debug build | Kept     |
| A-2 | 2026-11-15 | GitHub Copilot (Auto) | Enable java.time API desugaring | "Update library dependencies to support Android API 26 such that LocalDateTime can be used. Do not make any other changes. Update AI_LOG.md accordingly." | Gradle lint, unit tests, debug build | Kept     |
| A-3 | 2026-11-15 | GitHub Copilot (Auto) | Create dummy data for every model | "Read AGENTS.md, TODO.md, and app/src/main/kotlin/types/models.kt. Create sensible dummy data for every data class. Use DKK for currency. Update AI_LOG.md and TODO.md." | Gradle lint, unit tests, debug build | Kept |
| A-4 | 2026-11-15 | GitHub Copilot (Auto) | Add password hashing and verification | "Read AGENTS.md, TODO.md, app/src/main/kotlin/types/models.kt, and app/src/main/kotlin/data/DummyData.kt. Create app/src/main/kotlin/utils/password.kt. Use SHA-256 to implement a simple one-way hashing algorithm for passwords. Also implement password verification. Then update DummyData.kt to include passwords for users that utilise this algorithm. Ensure that the password for each user is \"123<i>\", where <i> is the order the user appears in the dummy data. If necessary, write an asynchronous function to achieve this. Then update AI_LOG.md and TODO.md." | Gradle lint, unit tests, debug build | Kept |

## Failure log

| ID | What the AI got wrong | How we caught it | Fix or outcome |
|----|-----------------------|------------------|----------------|