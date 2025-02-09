#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo "Rebooting emulator..."
adb -e reboot
$DIR/wait_for_emulator.sh
echo "Emulator rebooted!"