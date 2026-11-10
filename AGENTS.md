This is an Android application developed in Android Studio. Prioritise mobile-first patterns, Android conventions, performance, accessibility, and compatibility across supported Android versions.

Use Kotlin as the primary programming language. Use Jetpack Compose for UI development. Use AndroidX and Jetpack libraries where appropriate.

## Android SDK and Gradle

Before writing any code that touches Android API's, Kotlin, Jetpack Compose, AndroidX, or Gradle:

1. Inspect app/build.gradle.kts, gradle/libs.versions.toml, and gradle/wrapper/gradle-wrapper.properties
2. Identify the versions of AGP, Kotlin, Gradle, Jetpack Compose BOM, and Android SDK (compileSdk, minSdk, and targetSdk).
3. Where relevant, fetch the matching versioned documentation:
   for Android API's: `https://developer.android.com/reference`,
   for Jetpack Compose: `https://developer.android.com/develop/ui/compose/documentation`,
   for Kotlin: `https://kotlinlang.org/docs/`,
   for Gradle: `https://docs.gradle.org/current/userguide/`.

Follow links to the specific documentation you need; never answer from memory.

## Commands

- `./gradlew assembleDebug`: Build the debug application.
- `./gradlew assembleRelease`: Build the release application.
- `./gradlew test`: Run unit tests.
- `./gradlew lint`: Run linting.
- `./gradlew clean`: Clean builds.
- `./gradlew tasks`: List available Gradle tasks.

Run linting, unit tests, and assemble a debug build before declaring any task done. If a command fails, report the error.

## Navigation and UI

Follow the UI and navigation patterns already established in the project. When implementing or modifying a specific screen, look for an image matching the screen in `app/screenshots`. If one exist, model and style the screen according to the image, otherwise use minimal styling unless explicitly told otherwise. Use composable functions for UI components and ViewModel for business logic and screen level state. Keep reusable components separate from screen specific components. Always make a reusable component whenever two or more screens share functionality or styling. Use NavController for all navigation.

Documentation: `https://developer.android.com/topic/architecture/ui-layer`, `https://developer.android.com/guide/navigation`.

## Rules

Never make git commits. It is the user's job to manually review and commit changes.

Prefer AndroidX and official Jetpack libraries over third-party libraries, and check your available skills before adding dependencies.

Respect existing architecture and follow conventions. Documentation: `https://developer.android.com/topic/architecture`.

## Project conventions

- **Data model**: Kotlin data classes represent the application's entities and live in `app/src/main/kotlin/types/models.kt`. Any schema change starts there, not in a screen file.
- **Dummy data**: Lives in `app/src/main/kotlin/data/` until persistence is added. Treated as the temporary stand-in for a database. Do not allow UI code to assume it is real.
- **Screens**: See `TODO.md` for the build order. Screen files live in `app/src/main/kotlin/com/example/application_0/` follow the S-IDs from the design document navigation model.
- **Screens**: Keep unit tests in `app/src/test/` and Android environment tests in `app/src/androidTest/`. Add tests for business logic and UI navigation.
- **Commits**: Lowercase, imperative, no full stop, terse. The diff explains itself. For example: `add login screen`, not `Implement account login functionality`.

## AI use disclosure (course requirement)

This repository is built with AI assistance under the course design document AI policy. Drafting, scaffolding, and explanations may come from AI tools.

Prioritisation and scope decisions, cross-artifact consistency checks, and final design rationale are the team's own and are logged in Appendix A of the design document per session, not in this file.

## Tone and style constraints for AI models

When contributing code, documentation, commit messages, or responses in this repository, you must adhere strictly to the following language requirements:

1. **Spelling**: Use British English spelling throughout (for example: initialise, prioritise, behaviour, prioritisation, colour, standardise).
2. **No contractions**: Write out full words without contractions (for example: write "do not" instead of "don't", "it is" instead of "it's", "cannot" instead of "can't").
3. **No Latin abbreviations**: Do not use abbreviations such as "e.g.", "i.e.", "etc.", or "vs.". Write out full phrases (for example: "for example", "that is", "and so on", "versus").
4. **No em-dashes or punctuation tricks**: Do not use em-dashes (`—`), en-dashes (`–`), or double hyphens (`--`). Use colons, commas, or standard full stops to structure sentences.
5. **Explicit avoidance of AI tropes and buzzwords**:
    - Do not use flowery or overused AI vocabulary (for example: "delve", "tapestry", "seamless", "crucial", "robust", "landscape", "beacon", "game-changer", "fostering", "testament", "effortlessly", "elevate").
    - Do not use conversational setups or robotic meta-announcements (for example: "Here is the code", "In this document", "Let us dive in", "Certainly!").
    - Do not append unnecessary summary blocks, conclusion paragraphs, or polite sign-offs.

## AI logging protocol

After completing any significant task (generating code, setting up routes, refactoring, or writing tests), you must append an entry to `AI_LOG.md`.

1. **Interaction format**:
   Add a new row to the Interaction Log table in `AI_LOG.md`:
   `| A-# | <Date> | <Tool/Model> | <Task description> | "<Prompt used>" | <Verification method> | <Kept/Modified/Discarded> |`

2. **Failure format**:
   If you made an error during generation (for example, wrong spelling, invalid syntax, incorrect types, or breaking project rules) that required correction, add a row to the Failure Log table in `AI_LOG.md`:
   `| FL-# | <Description of error> | <How it was identified> | <Resolution> |`

## Agent execution constraints

- Do not install, uninstall, or modify dependencies unless explicitly requested.
- Do not edit root configuration files (`build.gradle.kts`, `settings.gradle.kts` etc.) unless explicitly instructed.
- If a terminal command fails (such as `./gradlew lint`), report the error to the user rather than modifying project setup.

3. **Task tracking format**:
   When completing a task listed in `TODO.md`, you must update `TODO.md` by checking off the corresponding checkbox (`- [x]`) before finalising the task.