const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Fix broken "// 导入\nconst  = () => {" patterns
c = c.replace(/\/\/ 导入\nconst  = \(\) => \{[\s\S]*?\n\}/g, "");
c = c.replace(/\/\/ 导入\nconst = \(\) => \{[\s\S]*?\n\}/g, "");

// Also handle any remaining "// 导入" followed by broken code
c = c.replace(/\/\/ 导入[\s\S]*?\n\}/g, "");

// Clean up getRoleListData references
c = c.replace(/const getRoleListData[\s\S]*?getRoleList\(\)[\s\S]*?\n\s+\}/g, "");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("done");
