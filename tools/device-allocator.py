#!/usr/bin/env python3
"""
pCloudy Device Allocator
=========================
Given your test count and available pCloudy slots, recommends:
  1. Which parallel pattern to use
  2. How to split tests across slots
  3. Expected execution ratio and wall-clock time

Usage:
  python device-allocator.py --tests 200 --slots 5
  python device-allocator.py --tests 200 --slots 5 --avg-test-time 45
  python device-allocator.py --junit ./results/ --slots 5
"""

import argparse
import glob
import math
import sys
import xml.etree.ElementTree as ET


def parse_junit_for_counts(results_dir: str):
    """Extract test count and avg duration from JUnit XML."""
    xml_files = glob.glob(f"{results_dir}/**/*.xml", recursive=True)
    total_tests = 0
    total_time  = 0.0

    for f in xml_files:
        try:
            root = ET.parse(f).getroot()
            suites = root.findall('.//testsuite') if root.tag == 'testsuites' else [root]
            for s in suites:
                total_tests += int(s.get('tests', 0))
                total_time  += float(s.get('time', 0))
        except Exception:
            continue

    avg_time = (total_time / total_tests) if total_tests > 0 else 45.0
    return total_tests, avg_time


def recommend_pattern(test_count: int, slots: int, avg_test_time_sec: float):
    """Recommend the best parallel pattern and show the math."""

    tests_per_slot      = math.ceil(test_count / slots)
    wall_clock_min      = (tests_per_slot * avg_test_time_sec) / 60
    execution_ratio_rr  = min(100, (slots * tests_per_slot / test_count) * 100)
    # By-class typically has one large class — assume worst-case 30% imbalance
    wall_clock_by_class = wall_clock_min * 1.3
    utilisation_by_class = (100 / 1.3)

    print()
    print("=" * 65)
    print("  pCloudy Device Allocator — Parallel Pattern Recommendation")
    print("=" * 65)
    print(f"  Test count          : {test_count}")
    print(f"  Available slots     : {slots}")
    print(f"  Avg test duration   : {avg_test_time_sec:.1f}s")
    print(f"  Tests per slot      : {tests_per_slot}")
    print("-" * 65)

    print()
    print("  Pattern comparison:")
    print(f"  {'Pattern':<28} {'Wall clock':>12} {'Slot util.':>12}")
    print("  " + "-" * 55)

    patterns = [
        ("01 By Test Class",    f"{wall_clock_by_class:.0f} min", f"{utilisation_by_class:.0f}%"),
        ("02 By Feature Module", f"{wall_clock_by_class:.0f} min", f"{utilisation_by_class:.0f}%"),
        ("03 By Device Type",   f"{wall_clock_min * test_count / tests_per_slot:.0f} min",
                                f"100% (coverage)"),
        ("04 Round-Robin",      f"{wall_clock_min:.0f} min",      "94–100%"),
    ]
    for name, wc, util in patterns:
        print(f"  {name:<28} {wc:>12} {util:>12}")

    print()

    # Recommendation logic
    if test_count > 100 and slots >= 3:
        rec = "04 Round-Robin"
        reason = "large suite + multiple slots = round-robin maximises utilisation"
    elif test_count <= 50:
        rec = "01 By Test Class"
        reason = "small suite — simplest pattern is sufficient"
    elif slots == 1:
        rec = "N/A — add more slots"
        reason = "single slot cannot parallelise"
    else:
        rec = "02 By Feature Module"
        reason = "medium suite — feature isolation gives clearer failure signals"

    print(f"  Recommendation: Pattern {rec}")
    print(f"  Reason:         {reason}")
    print()

    # Round-robin split
    print("  Round-Robin slot allocation:")
    for i in range(1, slots + 1):
        start = (i - 1) * tests_per_slot + 1
        end   = min(i * tests_per_slot, test_count)
        print(f"    Slot {i}: tests {start}–{end}  ({end - start + 1} tests, "
              f"~{((end - start + 1) * avg_test_time_sec / 60):.0f} min)")

    print()
    print(f"  Expected execution ratio (round-robin): {execution_ratio_rr:.1f}%")
    if execution_ratio_rr >= 90:
        print("  ✅ Above 90% target")
    else:
        print(f"  ⚠️  Below 90% target — add {math.ceil((test_count * 0.9 - slots * tests_per_slot) / tests_per_slot)} more slot(s)")
    print("=" * 65)
    print()


def main():
    parser = argparse.ArgumentParser(
        description='Recommend a pCloudy parallel execution pattern'
    )
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--tests', '-t', type=int, help='Total number of tests')
    group.add_argument('--junit', '-j', help='Directory with JUnit XML results (auto-counts)')

    parser.add_argument('--slots',          '-s', type=int,   required=True, help='pCloudy parallel slot count')
    parser.add_argument('--avg-test-time',  '-a', type=float, default=45.0,
                        help='Average test duration in seconds (default: 45)')

    args = parser.parse_args()

    if args.junit:
        test_count, avg_time = parse_junit_for_counts(args.junit)
        print(f"Parsed {test_count} tests from JUnit XML, avg duration {avg_time:.1f}s")
    else:
        test_count = args.tests
        avg_time   = args.avg_test_time

    recommend_pattern(test_count, args.slots, avg_time)


if __name__ == '__main__':
    main()
