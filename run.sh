#!/usr/bin/env bash
echo "Run (load-file \"src/dactyl_keyboard/dactyl.clj\") to generate files"
docker run -i -t -v "$(pwd)":/app dactyl-manuform-lein repl
