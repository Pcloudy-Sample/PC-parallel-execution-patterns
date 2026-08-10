#!/usr/bin/env python3
"""
Slot utilisation report
=======================
Parses simple pCloudy slot log lines and computes active vs idle time.

Expected log format (one event per line):
  <timestamp_iso> <slot_id> <event>
where event is one of: START, END

Example:
  2026-08-10T10:00:00Z slot-1 START
  2026-08-10T10:08:10Z slot-1 END

Usage:
  python tools/slot-utilisation-report.py --log ./slot.log
"""

import argparse
from datetime import datetime
from collections import defaultdict


def parse_ts(value):
    if value.endswith("Z"):
        value = value[:-1] + "+00:00"
    return datetime.fromisoformat(value)


def main():
    parser = argparse.ArgumentParser(
        description="Report pCloudy slot utilisation from START/END logs"
    )
    parser.add_argument("--log", "-l", required=True, help="Path to slot log file")
    args = parser.parse_args()

    active_seconds = defaultdict(float)
    open_start = {}
    first_ts = None
    last_ts = None

    with open(args.log, "r", encoding="utf-8") as handle:
        for line in handle:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            ts_s, slot, event = line.split(maxsplit=2)
            ts = parse_ts(ts_s)
            first_ts = ts if first_ts is None else min(first_ts, ts)
            last_ts = ts if last_ts is None else max(last_ts, ts)

            if event == "START":
                open_start[slot] = ts
            elif event == "END" and slot in open_start:
                active_seconds[slot] += (ts - open_start[slot]).total_seconds()
                del open_start[slot]

    if first_ts is None or last_ts is None:
        print("No valid events found")
        return

    total_window = (last_ts - first_ts).total_seconds()
    if total_window <= 0:
        print("Invalid time window")
        return

    print("\npCloudy Slot Utilisation Report")
    print("=" * 44)
    print(
        f"Window: {first_ts.isoformat()} -> {last_ts.isoformat()} ({total_window:.0f}s)"
    )
    print("-" * 44)
    print(f"{'Slot':<12} {'Active(s)':>10} {'Idle(s)':>10} {'Util(%)':>10}")

    for slot in sorted(set(list(active_seconds.keys()) + list(open_start.keys()))):
        active = active_seconds.get(slot, 0.0)
        idle = max(total_window - active, 0)
        util = (active / total_window) * 100
        print(f"{slot:<12} {active:>10.0f} {idle:>10.0f} {util:>9.1f}")

    print("=" * 44)


if __name__ == "__main__":
    main()
