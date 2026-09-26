#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECTS=("expo-user" "expo-admin")
PROJECT_SELECTION="all"
PROFILE="preview"
SKIP_INSTALL="false"
SKIP_BUNDLE="false"

usage() {
  cat <<'USAGE'
Usage:
  ./scripts/test-eas-build-local.sh [options]

Options:
  --project all|user|admin   Project to test (default: all)
  --profile PROFILE          EAS profile (default: preview)
  --skip-install             Do not run npm ci
  --skip-bundle              Skip TypeScript and Metro bundle checks
  -h, --help                 Show this help

The script runs, for each selected project:
  1. npm ci (unless --skip-install)
  2. npx tsc --noEmit
  3. npx expo export --platform android
  4. eas build --local --platform android --non-interactive

Requirements:
  - Node.js and npm
  - EAS project initialized with `npx eas init` (extra.eas.projectId in app.json)
  - Expo authentication through EXPO_TOKEN or a local EAS login
  - Android local-build prerequisites required by EAS CLI
USAGE
}

fail() { echo "ERROR: $*" >&2; exit 1; }
info() { printf '\n==> %s\n' "$*"; }

while [[ $# -gt 0 ]]; do
  case "$1" in
    --project)
      [[ $# -ge 2 ]] || fail "--project requires all, user, or admin"
      PROJECT_SELECTION="$2"; shift 2 ;;
    --profile)
      [[ $# -ge 2 ]] || fail "--profile requires an EAS profile name"
      PROFILE="$2"; shift 2 ;;
    --skip-install) SKIP_INSTALL="true"; shift ;;
    --skip-bundle) SKIP_BUNDLE="true"; shift ;;
    -h|--help) usage; exit 0 ;;
    *) fail "Unknown option: $1 (use --help)" ;;
  esac
done

case "$PROJECT_SELECTION" in
  all) SELECTED_PROJECTS=("${PROJECTS[@]}") ;;
  user) SELECTED_PROJECTS=("expo-user") ;;
  admin) SELECTED_PROJECTS=("expo-admin") ;;
  *) fail "Invalid project '$PROJECT_SELECTION'; use all, user, or admin" ;;
esac

for command in node npm npx; do
  command -v "$command" >/dev/null 2>&1 || fail "Missing required command: $command"
done

EAS=(npx --yes eas-cli@latest)
ARTIFACT_DIR="$ROOT_DIR/artifacts/eas-local"
mkdir -p "$ARTIFACT_DIR"

printf 'AvendOc local EAS build test\nProfile: %s\nProjects: %s\n' "$PROFILE" "${SELECTED_PROJECTS[*]}"

for project in "${SELECTED_PROJECTS[@]}"; do
  PROJECT_DIR="$ROOT_DIR/$project"
  [[ -d "$PROJECT_DIR" ]] || fail "Project directory not found: $PROJECT_DIR"
  [[ -f "$PROJECT_DIR/app.json" ]] || fail "Missing app.json in $PROJECT_DIR"
  [[ -f "$PROJECT_DIR/eas.json" ]] || fail "Missing eas.json in $PROJECT_DIR"

  info "Testing $project"
  cd "$PROJECT_DIR"

  if [[ "$SKIP_INSTALL" != "true" ]]; then
    info "$project: installing locked dependencies"
    npm ci
  fi

  PROJECT_ID="$(node -e "const c=require('./app.json').expo; process.stdout.write(c.extra?.eas?.projectId || '')")"
  [[ -n "$PROJECT_ID" ]] || fail "$project is not linked to EAS. Run 'npx eas init' in $PROJECT_DIR and commit app.json before using this script."

  if [[ "$SKIP_BUNDLE" != "true" ]]; then
    info "$project: TypeScript check"
    npx tsc --noEmit

    info "$project: Metro Android bundle check"
    rm -rf .eas-local-dist
    npx expo export --platform android --output-dir .eas-local-dist
    rm -rf .eas-local-dist
  fi

  OUTPUT="$ARTIFACT_DIR/${project}-${PROFILE}.apk"
  rm -f "$OUTPUT"
  info "$project: EAS local Android build"
  "${EAS[@]}" build \
    --platform android \
    --profile "$PROFILE" \
    --local \
    --non-interactive \
    --output "$OUTPUT"

  [[ -s "$OUTPUT" ]] || fail "$project build completed without producing $OUTPUT"
  printf 'PASS: %s -> %s (%s bytes)\n' "$project" "$OUTPUT" "$(stat -c '%s' "$OUTPUT")"
done

info "All selected EAS local builds passed"
