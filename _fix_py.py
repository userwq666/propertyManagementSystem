import re
for f in ["frontend/src/api/system/role.js", "frontend/src/api/system/menu.js"]:
    with open(f, "r", encoding="utf-8") as fp:
        c = fp.read()
    c = c.replace("url: \\/system/role\\", "url: `" + "/system/role/${roleId}`")
    c = c.replace("url: \\/system/menu\\", "url: `" + "/system/menu/${menuId}`")
    with open(f, "w", encoding="utf-8") as fp:
        fp.write(c)
    print(f + " fixed")
