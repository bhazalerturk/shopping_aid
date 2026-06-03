#!/usr/bin/env python3
"""Batch search products in Esselunga app via iPhone Mirroring.
Uses backspace to clear previous text before typing new keyword."""

import Quartz
import time
import subprocess
import sys
import os

KEYCODES = {
    'a': 0, 'b': 11, 'c': 8, 'd': 2, 'e': 14, 'f': 3, 'g': 5, 'h': 4,
    'i': 34, 'j': 38, 'k': 40, 'l': 37, 'm': 46, 'n': 45, 'o': 31,
    'p': 35, 'q': 12, 'r': 15, 's': 1, 't': 17, 'u': 32, 'v': 9,
    'w': 13, 'x': 7, 'y': 16, 'z': 6, ' ': 49,
}


def get_window():
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
    time.sleep(0.5)


def press_key(keycode):
    down = Quartz.CGEventCreateKeyboardEvent(None, keycode, True)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, down)
    time.sleep(0.04)
    up = Quartz.CGEventCreateKeyboardEvent(None, keycode, False)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, up)
    time.sleep(0.04)


def type_text(text):
    for ch in text:
        kc = KEYCODES.get(ch.lower())
        if kc is not None:
            press_key(kc)


def backspace(count):
    """Press backspace N times to delete characters."""
    for _ in range(count):
        press_key(51)  # backspace keycode


def press_enter():
    press_key(36)


def screenshot(win_id, path):
    subprocess.run(["screencapture", "-x", "-l", str(win_id), path], check=True)


def click_search_bar(win):
    click(win["x"] + 100, win["y"] + 68)
    time.sleep(0.3)


def main():
    keywords = [
        "pasta", "riso", "latte", "acqua", "olio",
        "caffe", "biscotti", "cereali", "uova", "yogurt",
        "formaggio", "burro", "prosciutto", "pollo", "salmone",
        "surgelati", "gelato", "pane", "vino", "birra",
        "succo", "detersivo", "shampoo", "pannolini",
    ]

    output_dir = "/tmp/esselunga_search"
    os.makedirs(output_dir, exist_ok=True)

    subprocess.run(
        ["osascript", "-e", 'tell application "iPhone Mirroring" to activate'],
        capture_output=True,
    )
    time.sleep(1)

    win = get_window()
    if not win:
        print("iPhone Mirroring not found!")
        sys.exit(1)

    print(f"Window: {win}")
    print(f"Searching {len(keywords)} keywords...\n")

    # Click search bar to focus
    click_search_bar(win)
    time.sleep(0.5)

    prev_len = 0  # track how many chars to delete

    for kw in keywords:
        win = get_window()
        if not win:
            print(f"  SKIP {kw}: window lost")
            # Try to refocus
            subprocess.run(
                ["osascript", "-e", 'tell application "iPhone Mirroring" to activate'],
                capture_output=True,
            )
            time.sleep(1)
            win = get_window()
            if not win:
                print(f"  FAIL {kw}: can't recover")
                continue

        # Delete previous keyword by pressing backspace
        if prev_len > 0:
            backspace(prev_len)
            time.sleep(0.3)

        # Type new keyword
        type_text(kw)
        time.sleep(0.5)

        # Press enter to search
        press_enter()
        time.sleep(2.5)

        # Screenshot
        path = os.path.join(output_dir, f"{kw}.png")
        screenshot(win["id"], path)
        print(f"  [{keywords.index(kw)+1}/{len(keywords)}] {kw}")

        # RE-CLICK search bar to refocus it (search submission loses focus)
        click_search_bar(win)
        time.sleep(0.5)

        # Remember length for next delete
        prev_len = len(kw)

    print(f"\nDone! Screenshots in {output_dir}/")


if __name__ == "__main__":
    main()
