const fs = require("fs");
// Get the first bytes of the file to check BOM
const buf = fs.readFileSync("frontend/src/views/system/user/index.vue");
console.log("First 3 bytes:", buf[0].toString(16), buf[1].toString(16), buf[2].toString(16));
console.log("File size:", buf.length);

// Check if there"s a BOM
if (buf[0] === 0xEF && buf[1] === 0xBB && buf[2] === 0xBF) {
  console.log("File has UTF-8 BOM!");
}
