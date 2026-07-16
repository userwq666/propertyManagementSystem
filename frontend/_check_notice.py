with open("src/views/announcement/notice/index.vue", "r", encoding="utf-8") as f:
    lines = f.readlines()
for i in range(min(20, len(lines))):
    print(f"{i+1}: {lines[i].rstrip()}")
print(f"\nTotal lines: {len(lines)}")
