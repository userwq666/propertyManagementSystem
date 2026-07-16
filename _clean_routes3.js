const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");
const lines = c.split("\n");
let result = [];
let skip = false;
let braceCount = 0;

for (let i = 0; i < lines.length; i++) {
  const l = lines[i];
  
  // Detect start of a route block to remove
  // dept, dict, config, log/ blocks
  if (l.includes("path: 'dept'") || l.includes("path: 'dict'") || 
      l.includes("path: 'config'") || l.includes("path: 'log/")) {
    skip = true;
    continue;
  }
  
  // If we are skipping and see the previous line had "{", that line should also be skipped
  // Handle this by checking if current line is a route start - the "{" would be the previous line
  if (skip) {
    // We need to also skip the "{", "}," etc
    // Just skip everything until we see a line with "}," that is at the same indent level
    // Actually, let me just skip until we see the next route block or the end of children
    continue;
  }
  
  result.push(l);
}

// Now fix the issue of extra "{" left before path lines by checking neighbors
let cleaned = [];
for (let i = 0; i < result.length; i++) {
  const cur = result[i].trim();
  const next = i + 1 < result.length ? result[i+1].trim() : "";
  // If current is "{" and next starts "path: 'dept'" etc (already removed), skip
  if (cur === "{" && (next.startsWith("},") || next === "]")) {
    // These are leftover braces from removed routes, keep them
    // Actually the route blocks already removed, but { remains
    cleaned.push(result[i]);
  } else {
    cleaned.push(result[i]);
  }
}

fs.writeFileSync("frontend/src/router/routes.js", cleaned.join("\n"), "utf8");
console.log("routes attempt 3");
