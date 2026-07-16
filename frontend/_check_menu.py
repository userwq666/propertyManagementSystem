import re

# Fix system/menu/index.vue - getMenuTreeselect and exportMenu
with open("src/views/system/menu/index.vue", "r", encoding="utf-8") as f:
    content = f.read()

# Check if getMenuTreeselect and exportMenu are used
for name in ["getMenuTreeselect", "exportMenu"]:
    count = content.count(name)
    print(f"{name}: appears {count} times")
    for i, line in enumerate(content.split("\n"), 1):
        if name in line:
            print(f"  L{i}: {line.strip()}")

# Also check if getMenuTreeselect is used beyond the import
print()
print("Done checking menu view")
