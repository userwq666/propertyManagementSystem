import os, re

api_dir = "frontend/src/api"
# Find all js files with broken backslash patterns
for root, dirs, files in os.walk(api_dir):
    for f in files:
        if f.endswith(".js"):
            path = os.path.join(root, f)
            with open(path, "rb") as fp:
                raw = fp.read()
            # Look for pattern: \/module/\
            text = raw.decode("utf-8")
            # Find all url: \/xxx/\\, patterns
            # These are broken template literals
            fixed = text
            # Pattern: url: \/xxx/\\, where xxx is the path
            # Replace with proper template literal: url: `/xxx/${id}`,
            matches = re.findall(r"url: \\/(\w+(?:/\w+)*)/\\\\,", text)
            for m in matches:
                last_seg = m.split("/")[-1]
                # Determine variable name from last segment
                # e.g., /community/house -> houseId, /system/user -> userId
                var_map = {
                    "user": "userId",
                    "menu": "menuId",
                    "role": "roleId",
                    "building": "buildingId",
                    "house": "houseId",
                    "owner": "ownerId",
                    "parking": "parkingId",
                    "notice": "noticeId",
                    "record": "recordId",
                    "plan": "planId",
                    "item": "itemId",
                    "suggest": "id",
                }
                var_name = var_map.get(last_seg, "id")
                old = f"url: \\/{m}/\\\\,"
                new = f"url: `/{m}/${{{var_name}}}`,"
                fixed = fixed.replace(old, new)
            
            if fixed != text:
                with open(path, "wb") as fp:
                    fp.write(fixed.encode("utf-8"))
                print(f"Fixed: {path}")
