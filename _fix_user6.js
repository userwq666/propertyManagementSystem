const fs = require('fs');
let c = fs.readFileSync('frontend/src/views/system/user/index.vue', 'utf8');

// Remove broken '// 导入' section
c = c.replace(/\/\/ 导入\nconst = \(\) => \{[\s\S]*?\n\}/g, '');
// Remove broken '// export removed' and extra spaces
c = c.replace(/\/\/ export removed\n+/g, '');
// Remove broken import references   
c = c.replace(/\/\/ 导入\n\/\/ import removed\n\s+\}\n\}/g, '');

// Find and fix deptTreeProps reference (remove if not used)
// Find '// 获取部门树' and its function
c = c.replace(/\/\/ 获取部门树\nconst getDeptTreeData[\s\S]*?\n\}/g, '');

// Clean up getRoleListData to use proper import
// Check handleAssignRole references
c = c.replace(/getRoleListData/g, 'getRoleList');

// Remove extra blank lines
c = c.replace(/\n{4,}/g, '\n\n\n');

fs.writeFileSync('frontend/src/views/system/user/index.vue', c, 'utf8');
console.log('done');
