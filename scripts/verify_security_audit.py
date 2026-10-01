import os
import sys
import re

root = r"d:\ludo"
errors = []

# 1. Manifest
manifest_path = os.path.join(root, "app", "src", "main", "AndroidManifest.xml")
with open(manifest_path, "r", encoding="utf-8") as f:
    mc = f.read()
    exp = re.findall(r'android:exported\s*=\s*"true"', mc)
    if len(exp) > 1:
        errors.append("More than 1 exported activity")
    if "android.intent.action.MAIN" not in mc or "android.intent.category.LAUNCHER" not in mc:
        errors.append("Manifest missing MAIN/LAUNCHER")

# 2. CSPRNG
dice_path = os.path.join(root, "engine", "core", "src", "main", "kotlin", "game", "ludora", "engine", "core", "DiceRoller.kt")
with open(dice_path, "r", encoding="utf-8") as f:
    dc = f.read()
    if "java.security.SecureRandom" not in dc:
        errors.append("DiceRoller does not use SecureRandom")
    if "java.util.Random" in dc:
        errors.append("DiceRoller uses insecure java.util.Random")

room_path = os.path.join(root, "core", "network", "src", "main", "kotlin", "game", "ludora", "core", "network", "util", "RoomCodeGenerator.kt")
with open(room_path, "r", encoding="utf-8") as f:
    rc = f.read()
    if "java.security.SecureRandom" not in rc:
        errors.append("RoomCodeGenerator does not use SecureRandom")

# 3. Report
report_path = os.path.join(root, "docs", "24_SECURITY_AUDIT_REPORT.md")
with open(report_path, "r", encoding="utf-8") as f:
    rep = f.read()
    for cwe in ["CWE-89", "CWE-926", "CWE-330", "CWE-312", "CWE-400"]:
        if cwe not in rep:
            errors.append(f"Report missing {cwe}")

if errors:
    print(f"FAILED with {len(errors)} error(s):", errors, file=sys.stderr)
    sys.exit(1)
else:
    print("ALL SECURITY AUDIT GATES PASSED PERFECTLY!")
    sys.exit(0)
