const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Remove ALL broken code from "// 导出" to "// 获取角色列表"
const start = c.indexOf("\n// 导出");
const end = c.indexOf("// 获取角色列表", start);
if (start > 0 && end > start) {
  c = c.substring(0, start) + "\n" + c.substring(end);
  console.log("Removed broken code from line", start, "to", end);
}

// Remove leftover importForm references
c = c.replace(/importForm\.file[\s\S]*?\n/g, "\n");
c = c.replace(/importDialogVisible[\s\S]*?\n/g, "\n");
c = c.replace(/importLoading[\s\S]*?\n/g, "\n");
c = c.replace(/importUser\(/g, "console.log(");

// Remove importReferences in template
c = c.replace(/v-permission="\[['\u0022]system:user:export['\u0022]\]"/g, "");

// Clean up empty lines
c = c.replace(/\n{4,}/g, "\n\n\n");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("BROKEN CODE REMOVED");
