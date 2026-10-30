# AGENTS.md

## General

### Do

- Use ASD-STE100.
- Be concise.
- When presenting any information to the user, when breaking it down, you should use 1-4 base
  elements/points. It pertains to explaining what you did, explaining different options to choose,
  or anything else. Any complex piece of information should consist of 1-4 root level elements,
  which in their turn can be expanded or broken down further, if prompted or required.
- Always start work in a new git branch from `master`, unless the user names another base branch.
  Obey "Git".
- Don't leave any files unstaged and uncommitted. Either stage and commit or add to `.gitignore` if
  it makes sense.

### Don't

- Write any comments in the code
- Suppress any static analysis findings with `@Suppress` and don't baseline them
- Weaken detekt rules configuration without explicit user request

## Git

- Work only in a linked worktree under `.claude/worktrees/`, never in the main checkout. Parallel
  sessions in one checkout break each other's branches and uncommitted changes.
- If the session is in the main checkout, call `EnterWorktree` first. If it is already in a
  worktree, stay there and do not create a nested worktree.
- In the worktree, create a new branch from `master` (or the base branch that the user names):
  `git checkout -b <branch> master`.
- One branch per session, unless the user deviates from initial topic - in this case ask the user if
  you should continue working in the same branch or create a new one.
- At the end commit all the work. Keep the branch and report its name. Do not merge it, unless the
  user asks for it.
- Push the branch and open a PR into `master`. Never push to `master` directly.
- Fixes, corrections, improvements belong in the same branch.

## Verification

### L1: Static analysis

```shell
./gradlew detekt # runs automatically on pre-commit
```

### L2: Unit tests

For quick feedback run specific tests instead of all tests.

```shell
./gradlew test # all tests
./gradlew test --tests <TestClass> # one test class
```

### L3: Plugin verification

```shell
./gradlew verifyPlugin # binary compatibility with the recommended IDE versions, slow
```

Run it when the change touches the IntelliJ Platform API, `plugin.xml` or the platform version.

### L4: Manual check

`./gradlew runIde` starts a sandbox IDE with the plugin. The UI has no automated tests, so ask the
user to check a UI change there.

## Self-editing

When editing .md files, including this file:

- Use 1-3 sentences per entry: the rule, plus a short reason when the reason is not obvious. When
  a change sets a convention, propose the edit unprompted, in the same turn.
- Never write in the first person. Use "the user", "the workspace" or the imperative.
- Only make additions to the agentic setup docs if: 1) it is required by the current setup
  explicitly; 2) you can provide strong arguments that not having them will hinder or disrupt the
  project in any way.
- Don't describe or reference the concrete modules, files, classes, functions currently present in
  this project. Refer to them as generic abstractions.
