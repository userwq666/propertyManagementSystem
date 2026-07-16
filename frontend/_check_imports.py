import os, re, sys

# Check all API files for exports
api_exports = {}
for root, dirs, files in os.walk("src/api"):
    for f in files:
        if f.endswith(".js"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            exports = re.findall(r"export function (\w+)", content)
            rel_path = os.path.relpath(path, "src/api").replace("\\", "/")
            api_exports[rel_path] = exports
            print(f"API: {rel_path} -> {exports}")

print()
found_issues = False

# Check each view for imports
for root, dirs, files in os.walk("src/views"):
    for f in files:
        if f.endswith(".vue"):
            path = os.path.join(root, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            
            imports = re.findall(r"import \{\s*([^}]+)\s*\} from '@\/api\/([^']+)'", content)
            for names, api_path in imports:
                full_path = api_path + ".js"
                if full_path in api_exports:
                    for name in names.split(","):
                        name = name.strip()
                        if name and name not in api_exports[full_path]:
                            rel = os.path.relpath(path, "src/views")
                            print(f"ISSUE: {rel} imports '{name}' but {full_path} doesn't export it")
                            found_issues = True

if not found_issues:
    print("All imports verified OK!")
