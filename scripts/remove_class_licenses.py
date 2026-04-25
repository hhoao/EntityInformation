#!/usr/bin/env python3
import argparse
import pathlib
import re
import sys


IGNORED_DIRS = {".git", ".gradle", ".idea", "build", "out", "target"}
LICENSE_HEADER_RE = re.compile(
    r"\A(?:\ufeff)?[ \t\r\n]*/\*.*?Mozilla Public License.*?\*/[ \t\r\n]*",
    re.DOTALL,
)


def remove_leading_license(source):
    match = LICENSE_HEADER_RE.match(source)
    if not match:
        return source, False
    return source[match.end() :], True


def iter_java_files(roots):
    for root in roots:
        root_path = pathlib.Path(root)
        if root_path.is_file():
            if root_path.suffix == ".java":
                yield root_path
            continue

        for path in root_path.rglob("*.java"):
            if any(part in IGNORED_DIRS for part in path.parts):
                continue
            yield path


def clean_file(path, dry_run):
    source = path.read_text(encoding="utf-8")
    cleaned, changed = remove_leading_license(source)
    if changed and not dry_run:
        path.write_text(cleaned, encoding="utf-8")
    return changed


def parse_args(argv):
    parser = argparse.ArgumentParser(
        description="Remove leading Mozilla Public License blocks from Java class files."
    )
    parser.add_argument(
        "roots",
        nargs="*",
        default=["."],
        help="Files or directories to scan. Defaults to the current directory.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="List files that would change without writing them.",
    )
    return parser.parse_args(argv)


def main(argv=None):
    args = parse_args(argv or sys.argv[1:])
    changed_paths = []

    for path in iter_java_files(args.roots):
        if clean_file(path, args.dry_run):
            changed_paths.append(path)

    action = "Would clean" if args.dry_run else "Cleaned"
    for path in changed_paths:
        print(path)
    print(f"{action} {len(changed_paths)} Java file(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
