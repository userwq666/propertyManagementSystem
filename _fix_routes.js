const fs = require("fs");
let c = fs.readFileSync("frontend/src/router/routes.js", "utf8");

// Remove dept route
c = c.replace(/{\s*\n\s+path: 'dept',[\s\S]*?metaroles: \['admin'\]\s+\}\s+},/, "");
// Remove dict route  
c = c.replace(/{\s*\n\s+path: 'dict',[\s\S]*?metaroles: \['admin'\]\s+\}\s+},/, "");
// Remove dict/data route
c = c.replace(/{\s*\n\s+path: 'dict\/data\/:dictType',[\s\S]*?metaroles: \['admin'\]\s+\}\s+},/, "");
// Remove config route
c = c.replace(/{\s*\n\s+path: 'config',[\s\S]*?metaroles: \['admin'\]\s+\}\s+},/, "");
// Remove login log route
c = c.replace(/{\s*\n\s+path: 'log\/login',[\s\S]*?metaroles: \['admin'\]\s+\}\s+},/, "");
// Remove operation log route
c = c.replace(/{\s*\n\s+path: 'log\/operation',[\s\S]*?metaroles: \['admin'\]\s+\}\s+},/, "");

// Clean up empty children arrays
c = c.replace(/children: \[\s*\n\s*\n\s*\]/g, "children: []");

fs.writeFileSync("frontend/src/router/routes.js", c, "utf8");
console.log("routes fixed");
