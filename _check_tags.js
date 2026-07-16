const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");
const lines = c.split("\n");
for(let i = 0; i < lines.length; i++) {
  const openCount = (lines[i].match(/<[a-zA-Z]/g) || []).length;
  const closeCount = (lines[i].match(/<\/[a-zA-Z]/g) || []).length;
  if(openCount !== closeCount && lines[i].includes("<")) {
    console.log((i+1) + " (diff=" + (openCount-closeCount) + "): " + lines[i].trim());
  }
}
