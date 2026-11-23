set shell := ["bash", "-euo", "pipefail", "-c"]

# List the recipes.
default:
    @just --list

# Install the pinned tools.
install:
    mise install

# Format the repository.
fmt:
    bun scripts/format.ts

# Fail when a file needs formatting.
fmt-check:
    bun scripts/format.ts --check

# Lint the workflows.
lint:
    actionlint
    zizmor .github/workflows

# Run the unit tests.
test:
    mvn --batch-mode --no-transfer-progress test

# Build the plugin jar (one build per pull request, tests included).
build:
    mvn --batch-mode --no-transfer-progress clean verify

# Everything CI requires.
check: fmt-check lint build

# Two clean builds, byte comparison, and the SHA-256 for a release.
reproducible:
    #!/usr/bin/env bash
    set -euo pipefail
    out="${RUNNER_TEMP:-$(mktemp -d)}"
    mvn --batch-mode --no-transfer-progress clean verify
    artifact="$(find target -maxdepth 1 -type f -name 'chestcleaner-*.jar' -print -quit)"
    test -n "$artifact"
    cp "$artifact" "$out/first-build.jar"
    mvn --batch-mode --no-transfer-progress clean verify
    cmp "$out/first-build.jar" "$artifact"
    sha256sum "$artifact" | tee "$out/artifact-sha256.txt"
    {
      echo "## Build artifact"
      echo
      echo "- File: \`$(basename "$artifact")\`"
      echo "- SHA-256: \`$(cut -d ' ' -f 1 "$out/artifact-sha256.txt")\`"
      echo "- Reproducibility: two clean builds produced identical bytes."
    } >> "${GITHUB_STEP_SUMMARY:-/dev/stdout}"

# Redacted secret scan: the pull request diff in CI, full history otherwise.
secrets:
    bun scripts/secrets.ts

# Install the committed git hooks.
hooks:
    git config --local core.hooksPath .githooks

# Fail when .agents/skills and .claude/skills disagree.
agent-files:
    bun scripts/agent-files.ts
