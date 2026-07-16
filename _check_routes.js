const fs = require("fs");
const c = fs.readFileSync("frontend/src/router/routes.js", "utf8");
// Find all import paths
const imports = c.match(/import\('@\/.*?'\)/g) || [];
imports.forEach(imp => {
  const p = imp.replace("import('@/", "frontend/src/").replace("')", "");
  if (!fs.existsSync(p)) {
    console.log("MISSING: " + p);
  }
});
console.log("check done");
