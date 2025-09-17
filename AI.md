## AI Assistance Disclosure

This project used AI assistance to improve documentation and code quality in a limited, reviewer-friendly manner. This file records where and how AI was used, following the iP.AI route.

### Tools Used
- AI coding assistant (GPT-5) in Cursor.
- Gradle Checkstyle and unit tests to validate changes.

### Scope of AI-Assisted Edits
The AI assisted with writing JavaDoc comments and small code-quality improvements, adhering to the CS2103/T guidelines (code quality and documentation).

Files updated with AI assistance:
- `src/main/java/boyd/Launcher.java`
  - Added JavaDoc to `main(String[] args)` describing purpose and parameters.

- `src/main/java/boyd/MainWindow.java`
  - Added/improved JavaDoc for `initialize()`, `setBoyd(Boyd)`, and `handleUserInput()`.
  - Added a guard clause in `setBoyd` to validate non-null parameter.
  - Removed duplicate Javadoc to satisfy Checkstyle.

- `src/main/java/boyd/tasks/Task.java`
  - Added parameter validation (reject null/blank descriptions) with corresponding JavaDoc documenting `IllegalArgumentException`.

### Rationale and How AI Helped
- Generated concise, minimal-yet-sufficient JavaDoc consistent with CS2103/T documentation guidance (top-down, comprehensible, non-duplicative).
- Suggested guard clauses and explicit parameter validation to meet code-quality expectations (avoid hidden assumptions, fail fast with clear messages).
- Ensured no UI printing from core logic and preserved separation of concerns.

### Safeguards and Verification
- All changes compiled successfully.
- Unit tests: `./gradlew test` passed.
- Static checks: `./gradlew check` passed; fixed one Checkstyle error (invalid Javadoc position) before finalizing.

### Notes
- No functional behavior was changed except for safer validation in `Task` constructor; downstream code already assumes non-empty descriptions, so this aligns with expected usage.
- No external dependencies were added.

If further AI-assisted changes are made, update this file with the date, files, and a short summary of the assistance.

### UI Polish (AI-Assisted)
- Files: `src/main/resources/view/MainWindow.fxml`, `src/main/resources/view/DialogBox.fxml`, `src/main/resources/view/styles.css`, `src/main/java/boyd/utils/DialogBox.java`
- Changes:
  - Optimized layout to reduce wasted space: moved input into an `HBox`, enabled `fitToWidth` on `ScrollPane`, tightened paddings, and added a compact spacing.
  - Added a CSS stylesheet with compact typography and bubble styles; distinct variants for user, Boyd, and error messages.
  - Reduced avatar size to save vertical space and improved alignment.
  - Added programmatic style-class assignment in `DialogBox` for Boyd and error bubbles.
- Rationale: Improve readability in a small window, support long replies without excessive background space.
- Verification: App builds, tests pass, and Checkstyle passes.

### Parser/Storage Formatting Corrections
- Files: `src/main/java/boyd/tasks/*`, `src/main/java/boyd/utils/Parser.java`, `src/main/java/boyd/utils/Storage.java`
- Changes:
  - Reverted experimental tag persistence to match the original iP data format expected by tests.
  - Fixed Checkstyle issues (indentation, missing Javadoc) introduced during edits.
- Rationale: Maintain backward compatibility with existing tests and storage format.
- Verification: `./gradlew test` and `./gradlew check` both pass after changes.

### Level 8: Dates and Times (Event validation)
- Files: `src/main/java/boyd/tasks/Event.java`, `src/main/java/boyd/utils/Parser.java`, `src/main/java/boyd/utils/Storage.java`
- Changes:
  - `Event` now stores start/end as `LocalDateTime` and validates input using the same `uuuu-MM-dd HH:mm` format used for `Deadline`.
  - `Parser` surfaces invalid event datetime input as a user-friendly error.
  - `Storage` continues to persist events as `E | done | desc | yyyy-MM-dd HH:mm - yyyy-MM-dd HH:mm`.
- Rationale: Align event datetime handling with deadlines and reject invalid inputs like `/from 2 /to 3`.
- Verification: Unit tests and Checkstyle pass.

