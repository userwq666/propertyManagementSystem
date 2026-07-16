with open("src/views/system/menu/index.vue", "r", encoding="utf-8") as f:
    content = f.read()

# 1. Remove getMenuTreeselect from imports
content = content.replace("  getMenuTreeselect,\n  exportMenu", "  exportMenu")
content = content.replace("  getMenuTreeselect,", "")
content = content.replace("getMenuTreeselect,", "")

# 2. Remove exportMenu from imports and its usage
content = content.replace("  exportMenu\n} from ", "} from ")

# 3. Remove handleExport function
idx = content.find("const handleExport = async () => {")
if idx >= 0:
    start = content.rfind("\n", 0, idx)
    brace_count = 0
    found_first = False
    end = start
    for i in range(start, len(content)):
        c = content[i]
        if c == "{":
            brace_count += 1
            found_first = True
        elif c == "}":
            brace_count -= 1
            if found_first and brace_count == 0:
                end = i + 1
                break
    while end < len(content) and content[end] in "\n\r":
        end += 1
    content = content[:start] + content[end:]
    print("Removed handleExport OK")

with open("src/views/system/menu/index.vue", "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed system/menu/index.vue")
