#!/usr/bin/env python3
"""Batch search products in Esselunga app via iPhone Mirroring."""

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


def type_key(ch):
    kc = KEYCODES.get(ch.lower())
    if kc is None:
        return
    down = Quartz.CGEventCreateKeyboardEvent(None, kc, True)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, down)
    time.sleep(0.06)
    up = Quartz.CGEventCreateKeyboardEvent(None, kc, False)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, up)
    time.sleep(0.06)


def type_text(text):
    for ch in text:
        type_key(ch)


def press_enter():
    down = Quartz.CGEventCreateKeyboardEvent(None, 36, True)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, down)
    time.sleep(0.05)
    up = Quartz.CGEventCreateKeyboardEvent(None, 36, False)
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, up)
    time.sleep(0.5)


def clear_search(win):
    """Click the X clear button on search bar."""
    click(win["x"] + 270, win["y"] + 68)
    time.sleep(0.3)


def click_search_bar(win):
    """Click on the search bar to focus it."""
    click(win["x"] + 100, win["y"] + 68)
    time.sleep(0.3)


def screenshot(win_id, path):
    subprocess.run(["screencapture", "-x", "-l", str(win_id), path], check=True)


def scroll_down(win):
    """Swipe up to scroll down in results."""
    x = float(win["x"] + win["w"] // 2)
    y_start = float(win["y"] + int(win["h"] * 0.7))
    y_end = float(win["y"] + int(win["h"] * 0.3))

    # Mouse drag for swipe
    start_point = Quartz.CGPointMake(x, y_start)
    end_point = Quartz.CGPointMake(x, y_end)

    down = Quartz.CGEventCreateMouseEvent(
        None, Quartz.kCGEventLeftMouseDown, start_point, Quartz.kCGMouseButtonLeft
    )
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, down)
    time.sleep(0.05)

    # Drag in steps
    steps = 10
    for i in range(1, steps + 1):
        frac = i / steps
        mid_y = y_start + (y_end - y_start) * frac
        mid_point = Quartz.CGPointMake(x, mid_y)
        drag = Quartz.CGEventCreateMouseEvent(
            None, Quartz.kCGEventLeftMouseDragged, mid_point, Quartz.kCGMouseButtonLeft
        )
        Quartz.CGEventPost(Quartz.kCGHIDEventTap, drag)
        time.sleep(0.02)

    up = Quartz.CGEventCreateMouseEvent(
        None, Quartz.kCGEventLeftMouseUp, end_point, Quartz.kCGMouseButtonLeft
    )
    Quartz.CGEventPost(Quartz.kCGHIDEventTap, up)
    time.sleep(1)


def search_and_capture(keyword, output_dir, win):
    """Search a keyword, take screenshot of first page of results."""
    win = get_window()
    if not win:
        print(f"  SKIP {keyword}: window not found")
        return False

    # Click search bar
    click_search_bar(win)
    time.sleep(0.3)

    # Type keyword
    type_text(keyword)
    time.sleep(0.5)

    # Press enter
    press_enter()
    time.sleep(2.5)

    # Screenshot results
    path = os.path.join(output_dir, f"{keyword}.png")
    screenshot(win["id"], path)
    print(f"  OK: {keyword} -> {path}")

    # Clear for next search - click X button
    clear_search(win)
    time.sleep(0.5)

    return True


def main():
    keywords = [
        "pasta", "riso", "latte", "acqua", "olio",
        "caffe", "biscotti", "cereali", "uova", "yogurt",
        "formaggio", "burro", "prosciutto", "pollo", "salmone",
        "surgelati", "gelato", "pane", "vino", "birra",
        "succo", "detersivo", "shampoo", "pannolini", "cibo gatto",
    ]

    output_dir = "/tmp/esselunga_search"
    os.makedirs(output_dir, exist_ok=True)

    # Focus app
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
    print(f"Searching {len(keywords)} keywords...")

    # Click search bar first
    click_search_bar(win)
    time.sleep(0.5)

    for kw in keywords:
        search_and_capture(kw, output_dir, win)

    print(f"\nDone! Screenshots saved to {output_dir}/")


if __name__ == "__main__":
    main()
