const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");
const lines = c.split("\n");
// Show lines 268-272
for(let i = 265; i < 275 && i < lines.length; i++) {
  console.log((i+1) + ": " + lines[i]);
}
