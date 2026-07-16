const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Remove broken getDeptTreeData block
c = c.replace(/\/\/ 获取部门树\n\)[\s\S]*?\n\}/g, "");
c = c.replace(/\/\/ 获取部门树[\s\S]*?\n\}/g, "");
c = c.replace(/\/\/ 获取部门树\nconst getDeptTreeData[\s\S]*?\n\}/g, "");

// Remove getRoleListData call 
c = c.replace(/await getRoleListData\(\)\n/, "// role data loading removed\n");

// Fix importFormRef
c = c.replace(/const importFormRef = ref\(null\)/, "");

// Check any remaining broken refs
c = c.replace(/const assignRoleFormRef[\s\S]*?\n\s+}\n\s+}\n/, "const assignRoleFormRef = ref(null)\n");

// Clean up getRoleListData function
c = c.replace(/const getRoleListData[\s\S]*?\n\s+}\n\s+}\n/g, "");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("done");
