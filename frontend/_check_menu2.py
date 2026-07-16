with open("src/views/system/menu/index.vue", "r", encoding="utf-8") as f:
    lines = f.readlines()

# Show import section and export function area
print("=== Import section ===")
for i in range(55, 66):
    print(f"{i+1}: {lines[i].rstrip()}")

print()
print("=== Export function area ===")
for i in range(470, min(495, len(lines))):
    print(f"{i+1}: {lines[i].rstrip()}")
