#!/usr/bin/env sh
set -eu
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
echo "Gradle is not installed. GitHub Actions installs Gradle 8.14.4 before running this script." >&2
exit 127
