const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");
c = c.replace(/\/\/ 导入弹窗\n= ref\(false\)\n= ref\(false\)\n= reactive\(\{[^}]*\}\)\n\n/, "\n");
fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("fixed");
