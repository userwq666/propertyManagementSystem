const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Find template section
const templateStart = c.indexOf("<template>");
const scriptStart = c.indexOf("<script setup>");
if (templateStart >= 0 && scriptStart > templateStart) {
  const templateContent = c.substring(templateStart, scriptStart);
  console.log("Template from line", templateContent.substring(0, 200));
  console.log("...");
  console.log(templateContent.substring(templateContent.length - 200));
}
