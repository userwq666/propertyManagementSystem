with open("src/views/system/menu/index.vue", "r", encoding="utf-8") as f:
    lines = f.readlines()

# Find script setup
for i, line in enumerate(lines, 1):
    if "<script setup>" in line:
        print(f"Script setup at line {i}")
        for j in range(i, min(i+40, len(lines)+1)):
            print(f"{j}: {lines[j-1].rstrip()}")
        break
