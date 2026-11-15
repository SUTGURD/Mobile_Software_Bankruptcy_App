# AI Interaction and Failure Log

## Interaction log

| ID  | Date       | Tool and mode        | Task                         | Prompt or link                                                                    | Verification                         | Decision |
|-----|------------|----------------------|------------------------------|-----------------------------------------------------------------------------------|--------------------------------------|----------|
| A-1 | 2026-11-15 | GitHub Copilot (Auto) | Add the user data type       | "Create a User data type with the specified fields; update AI_LOG.md and TODO.md." | Gradle lint, unit tests, debug build | Kept     |
| A-2 | 2026-11-15 | GitHub Copilot (Auto) | Enable java.time API desugaring | "Update library dependencies to support Android API 26 such that LocalDateTime can be used. Do not make any other changes. Update AI_LOG.md accordingly." | Gradle lint, unit tests, debug build | Kept     |

## Failure log

| ID | What the AI got wrong | How we caught it | Fix or outcome |
|----|-----------------------|------------------|----------------|