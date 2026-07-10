#!/usr/bin/env sh
set -e
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
cat >&2 <<'MSG'
Gradle is not installed on PATH. Install Gradle 9.4.1 or run in GitHub Actions,
where gradle/actions/setup-gradle installs the requested Gradle version.
MSG
exit 127
