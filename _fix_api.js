const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");
// Also fix the API file
let api = fs.readFileSync("frontend/src/api/system/user.js", "utf8");
api = api.replace(/\\\/system\/user\\\/?\\/g, "`/system/user/${userId}`");
api = api.replace(/\\\/system\/user\\\/?\\/g, "`/system/user/${id}`");
// Actually, the first replacement catches both, so let me do it properly
api = api.replace(/url: .\/system\/user.?,/g, function(m) {
  if (m.includes("${userId}")) return "url: \`/system/user/" + "${userId}" + "\`,";
  if (m.includes("${id}")) return "url: \`/system/user/" + "${id}" + "\`,";
  return m;
});
console.log(api);
