const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");
const lines = c.split("\n");
let result = [];
let skip = false;
for (let i = 0; i < lines.length; i++) {
  const l = lines[i];
  if (l.includes("path: 'dict')") || l.includes("path: 'dict/data") || 
      l.includes("path: 'config'") || l.includes("path: 'log/")) {
    skip = true;
    continue;
  }
  if (skip && (l.trim().startsWith("},") || l.trim() === "]")) {
    skip = false;
    continue;
  }
  if (!skip) result.push(l);
}
fs.writeFileSync("frontend/src/router/routes.js", result.join("\n"), "utf8");
console.log("routes cleaned 2");
