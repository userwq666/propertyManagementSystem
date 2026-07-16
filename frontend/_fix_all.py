import os, re

# Functions to remove from imports and usages
# Format: { "view_relative_path": [(func_name, has_handleExport_function), ...] }
# has_handleExport_function: True means there is a handleExport function using this

issues = {
    "property/building/index.vue": ["changeBuildingStatus", "exportBuilding"],
    "property/house/index.vue": ["changeHouseStatus", "exportHouse", "getBuildingTree"],
    "property/owner/index.vue": ["changeOwnerStatus", "exportOwner", "getHouseTree"],
    "property/parking/index.vue": ["changeParkingStatus", "exportParking", "getBuildingTree", "getHouseTree"],
    "announcement/notice/index.vue": ["toggleTopNotice", "getNoticeStats", "getNoticeReadTrend", "exportNotice"],
    "fee/item/index.vue": ["changeChargeItemStatus", "exportChargeItem"],
    "fee/notice/index.vue": ["sendNotice", "getSendDetail", "markReadStatus", "exportNotice", "getHouseTree"],
    "inspection/plan/index.vue": ["changeInspectionPlanStatus", "exportInspectionPlan", "getBuildingTree", "getEquipmentTree"],
    "inspection/record/index.vue": ["executeInspectionRecord", "handleAbnormal", "exportInspectionRecord", "uploadImage"],
    "repair/order/index.vue": ["cancelRepairOrder", "dispatchRepairOrder", "processRepairOrder", "finishRepairOrder", "replyEvaluate", "exportRepairOrder", "getHouseTree", "uploadImage"],
    "system/menu/index.vue": [],
}

# Also add fee/record if needed
for root, dirs, files in os.walk("src/views"):
    for f in files:
        if f.endswith(".vue"):
            path = os.path.join(root, f)
            rel_path = os.path.relpath(path, "src/views")
            if rel_path not in issues:
                issues[rel_path] = []

def remove_handle_export(content):
    """Remove handleExport function from content"""
    for prefix in ["const handleExport", "// 导出\nconst handleExport"]:
        idx = content.find(prefix)
        if idx >= 0:
            start = content.rfind("\n", 0, idx)
            if start < 0:
                start = 0
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
            return content[:start] + content[end:]
    return content

def remove_imports(content, func_names):
    """Remove specific functions from import statements"""
    lines = content.split("\n")
    result = []
    skip_until = -1
    has_menu_vue_fix = False
    
    for i, line in enumerate(lines):
        # Check if this line has an import with any of the functions
        if "import {" in line and "from "@/api/" in line or "} from '@/api/" in line:
            continue
        if skip_until > 0 and i < skip_until:
            continue
        
        # Detect import block start
        if "import {" in line and "@/api/" in line:
            # Single line import
            for name in func_names:
                if name in line:
                    line = line.replace(", " + name, "").replace(name + ", ", "").replace(name, "")
                    # If the line becomes just "import { } from ...", remove it
                    if "import { } from" in line or "import {\n} from" in line:
                        line = ""
            if line:
                result.append(line)
            continue
        
        # Multi-line import block
        if line.strip().startswith("import {") and not "@/api/" in line:
            # Start of multi-line import
            block_lines = [line]
            for j in range(i+1, len(lines)):
                block_lines.append(lines[j])
                if lines[j].strip().endswith("} from '@/api/") or "'" in lines[j] and "@/api/" in lines[j]:
                    break
            
            # Check if any function name in this block needs removal
            modified = False
            new_block = []
            for bl in block_lines:
                new_line = bl
                for name in func_names:
                    if name in new_line:
                        new_line = new_line.replace(name + ",\n", "").replace(name + ",", "").replace(name, "")
                        modified = True
                if new_line.strip():
                    new_block.append(new_line)
            
            if modified:
                for bl in new_block:
                    result.append(bl)
                # Skip to end of block
                skip_until = i + len(block_lines)
                continue
            else:
                result.append(line)
                continue
        
        result.append(line)
    
    # Remove empty import blocks
    text = "\n".join(result)
    text = re.sub(r'import \{\s*\} from [^\n]+\n', '', text)
    # Also remove empty multi-line imports
    text = re.sub(r'import \{\n\s*\n\} from [^\n]+\n', '', text)
    # Remove empty lines from within import blocks
    text = re.sub(r'import \{\n\s*\n\s+', 'import {\n  ', text)
    
    return text

print("Fixing all views...")

for rel_path, funcs in issues.items():
    full_path = os.path.join("src/views", rel_path)
    if not os.path.exists(full_path):
        continue
    
    with open(full_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    original = content
    changed = False
    
    # Remove handleExport if exportXxx functions are present
    for func in funcs:
        if func.startswith("export") and func in content:
            content = remove_handle_export(content)
            if content != original:
                print(f"  {rel_path}: removed handleExport")
                changed = True
            break
    
    # Remove function imports
    for func in funcs:
        if func in content:
            # Also remove usages of these functions
            # For handleXxx function calls
            pass
    
    # For system/menu, already fixed
    if rel_path == "system/menu/index.vue":
        # Check if getMenuTreeselect and exportMenu still present
        if "getMenuTreeselect" in content or "exportMenu" in content:
            content = remove_imports(content, ["getMenuTreeselect", "exportMenu"])
            content = remove_handle_export(content)
            changed = True
            print(f"  {rel_path}: fixed menu imports")

# This approach is too complex. Let me use a simpler one.
print("Using simpler approach...")
