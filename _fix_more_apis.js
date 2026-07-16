const fs = require("fs");

// Fix api/system/menu.js
let menu = fs.readFileSync("frontend/src/api/system/menu.js", "utf8");
menu = menu.replace(/url: .\/system\/menu.?,/g, function(m) {
  if (m.includes("menuId")) return "url: `/system/menu/${menuId}`,";
  if (m.includes("id")) return "url: `/system/menu/${id}`,";
  return m;
});
// Fallback: fix the known broken lines by line number
menu = menu.replace(/url: \\\/system\/menu\\\/?,/g, "url: `/system/menu/${menuId}`,");
fs.writeFileSync("frontend/src/api/system/menu.js", menu, "utf8");

// Fix api/system/role.js  
let role = fs.readFileSync("frontend/src/api/system/role.js", "utf8");
role = role.replace(/url: \\\/system\/role\\\/?,/g, "url: `/system/role/${roleId}`,");
role = role.replace(/url: \\\/system\/role\\\/?,/g, "url: `/system/role/${id}`,");
fs.writeFileSync("frontend/src/api/system/role.js", role, "utf8");

console.log("fixed");
