#!/bin/bash
set -e
echo "Starting LibMan Desktop Application..."
export PATH="$HOME/.local/bin:$PATH"

# Ensure proper Vietnamese IME support on Linux (GNOME / Wayland / X11)
if [ -z "$XMODIFIERS" ]; then
    export XMODIFIERS="@im=ibus"
fi
if [ -z "$GTK_IM_MODULE" ]; then
    export GTK_IM_MODULE="ibus"
fi
if [ -z "$QT_IM_MODULE" ]; then
    export QT_IM_MODULE="ibus"
fi

./mvnw javafx:run
