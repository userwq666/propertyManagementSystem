with open("src/views/fee/item/index.vue", "r", encoding="utf-8") as f:
    c = f.read()
# Count sections
import re
print("Template line count:", c.count("\n"))
print("Has script setup:", "<script setup>" in c)
print("Has el-table:", "<el-table" in c)
print("Has el-pagination:", "<el-pagination" in c)
print("Has el-dialog:", "<el-dialog" in c)
print("Has el-form:", "<el-form" in c)
# Check status enum usage
for s in ["status", "Status", "STATUS"]:
    cnt = c.count(s)
    if cnt > 3:
        print(f"Status refs ({s}): {cnt}")
