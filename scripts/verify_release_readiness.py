import os
import sys

project_root = r"d:\ludo"
locales = ["en-US", "es-ES", "fr-FR", "de-DE", "hi-IN"]

errors = []

for loc in locales:
    p_title = os.path.join(project_root, "fastlane", "metadata", "android", loc, "title.txt")
    if not os.path.exists(p_title):
        errors.append(f"Missing title for {loc}")
    else:
        with open(p_title, "r", encoding="utf-8") as f:
            t = f.read().strip()
            if len(t) == 0 or len(t) > 30:
                errors.append(f"Title length {len(t)} invalid for {loc}: '{t}'")

    p_short = os.path.join(project_root, "fastlane", "metadata", "android", loc, "short_description.txt")
    if not os.path.exists(p_short):
        errors.append(f"Missing short description for {loc}")
    else:
        with open(p_short, "r", encoding="utf-8") as f:
            s = f.read().strip()
            if len(s) == 0 or len(s) > 80:
                errors.append(f"Short description length {len(s)} invalid for {loc}: '{s}'")

    p_full = os.path.join(project_root, "fastlane", "metadata", "android", loc, "full_description.txt")
    if not os.path.exists(p_full):
        errors.append(f"Missing full description for {loc}")
    else:
        with open(p_full, "r", encoding="utf-8") as f:
            fd = f.read().strip()
            if len(fd) == 0 or len(fd) > 4000:
                errors.append(f"Full description length {len(fd)} invalid for {loc}")

# Check whatsnew
wn = os.path.join(project_root, "distribution", "whatsnew", "whatsnew-en-US")
if not os.path.exists(wn):
    errors.append("Missing whatsnew-en-US")
else:
    with open(wn, "r", encoding="utf-8") as f:
        c = f.read()
        if "1.0.0" not in c:
            errors.append("whatsnew missing 1.0.0")

# Check privacy policy
pp = os.path.join(project_root, "docs", "22_PRIVACY_POLICY.md")
if not os.path.exists(pp):
    errors.append("Missing 22_PRIVACY_POLICY.md")
else:
    with open(pp, "r", encoding="utf-8") as f:
        pt = f.read()
        for sec in [
            "Offline Gameplay Data",
            "Online Multiplayer Data",
            "Advertising Data",
            "Children's Privacy",
            "Data Retention and Deletion",
        ]:
            if sec not in pt:
                errors.append(f"Privacy policy missing section: {sec}")

# Check terms
tos = os.path.join(project_root, "docs", "23_TERMS_OF_SERVICE.md")
if not os.path.exists(tos):
    errors.append("Missing 23_TERMS_OF_SERVICE.md")
else:
    with open(tos, "r", encoding="utf-8") as f:
        tt = f.read()
        for sec in [
            "Acceptance of Terms",
            "Fair Play and Code of Conduct",
            "Virtual Currency and Cosmetic Items",
            "In-App Purchases",
        ]:
            if sec not in tt:
                errors.append(f"Terms missing section: {sec}")

# Check checklist
rc = os.path.join(project_root, "distribution", "release_checklist.md")
if not os.path.exists(rc):
    errors.append("Missing release_checklist.md")
else:
    with open(rc, "r", encoding="utf-8") as f:
        rct = f.read()
        for g in range(1, 8):
            if f"Gate {g}:" not in rct:
                errors.append(f"Checklist missing Gate {g}")

# Check proguard rules
pr = os.path.join(project_root, "app", "proguard-rules.pro")
if not os.path.exists(pr):
    errors.append("Missing proguard-rules.pro")
else:
    with open(pr, "r", encoding="utf-8") as f:
        prt = f.read()
        for req in [
            "LineNumberTable",
            "game.ludora.core.model.**",
            "game.ludora.engine.core.**",
            "kotlinx.serialization",
            "androidx.room",
        ]:
            if req not in prt:
                errors.append(f"Proguard rules missing requirement: {req}")

# Check app build.gradle.kts
bg = os.path.join(project_root, "app", "build.gradle.kts")
with open(bg, "r", encoding="utf-8") as f:
    bgt = f.read()
    for req in [
        "versionCode = 100",
        'versionName = "1.0.0"',
        "isMinifyEnabled = true",
        "isShrinkResources = true",
        "signingConfigs",
    ]:
        if req not in bgt:
            errors.append(f"build.gradle.kts missing requirement: {req}")

# Check manifest
am = os.path.join(project_root, "app", "src", "main", "AndroidManifest.xml")
with open(am, "r", encoding="utf-8") as f:
    amt = f.read()
    for req in [
        "android.permission.VIBRATE",
        "android.permission.INTERNET",
        "android.permission.ACCESS_NETWORK_STATE",
    ]:
        if req not in amt:
            errors.append(f"AndroidManifest.xml missing permission: {req}")

if errors:
    print(f"FAILED with {len(errors)} error(s):", file=sys.stderr)
    for e in errors:
        print(f" - {e}", file=sys.stderr)
    sys.exit(1)
else:
    print("ALL 7 RELEASE READINESS CHECKS PASSED PERFECTLY!")
    sys.exit(0)
