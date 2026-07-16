const fs = require("fs");
const files = ["frontend/src/views/system/user/index.vue","frontend/src/views/system/role/index.vue","frontend/src/views/system/menu/index.vue"];
files.forEach(f => {
  let buf = fs.readFileSync(f);
  if (buf[0] === 0xEF && buf[1] === 0xBB && buf[2] === 0xBF) {
    fs.writeFileSync(f, buf.slice(3));
    console.log(f + ": BOM removed");
  }
});
