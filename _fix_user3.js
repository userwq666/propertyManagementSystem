const fs = require("fs");
let c = fs.readFileSync("frontend/src/views/system/user/index.vue", "utf8");

// Remove importFormRef line
c = c.replace(/const importFormRef = ref\(null\)\n/, "");

// Remove import-related code blocks (functions already deleted, remove residual references)
c = c.replace(/importDialogVisible\.value = true[\s\S]*?importForm\.file = file\.raw[\s\S]*?importLoading\.value = true[\s\S]*?formData\.append\('file', importForm\.file\)[\s\S]*?importDialogVisible\.value = false[\s\S]*?importForm\.file = null[\s\S]*?importLoading\.value = false/g, "// import removed");

// Remove standalone importDialogVisible/importLoading/importForm references
c = c.replace(/importDialogVisible\.value[\s\S]*?\n/g, "// removed\n");
c = c.replace(/importForm\.file[\s\S]*?\n/g, "// removed\n");
c = c.replace(/importLoading\.value[\s\S]*?\n/g, "// removed\n");

// Remove import dialog section in template
c = c.replace(/<!-- 导入弹窗[\s\S]*?<\/el-dialog>/g, "");

// Remove empty import button handler reference in toolbar
c = c.replace(/handleImport/g, "");

// Fix deptTreeProps if it remains with no data source
c = c.replace(/const deptTreeProps = ref\(\{[\s\S]*?\}\n\)/, "const deptTreeProps = ref({})");

// Fix empty el-button-group
c = c.replace(/<el-button-group>\s*<\/el-button-group>/g, "");

fs.writeFileSync("frontend/src/views/system/user/index.vue", c, "utf8");
console.log("user view fully fixed");
