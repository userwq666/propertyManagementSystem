import os

# Check a few view files for structure reference
for path in ["src/views/fee/item/index.vue", "src/views/inspection/plan/index.vue", "src/views/repair/order/index.vue"]:
    with open(path, "r", encoding="utf-8") as f:
        lines = f.readlines()
    print(f"=== {path} ({len(lines)} lines) ===")
    # Show script section start
    for i, line in enumerate(lines):
        if "<script setup>" in line:
            print(f"  Script starts at line {i+1}")
        if "</script>" in line:
            print(f"  Script ends at line {i+1}")
        if "<style" in line:
            print(f"  Style section starts at line {i+1}")
    print()
