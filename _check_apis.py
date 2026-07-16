import os, re

backend_apis = {}
src_dir = "src/main/java/com/lsy/propertymanagementsystem"

for root, dirs, files in os.walk(src_dir):
    for f in files:
        if f.endswith("Controller.java"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            
            bm = re.search(r'@RequestMapping\("(/api/[^"]+)"\)', content)
            if not bm:
                bm = re.search(r'@RequestMapping\(value\s*=\s*"(/api/[^"]+)"\)', content)
            base_path = bm.group(1) if bm else ""
            
            for m in re.finditer(r'@(GetMapping|PostMapping|PutMapping|DeleteMapping)\(([^)]*)\)', content):
                ann = m.group()
                http_method = m.group(1).replace("Mapping", "").upper()
                sub = ""
                sm = re.search(r'"(/[^"]*)"', ann)
                if sm:
                    sub = sm.group(1)
                full_path = base_path + sub
                
                idx = m.end()
                method_m = re.search(r'public\s+\S+\s+(\w+)\s*\(', content[idx:idx+200])
                handler = method_m.group(1) if method_m else "?"
                
                backend_apis[full_path] = (http_method, handler)

# Frontend API calls
frontend_apis = {}
api_dir = "frontend/src/api"
for root, dirs, files in os.walk(api_dir):
    for f in files:
        if f.endswith(".js"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            
            for m in re.finditer(r'url:\s*"(/[^"]+)"', content):
                url = m.group(1)
                frontend_apis[url] = os.path.relpath(path, api_dir)
            
            for m in re.finditer(r"url:\s*'([^']+)'", content):
                url = m.group(1)
                frontend_apis[url] = os.path.relpath(path, api_dir)

print("=== 后端有接口但前端未调用 ===")
unmatched = []
for path in sorted(backend_apis.keys()):
    method, handler = backend_apis[path]
    # Normalize: backend path starts with /api, frontend URL also starts with /
    matched = False
    for f_url in frontend_apis:
        if path == f_url or path.rstrip("/") == f_url:
            matched = True
            break
        # Also check template-style URLs
        if "${" in f_url:
            tmpl = re.sub(r'\$\{[^}]+\}', "{id}", f_url)
            path_tmpl = re.sub(r'/\{[^}]+\}', "/{id}", path)
            if tmpl == path_tmpl:
                matched = True
                break
    if not matched:
        unmatched.append((method, path, handler))
        print(f"  {method:7s} {path}  ({handler})")

print(f"\n共 {len(unmatched)} 个后端接口未找到前端调用")

print("\n=== 前端调用了但后端无此接口 ===")
for f_url in sorted(frontend_apis.keys()):
    if "${" in f_url:
        continue
    matched = False
    for b_path in backend_apis:
        if f_url == b_path or f_url.rstrip("/") == b_path:
            matched = True
            break
    if not matched:
        print(f"  {f_url}  -> {frontend_apis[f_url]}")
