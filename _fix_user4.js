const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Remove handleExport broken code - from "// 导出" to the matching "finally {"
c = c.replace(/\/\/ 导出\n\)[\s\S]*?} finally \{\n\s+\}/, "// export removed");

// Remove "// removed" lines that were inserted from previous cleanup
c = c.replace(/\/\/ removed[\s]*\n/g, "");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("fixed remaining issues");
