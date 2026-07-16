const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Remove export button from template
c = c.replace(/\n\s+<el-button type="success"[^>]*>[\s\S]*?导出[\s\S]*?<\/el-button>/, "");

// Remove import button from template
c = c.replace(/\n\s+<el-button type="warning"[^>]*>[\s\S]*?导入[\s\S]*?<\/el-button>/, "");

// Remove download template button
c = c.replace(/\n\s+<el-button type="info"[^>]*>[\s\S]*?下载模板[\s\S]*?<\/el-button>/, "");

// Fix getRoleList function - was renamed from getRoleListData
c = c.replace(/getRoleListData/g, "getRoleList");

// Clean empty lines
c = c.replace(/\n{4,}/g, "\n\n\n");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("done");
