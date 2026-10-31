# Contributing to Sakura Tunes

Contributions are welcome through focused issues and pull requests.

## Before opening a pull request

1. Search existing issues and pull requests to avoid duplicate work.
2. Describe the problem, expected behavior, and actual behavior.
3. Keep changes scoped. Avoid mixing unrelated refactors with feature work.
4. Do not commit API keys, passwords, session cookies, signing keys, local configuration, or generated build outputs.
5. For UI changes, include before-and-after screenshots from a real device or emulator where practical.

## Development environment

- Android Studio or a compatible Gradle environment
- JDK 21
- Android SDK with the compile SDK configured by the project
- Git

Clone and inspect the project:

    git clone https://github.com/xboyrex/SakuraTunes.git
    cd SakuraTunes

Build a debug GMS variant:

    ./gradlew :app:assembleGmsDebug

Build release variants:

    ./gradlew :app:assembleFossRelease
    ./gradlew :app:assembleGmsRelease
    ./gradlew :app:assembleIzzyRelease

Run project checks appropriate to the change before submitting.

## Pull request expectations

A useful pull request should include:
- A clear summary of the change and its motivation.
- The affected modules, screens, providers, or build variants.
- Test commands executed and their results.
- Known limitations, migration notes, or follow-up work.
- Screenshots or logs when they materially help reviewers.

Keep commits readable and avoid adding generated files unless the change explicitly requires them.

## Code quality

Follow existing Kotlin, Gradle, Compose, and module conventions. Prefer small components, explicit state ownership, predictable coroutine lifecycles, and safe error handling. Avoid introducing dependencies without a clear need.

## Reporting security issues

Do not publish exploitable vulnerability details, tokens, or private user data in a public issue. Follow SECURITY.md for responsible reporting.

## License

By submitting a contribution, you agree that your contribution may be distributed under the repository's GPL-3.0 license.
