const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");
const lines = c.split("\n");

// Find the system section and keep only user, role, menu
let result = [];
let inSystem = false;
let skip = 0;
let keptCount = 0;

for (let i = 0; i < lines.length; i++) {
  const line = lines[i];
  
  if (line.includes("name: 'System'") && line.includes("meta:")) {
    inSystem = true;
  }
  
  if (inSystem && line.includes("children: [")) {
    result.push(line);
    // Keep user, role, menu (5 lines each = 4+1 header each)
    // user block: 6 lines
    for (let j = 0; j < 6; j++) {
      i++;
      result.push(lines[i]);
    }
    // role block: 6 lines  
    for (let j = 0; j < 6; j++) {
      i++;
      result.push(lines[i]);
    }
    // menu block: 6 lines
    for (let j = 0; j < 6; j++) {
      i++;
      result.push(lines[i]);
    }
    // Now skip dept, dict, dict/data, config, log/login, log/operation
    // Each block has 6 lines except dict/data which has 8-9 lines
    // dept (6)
    i += 6;
    // dict (6)
    i += 6;
    // dict/data (9)
    i += 9;
    // config (6)
    i += 6;
    // log/login (6)
    i += 6;
    // log/operation (6)
    i += 6;
    // remaining "]" closing
    result.push(lines[i]); // should be "    ]"
    inSystem = false;
    continue;
  }
  
  if (!inSystem) {
    result.push(line);
  }
}

fs.writeFileSync("frontend/src/router/routes.js", result.join("\n"), "utf8");
console.log("routes fixed directly");
