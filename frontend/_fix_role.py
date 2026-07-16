import sys

path = "src/views/system/role/index.vue"
with open(path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Fix imports
old_imports = "import {\n  getRoleList,\n  getRoleInfo,\n  addRole,\n  updateRole,\n  deleteRole,\n  getDeptTree,\n  exportRole,\n  getRoleMenuTreeselect\n} from '@/api/system/role'\nimport { usePermission } from '@/hooks/usePermission'"
new_imports = "import {\n  getRoleList,\n  getRoleInfo,\n  addRole,\n  updateRole,\n  deleteRole,\n  getRoleMenuIds\n} from '@/api/system/role'\nimport { getMenuTree } from '@/api/system/menu'\nimport { usePermission } from '@/hooks/usePermission'"

content = content.replace(old_imports, new_imports)
print("1. Fixed imports OK")

# 2. Fix getRoleListData -> getMenuTreeData
content = content.replace("getRoleListData", "getMenuTreeData")
content = content.replace("getRoleList({})", "getMenuTree({})")
print("2. Fixed function name OK")

# 3. Remove getDeptTreeData function
idx = content.find("\u83b7\u53d6\u90e8\u95e8\u6811")
if idx < 0:
    idx = content.find("获取部门树")
if idx >= 0:
    start = content.rfind("\n", 0, idx)
    end = content.find("}", idx)
    end = content.find("}", end + 1) + 1
    while end < len(content) and content[end] in "\n\r":
        end += 1
    content = content[:start] + content[end:]
    print("3. Removed getDeptTreeData OK")
else:
    print("3. getDeptTreeData not found")

# 4. Remove await getDeptTreeData() calls
content = content.replace("await getDeptTreeData()", "")
content = content.replace("  \n  ", "  ")
print("4. Cleaned up calls")

# 5. Fix handleAssignPerm
content = content.replace(
    "const res = await getRoleMenuTreeselect(row.roleId)\n    const data = res.data || res\n    assignMenuTreeData.value = data.menus || data.menuTree || []\n    assignPermForm.menuIds = data.checkedKeys || data.menuIds || []",
    "const [menuRes, roleMenuRes] = await Promise.all([\n      getMenuTree({}),\n      getRoleMenuIds(row.roleId)\n    ])\n    assignMenuTreeData.value = menuRes.data || menuRes || []\n    assignPermForm.menuIds = roleMenuRes.data || roleMenuRes || []"
)
print("5. Fixed handleAssignPerm OK")

# 6. Remove handleExport function
idx = content.find("// 导出\nconst handleExport")
if idx < 0:
    idx = content.find("const handleExport")
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
    print("6. Removed handleExport OK")
else:
    print("6. handleExport not found")

with open(path, "w", encoding="utf-8") as f:
    f.write(content)

print("\nAll fixes applied!")
