#!/usr/bin/env python3
"""
pCloudy Execution Ratio Calculator
===================================
Reads JUnit XML test result files and computes the execution ratio:
  ratio = tests_executed / tests_scheduled * 100

Usage:
  python execution-ratio-calculator.py --results ./results/
  python execution-ratio-calculator.py --results ./results/ --threshold 90
  python execution-ratio-calculator.py --results ./results/ --format json

Exit codes:
  0 — ratio meets or exceeds threshold
  1 — ratio below threshold (use in CI to fail the build)
"""

import argparse
import glob
import json
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def parse_junit_xml(file_path: str) -> dict:
    """Parse a JUnit XML file and return test counts."""
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()

        # Handle both <testsuite> and <testsuites> root elements
        suites = (
            root.findall('.//testsuite')
            if root.tag == 'testsuites'
            else [root]
        )

        total     = 0
        executed  = 0
        passed    = 0
        failed    = 0
        skipped   = 0
        errors    = 0

        for suite in suites:
            suite_total   = int(suite.get('tests',    0))
            suite_skipped = int(suite.get('skipped',  0))
            suite_failed  = int(suite.get('failures', 0))
            suite_errors  = int(suite.get('errors',   0))

            total    += suite_total
            skipped  += suite_skipped
            failed   += suite_failed
            errors   += suite_errors
            executed += suite_total - suite_skipped
            passed   += suite_total - suite_skipped - suite_failed - suite_errors

        return {
            'file':     file_path,
            'total':    total,
            'executed': executed,
            'passed':   passed,
            'failed':   failed,
            'skipped':  skipped,
            'errors':   errors,
        }

    except Exception as e:
        print(f"  Warning: could not parse {file_path}: {e}", file=sys.stderr)
        return None


def calculate_ratio(results_dir: str, threshold: float, output_format: str) -> int:
    """Calculate execution ratio from all JUnit XML files in a directory."""

    xml_files = glob.glob(f"{results_dir}/**/*.xml", recursive=True)
    if not xml_files:
        print(f"No JUnit XML files found in: {results_dir}")
        return 1

    all_results = []
    totals = {'total': 0, 'executed': 0, 'passed': 0, 'failed': 0,
              'skipped': 0, 'errors': 0}

    for f in sorted(xml_files):
        result = parse_junit_xml(f)
        if result:
            all_results.append(result)
            for key in totals:
                totals[key] += result[key]

    if totals['total'] == 0:
        print("No tests found in XML files.")
        return 1

    ratio = (totals['executed'] / totals['total'] * 100) if totals['total'] > 0 else 0

    if output_format == 'json':
        output = {
            'execution_ratio':    round(ratio, 2),
            'threshold':          threshold,
            'meets_threshold':    ratio >= threshold,
            'totals':             totals,
            'files_parsed':       len(all_results),
            'per_file':           all_results,
        }
        print(json.dumps(output, indent=2))
    else:
        # Table output
        print()
        print("=" * 60)
        print("  pCloudy Execution Ratio Report")
        print("=" * 60)
        print(f"  Results directory : {results_dir}")
        print(f"  Files parsed      : {len(all_results)}")
        print("-" * 60)
        print(f"  Tests scheduled   : {totals['total']:>6}")
        print(f"  Tests executed    : {totals['executed']:>6}")
        print(f"  Tests passed      : {totals['passed']:>6}")
        print(f"  Tests failed      : {totals['failed']:>6}")
        print(f"  Tests skipped     : {totals['skipped']:>6}")
        print("-" * 60)

        status_icon = "✅" if ratio >= threshold else ("⚠️ " if ratio >= 70 else "❌")
        print(f"  Execution ratio   : {ratio:>6.1f}%  {status_icon}")
        print(f"  Threshold         : {threshold:>6.1f}%")
        print()

        if ratio >= threshold:
            print(f"  PASS — ratio meets the >{threshold:.0f}% target")
        elif ratio >= 70:
            print(f"  WARNING — ratio {ratio:.1f}% is below {threshold:.0f}% target")
            print("  → Consider adding parallel slots or switching to round-robin pattern")
        else:
            print(f"  CRITICAL — ratio {ratio:.1f}% indicates a pipeline bottleneck")
            print("  → You are generating faster than you can execute")
            print("  → See: parallel-execution-patterns/patterns/04-round-robin/")

        print("=" * 60)
        print()

        # Per-file breakdown
        if len(all_results) > 1:
            print("  Per-file breakdown:")
            print(f"  {'File':<40} {'Sched':>6} {'Exec':>6} {'Ratio':>7}")
            print("  " + "-" * 62)
            for r in all_results:
                r_ratio = (r['executed'] / r['total'] * 100) if r['total'] > 0 else 0
                short_name = Path(r['file']).name
                print(f"  {short_name:<40} {r['total']:>6} {r['executed']:>6} {r_ratio:>6.1f}%")
            print()

    return 0 if ratio >= threshold else 1


def main():
    parser = argparse.ArgumentParser(
        description='Calculate execution ratio from JUnit XML test results'
    )
    parser.add_argument(
        '--results', '-r',
        default='./results',
        help='Directory containing JUnit XML files (default: ./results)'
    )
    parser.add_argument(
        '--threshold', '-t',
        type=float,
        default=90.0,
        help='Minimum acceptable execution ratio %% (default: 90)'
    )
    parser.add_argument(
        '--format', '-f',
        choices=['table', 'json'],
        default='table',
        help='Output format (default: table)'
    )

    args = parser.parse_args()
    sys.exit(calculate_ratio(args.results, args.threshold, args.format))


if __name__ == '__main__':
    main()
