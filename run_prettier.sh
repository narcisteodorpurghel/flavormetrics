#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <path>"
  exit 1
fi

TARGET_PATH="$1"

npx prettier --write "$TARGET_PATH"
