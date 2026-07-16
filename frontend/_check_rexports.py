import os

for f in ["api/repair.js", "api/fee.js", "api/property.js", "api/complaint.js", "api/notice.js", "api/system.js"]:
    path = "src/" + f
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as fh:
            content = fh.read()
        print(f"{f}:")
        print(content[:300])
        print("---")
