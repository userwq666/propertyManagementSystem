import re, os, glob

# Extract all v-permission values from all frontend Vue files
views_dir = r"frontend/src/views"
fe_perms = set()
for fp in glob.glob(f"{views_dir}/**/*.vue", recursive=True):
    with open(fp, "r", encoding="utf-8") as f:
        c = f.read()
    found = re.findall(r"v-permission=""'([^']+)'""", c)
    fe_perms.update(found)

print("=== Frontend v-permission values ===")
for p in sorted(fe_perms):
    print(f"  {p}")

# Extract all perms from 02_data.sql menu
with open(r"src/main/resources/sql/02_data.sql", "r", encoding="utf-8") as f:
    c = f.read()

# Find the menu INSERT
start = c.find("INSERT INTO sys_menu")
end = c.find("-- 角色-菜单")
menu_section = c[start:end]

sql_perms = set()
# Match perms in pattern: 'xxx:xxx:xxx'
found = re.findall(r"'([a-z]+:[a-z]+:[a-z]+)'", menu_section)
sql_perms.update(found)

print(f"\n=== SQL menu perms ({len(sql_perms)} total) ===")

# Find perms that are in frontend but NOT in SQL
missing_in_sql = fe_perms - sql_perms
print(f"\n=== In frontend but MISSING from SQL ({len(missing_in_sql)}) ===")
for p in sorted(missing_in_sql):
    print(f"  MISSING: {p}")

# Find perms in SQL but NOT in frontend
extra_in_sql = sql_perms - fe_perms
print(f"\n=== In SQL but NOT used in frontend ({len(extra_in_sql)}) ===")
for p in sorted(extra_in_sql)[:10]:
    print(f"  extra: {p}")
if len(extra_in_sql) > 10:
    print(f"  ... and {len(extra_in_sql)-10} more")
