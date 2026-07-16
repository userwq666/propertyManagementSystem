const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Find and remove the broken export code block
// Pattern: "// 导出\n)" followed by blob download code with "catch" and "finally"
const brokenCodeStart = c.indexOf('// 导出');
if (brokenCodeStart > 0) {
  // Find the next function declaration or end of file after this
  let endPos = c.indexOf('\n}\n\n', brokenCodeStart + 10);
  if (endPos < 0) endPos = c.indexOf('\nconst ', brokenCodeStart + 10);
  if (endPos < 0) endPos = c.length;
  // Actually, find "} finally {" and remove from there to the matching "}"
  let finallyPos = c.indexOf('} finally {', brokenCodeStart);
  if (finallyPos > 0) {
    let closePos = c.indexOf('\n}', finallyPos + 11);
    if (closePos > 0) closePos = c.indexOf('\n', closePos + 2); // include the newline after }
    // Remove from "// 导出" to the end of the finally block
    c = c.substring(0, brokenCodeStart) + '\n// export removed\n' + c.substring(closePos < 0 ? finallyPos + 11 : closePos + 1);
  }
}

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("fixed");
