import os, re

# 1. Collect all backend API endpoints
backend_apis = {}
src_dir = "src/main/java/com/lsy/propertymanagementsystem"
for root, dirs, files in os.walk(src_dir):
    for f in files:
        if f.endswith("Controller.java"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            
            # Get base path from @RequestMapping
            base_match = re.search(r'@RequestMapping\(["\x27](/api/[^"\x27]+)["\x27]\)', content)
            if not base_match:
                base_match = re.search(r'@RequestMapping\(value\s*=\s*["\x27](/api/[^"\x27]+)["\x27]\)', content)
            base_path = base_match.group(1) if base_match else ""
            
            # Get all endpoints
            for m in re.finditer(r'@(GetMapping|PostMapping|PutMapping|DeleteMapping)\([^)]*\)\s*\n\s*public\s+\S+\s+(\w+)\s*\(', content):
                annotation = m.group(1)
                method_name = m.group(2)
                # Extract sub path
                sub_match = re.search(r'@\w+Mapping\(["\x27](/[^"\x27]*)["\x27]\)', content[m.start():m.end()])
                sub_path = sub_match.group(1) if sub_match else ""
                
                full_path = base_path + sub_path
                if full_path not in backend_apis:
                    backend_apis[full_path] = []
                backend_apis[full_path].append(method_name)

print("=== 后端接口列表 ===")
for path in sorted(backend_apis.keys()):
    print(f"  {path}")

# 2. Collect all frontend API calls
print("\n=== 前端 API 调用 ===")
frontend_apis = {}
api_dir = "frontend/src/api"
for root, dirs, files in os.walk(api_dir):
    for f in files:
        if f.endswith(".js"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            
            # Get all request URLs
            for m in re.finditer(r"url:\s*["\x27](/[^"\x27]+)["\x27]", content):
                url = m.group(1)
                if url not in frontend_apis:
                    frontend_apis[url] = []
                frontend_apis[url].append(os.path.relpath(path, api_dir))
            
            # Also check for template literals with ${...}
            for m in re.finditer(r"url:\s*`[^`]*`", content):
                url_template = m.group(0)
                frontend_apis[url_template] = ["template literal in " + os.path.relpath(path, api_dir)]

for path in sorted(frontend_apis.keys()):
    print(f"  {path[:80]}")

# 3. Compare
print("\n=== 后端有但前端没有的接口 ===")
for path in sorted(backend_apis.keys()):
    # Normalize: remove /api prefix for matching
    frontend_path = path.replace("/api", "", 1) if path.startswith("/api") else path
    matched = False
    for f_path in frontend_apis:
        if frontend_path in f_path or frontend_path.rstrip("/") in f_path:
            matched = True
            break
        # Also check template literals
        if "$" in f_path:
            # Template literal - check if the static part matches
            static_part = re.sub(r'\$\{[^}]+\}', '{param}', f_path)
            if frontend_path.replace("{id}", "{param}").replace("{roleId}", "{param}").rstrip("/") in static_part:
                matched = True
                break
    
    if not matched:
        print(f"  {path}")

# Check frontend has but backend doesn't
print("\n=== 前端有但后端没实现的接口 ===")
for f_path in sorted(frontend_apis.keys()):
    if "$" in f_path:
        continue  # Skip template literals
    matched = False
    for b_path in backend_apis:
        if f_path in b_path or f_path.rstrip("/") == b_path.replace("/api", "", 1).rstrip("/"):
            matched = True
            break
    if not matched:
        print(f"  {f_path}  ({frontend_apis[f_path]})")
