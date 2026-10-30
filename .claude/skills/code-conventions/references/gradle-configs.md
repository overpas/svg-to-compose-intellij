# Gradle configs

## Use implementation() by default

Use `implementation()` instead of `api()`.

## Extract common configuration to convention plugins

Each convention plugin should be responsible for one thing, e.g. static analysis, code coverage,
etc.

## Sorting

Applied plugins: `id()` plugins first, then `alias()` plugins, each group sorted alphabetically.
Exception: a convention plugin keeps a plugin that another one needs first.

The dependencies of one configuration stay together. Inside it, the order is projects, libs,
test-only libs, and each group is sorted alphabetically.
