const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Fix conflict: local function getRoleList should be getRoleListData
// (import already provides getRoleList from API)
c = c.replace(/const getRoleList = async \(\) =>/, "const getRoleListData = async () =>");
// Also fix the call site
c = c.replace(/getRoleListData\(\)/g, "getRoleList()");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("fixed naming conflict");
