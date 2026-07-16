const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");

// Much simpler approach: remove lines containing these imports
const lines = c.split("\n");
const result = [];
let skipNext = false;
let skipCount = 0;

for (let i = 0; i < lines.length; i++) {
  const line = lines[i];
  
  // Detect start of a route block for dept/dict/config/log
  if (line.includes("path: 'dept'") || line.includes("path: 'dict'") || 
      line.includes("path: 'dict/data") || line.includes("path: 'config'") ||
      line.includes("path: 'log/")) {
    skipCount = 5; // skip ~5 lines for simple route
    if (line.includes("hidden: true")) skipCount = 7; // more for dict/data
    if (line.includes("activeMenu")) skipCount = 8; // more for dict/data
    continue;
  }
  
  if (skipCount > 0) {
    skipCount--;
    continue;
  }
  
  result.push(line);
}

fs.writeFileSync("frontend/src/router/routes.js", result.join("\n"), "utf8");
console.log("routes cleaned");
