#!/usr/bin/env python3
"""Automate iPhone Mirroring interactions using Quartz CGEvents."""

import Quartz
import time
import subprocess
import sys


def get_window():
    """Get iPhone Mirroring window bounds."""
    windows = Quartz.CGWindowListCopyWindowInfo(
        Quartz.kCGWindowListOptionOnScreenOnly, Quartz.kCGNullWindowID
    )
    for w in windows:
        if w.get("kCGWindowOwnerName") == "iPhone Mirroring":
            bounds = w.get("kCGWindowBounds", {})
            return {
                "x": int(bounds.get("X", 0)),
                "y": int(bounds.get("Y", 0)),
                "w": int(bounds.get("Width", 0)),
                "h": int(bounds.get("Height", 0)),
                "id": int(w.get("kCGWindowNumber", 0)),
            }
    return None


def click(x, y):
    """Click at absolute screen coordinates using CGEvents."""
    point = Quartz.CGPointMake(float(x), float(y))
    down = Quartz.CGEventCreateMouseEvent(
        None, Quartz.kCGEventLeftMouseDown, point, Quartz.kCGMouseButtonLeft
    )
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, down)
    time.sleep(0.1)
    up = Quartz.CGEventCreateMouseEvent(
        None, Quartz.kCGEventLeftMouseUp, point, Quartz.kCGMouseButtonLeft
    )
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, up)
    time.sleep(0.3)


def type_text(text):
    """Type text using AppleScript (handles Unicode)."""
    escaped = text.replace("\\", "\\\\").replace('"', '\\"')
    subprocess.run(
        ["osascript", "-e", f'tell application "System Events" to keystroke "{escaped}"'],
        capture_output=True,
    )
    time.sleep(0.3)


def press_key(key_code):
    """Press a key by keycode using CGEvents."""
    down = Quartz.CGEventCreateKeyboardEvent(None, key_code, True)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, down)
    time.sleep(0.05)
    up = Quartz.CGEventCreateKeyboardEvent(None, key_code, False)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, up)
    time.sleep(0.3)


def press_enter():
    press_key(36)  # Return key


def press_delete_all():
    """Select all text and delete."""
    # Cmd+A
    event = Quartz.CGEventCreateKeyboardEvent(None, 0, True)  # 'a' key
    Quartz.CGEventSetFlags(event, Quartz.kCGEventFlagMaskCommand)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, event)
    time.sleep(0.05)
    event = Quartz.CGEventCreateKeyboardEvent(None, 0, False)
    Quartz.CGEventSetFlags(event, Quartz.kCGEventFlagMaskCommand)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, event)
    time.sleep(0.2)
    # Delete
    press_key(51)  # Backspace
    time.sleep(0.2)


def screenshot(win_id, path):
    """Take screenshot of window."""
    subprocess.run(["screencapture", "-x", "-l", str(win_id), path], check=True)


def focus_app():
    subprocess.run(
        ["osascript", "-e", 'tell application "iPhone Mirroring" to activate'],
        capture_output=True,
    )
    time.sleep(0.5)


def search_product(keyword, win, output_path):
    """Search for a product keyword and take a screenshot of results."""
    focus_app()

    # Refresh window position
    win = get_window()
    if not win:
        print("Window not found!")
        return False

    # Click on search bar (center-x, ~70px from top of window)
    search_x = win["x"] + win["w"] // 2
    search_y = win["y"] + 70
    click(search_x, search_y)
    time.sleep(0.3)

    # Select all existing text and delete
    press_delete_all()
    time.sleep(0.3)

    # Type keyword
    type_text(keyword)
    time.sleep(0.5)

    # Press enter to search
    press_enter()
    time.sleep(2)  # Wait for results

    # Screenshot
    screenshot(win["id"], output_path)
    print(f"Searched '{keyword}' -> {output_path}")
    return True


if __name__ == "__main__":
    keyword = sys.argv[1] if len(sys.argv) > 1 else "pasta"
    output = sys.argv[2] if len(sys.argv) > 2 else "/tmp/im_search.png"

    win = get_window()
    if not win:
        print("iPhone Mirroring not found!")
        sys.exit(1)

    print(f"Window: {win}")
    search_product(keyword, win, output)
