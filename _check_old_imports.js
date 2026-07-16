const fs = require("fs");
const views = [
  "frontend/src/views/fee/index.vue",
  "frontend/src/views/property/index.vue",
  "frontend/src/views/repair/index.vue",
  "frontend/src/views/dashboard/index.vue",
  "frontend/src/views/system/index.vue",
  "frontend/src/views/complaint/index.vue",
  "frontend/src/views/notice/index.vue",
  "frontend/src/views/parking/space/index.vue",
  "frontend/src/views/parking/record/index.vue",
  "frontend/src/views/parking/rent/index.vue",
  "frontend/src/views/parking/vehicle/index.vue",
  "frontend/src/views/profile/index.vue",
  "frontend/src/views/profile/password.vue"
];
views.forEach(f => {
  if (fs.existsSync(f)) {
    let c = fs.readFileSync(f, "utf8");
    // Check for old API imports
    let hasOld = c.match(/from ['"]@\/api\/(system|fee|repair|complaint|property|notice|user)['"]/);
    if (hasOld) {
      console.log(f + ": has old import: " + hasOld[0]);
    }
  }
});
console.log("CHECK DONE");
