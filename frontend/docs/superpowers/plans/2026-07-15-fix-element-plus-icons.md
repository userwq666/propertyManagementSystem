# Fix Element Plus Icons Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 批量修复所有 Vue 文件中不存在的 Element Plus 图标，确保构建成功

**Architecture:** 通过脚本检查所有导入的图标，与可用图标列表比对，自动替换不存在的图标为推荐替代方案

**Tech Stack:** Node.js, Element Plus Icons Vue, Vite

---

## 任务 1: 分析所有 Vue 文件中的图标导入

**Files:**
- 临时脚本: `scripts/check-icons.js`
- 输出: `docs/superpowers/plans/icon-audit.md`

- [ ] **Step 1: 创建图标检查脚本**

```javascript
// scripts/check-icons.js
const fs = require('fs');
const path = require('path');
const glob = require('glob');

// 可用的 Element Plus 图标列表
const availableIcons = [
  'AddLocation', 'Aim', 'AlarmClock', 'Apple', 'ArrowDown', 'ArrowDownBold', 'ArrowLeft', 'ArrowLeftBold', 'ArrowRight', 'ArrowRightBold', 'ArrowUp', 'ArrowUpBold', 'Avatar', 'Back', 'Baseball', 'Basketball', 'Bell', 'BellFilled', 'Bicycle', 'Bottom', 'BottomLeft', 'BottomRight', 'Bowl', 'Box', 'Briefcase', 'Brush', 'BrushFilled', 'Burger', 'Calendar', 'Camera', 'CameraFilled', 'CaretBottom', 'CaretLeft', 'CaretRight', 'CaretTop', 'Cellphone', 'ChatDotRound', 'ChatDotSquare', 'ChatLineRound', 'ChatLineSquare', 'ChatRound', 'ChatSquare', 'Check', 'Checked', 'Cherry', 'Chicken', 'ChromeFilled', 'CircleCheck', 'CircleCheckFilled', 'CircleClose', 'CircleCloseFilled', 'CirclePlus', 'CirclePlusFilled', 'Clock', 'Close', 'CloseBold', 'Cloudy', 'Coffee', 'CoffeeCup', 'Coin', 'ColdDrink', 'Collection', 'CollectionTag', 'Comment', 'Compass', 'Connection', 'Coordinate', 'CopyDocument', 'Cpu', 'CreditCard', 'Crop', 'DArrowLeft', 'DArrowRight', 'DCaret', 'DataAnalysis', 'DataBoard', 'DataLine', 'Delete', 'DeleteFilled', 'DeleteLocation', 'Dessert', 'Discount', 'Dish', 'DishDot', 'Document', 'DocumentAdd', 'DocumentChecked', 'DocumentCopy', 'DocumentDelete', 'DocumentRemove', 'Download', 'Drizzling', 'Edit', 'EditPen', 'Eleme', 'ElemeFilled', 'ElementPlus', 'Expand', 'Failed', 'Female', 'Files', 'Film', 'Filter', 'Finished', 'FirstAidKit', 'Flag', 'Fold', 'Folder', 'FolderAdd', 'FolderChecked', 'FolderDelete', 'FolderOpened', 'FolderRemove', 'Food', 'Football', 'ForkSpoon', 'Fries', 'FullScreen', 'Goblet', 'GobletFull', 'GobletSquare', 'GobletSquareFull', 'GoldMedal', 'Goods', 'GoodsFilled', 'Grape', 'Grid', 'Guide', 'Handbag', 'Headset', 'Help', 'HelpFilled', 'Hide', 'Histogram', 'HomeFilled', 'HotWater', 'House', 'IceCream', 'IceCreamRound', 'IceCreamSquare', 'IceDrink', 'IceTea', 'InfoFilled', 'Iphone', 'Key', 'KnifeFork', 'Lightning', 'Link', 'List', 'Loading', 'Location', 'LocationFilled', 'LocationInformation', 'Lock', 'Lollipop', 'MagicStick', 'Magnet', 'Male', 'Management', 'MapLocation', 'Medal', 'Memo', 'Menu', 'Message', 'MessageBox', 'Mic', 'Microphone', 'MilkTea', 'Minus', 'Money', 'Monitor', 'Moon', 'MoonNight', 'More', 'MoreFilled', 'MostlyCloudy', 'Mouse', 'Mug', 'Mute', 'MuteNotification', 'NoSmoking', 'Notebook', 'Notification', 'Odometer', 'OfficeBuilding', 'Open', 'Operation', 'Opportunity', 'Orange', 'Paperclip', 'PartlyCloudy', 'Pear', 'Phone', 'PhoneFilled', 'Picture', 'PictureFilled', 'PictureRounded', 'PieChart', 'Place', 'Platform', 'Plus', 'Pointer', 'Position', 'Postcard', 'Pouring', 'Present', 'PriceTag', 'Printer', 'Promotion', 'QuartzWatch', 'QuestionFilled', 'Rank', 'Reading', 'ReadingLamp', 'Refresh', 'RefreshLeft', 'RefreshRight', 'Refrigerator', 'Remove', 'RemoveFilled', 'Right', 'ScaleToOriginal', 'School', 'Scissor', 'Search', 'Select', 'Sell', 'SemiSelect', 'Service', 'SetUp', 'Setting', 'Share', 'Ship', 'Shop', 'ShoppingBag', 'ShoppingCart', 'ShoppingCartFull', 'ShoppingTrolley', 'Smoking', 'Soccer', 'SoldOut', 'Sort', 'SortDown', 'SortUp', 'Stamp', 'Star', 'StarFilled', 'Stopwatch', 'SuccessFilled', 'Sugar', 'Suitcase', 'SuitcaseLine', 'Sunny', 'Sunrise', 'Sunset', 'Switch', 'SwitchButton', 'SwitchFilled', 'TakeawayBox', 'Ticket', 'Tickets', 'Timer', 'ToiletPaper', 'Tools', 'Top', 'TopLeft', 'TopRight', 'TrendCharts', 'Trophy', 'TrophyBase', 'TurnOff', 'Umbrella', 'Unlock', 'Upload', 'UploadFilled', 'User', 'UserFilled', 'Van', 'VideoCamera', 'VideoCameraFilled', 'VideoPause', 'VideoPlay', 'View', 'Wallet', 'WalletFilled', 'WarnTriangleFilled', 'Warning', 'WarningFilled', 'Watch', 'Watermelon', 'WindPower', 'ZoomIn', 'ZoomOut'
];

// 图标替换映射
const iconMappings = {
  'QuestionMarkCircle': 'QuestionFilled',
  'Send': 'Promotion',
  'Read': 'Reading',
  'Megaphone': 'Notification',
  'Logout': 'SwitchButton',
  'HouseFilled': 'House',
  'Parking': 'OfficeBuilding',
  'Truck': 'Van',
  'ListCheck': 'Finished',
  'Wrench': 'Tools',
  'MenuIcon': 'Menu'
};

// 搜索 Vue 文件
const srcDir = path.join(__dirname, '../src');
const vueFiles = glob.sync('**/*.vue', { cwd: srcDir });

const missingIcons = {};

vueFiles.forEach(file => {
  const filePath = path.join(srcDir, file);
  const content = fs.readFileSync(filePath, 'utf8');
  
  // 提取导入语句
  const importRegex = /import\s*\{([^}]+)\}\s*from\s*'@element-plus\/icons-vue'/g;
  let match;
  
  while ((match = importRegex.exec(content)) !== null) {
    const icons = match[1].split(',').map(icon => icon.trim());
    
    icons.forEach(icon => {
      if (!availableIcons.includes(icon)) {
        if (!missingIcons[file]) {
          missingIcons[file] = [];
        }
        missingIcons[file].push({
          original: icon,
          replacement: iconMappings[icon] || 'Unknown'
        });
      }
    });
  }
});

// 生成审计报告
let report = '# Icon Audit Report\n\n';
report += 'Generated: ' + new Date().toISOString() + '\n\n';

if (Object.keys(missingIcons).length === 0) {
  report += '✅ All icons are valid!\n';
} else {
  report += '❌ Missing icons found:\n\n';
  
  Object.keys(missingIcons).forEach(file => {
    report += `## ${file}\n\n`;
    missingIcons[file].forEach(({ original, replacement }) => {
      report += `- ${original} → ${replacement}\n`;
    });
    report += '\n';
  });
}

fs.writeFileSync(path.join(__dirname, '../docs/superpowers/plans/icon-audit.md'), report);
console.log('Audit report generated: docs/superpowers/plans/icon-audit.md');
console.log('Missing icons in', Object.keys(missingIcons).length, 'files');
```

- [ ] **Step 2: 运行图标检查脚本**

```bash
cd propertyManagementSystem/frontend
node scripts/check-icons.js
```

- [ ] **Step 3: 审查审计报告**

```bash
cat docs/superpowers/plans/icon-audit.md
```

## 任务 2: 批量替换不存在的图标

**Files:**
- 临时脚本: `scripts/fix-icons.js`
- 所有包含缺失图标的 Vue 文件

- [ ] **Step 1: 创建图标修复脚本**

```javascript
// scripts/fix-icons.js
const fs = require('fs');
const path = require('path');
const glob = require('glob');

// 图标替换映射
const iconMappings = {
  'QuestionMarkCircle': 'QuestionFilled',
  'Send': 'Promotion',
  'Read': 'Reading',
  'Megaphone': 'Notification',
  'Logout': 'SwitchButton',
  'HouseFilled': 'House',
  'Parking': 'OfficeBuilding',
  'Truck': 'Van',
  'ListCheck': 'Finished',
  'Wrench': 'Tools',
  'MenuIcon': 'Menu'
};

// 搜索 Vue 文件
const srcDir = path.join(__dirname, '../src');
const vueFiles = glob.sync('**/*.vue', { cwd: srcDir });

let fixedFiles = 0;

vueFiles.forEach(file => {
  const filePath = path.join(srcDir, file);
  let content = fs.readFileSync(filePath, 'utf8');
  let modified = false;
  
  // 替换导入语句中的图标
  const importRegex = /import\s*\{([^}]+)\}\s*from\s*'@element-plus\/icons-vue'/g;
  content = content.replace(importRegex, (match, icons) => {
    const iconList = icons.split(',').map(icon => icon.trim());
    const fixedIcons = iconList.map(icon => {
      if (iconMappings[icon]) {
        modified = true;
        console.log(`Replacing ${icon} → ${iconMappings[icon]} in ${file}`);
        return iconMappings[icon];
      }
      return icon;
    });
    return `import { ${fixedIcons.join(', ')} } from '@element-plus/icons-vue'`;
  });
  
  // 替换模板中的图标使用
  Object.keys(iconMappings).forEach(original => {
    const replacement = iconMappings[original];
    const regex = new RegExp(`<${original}\\b`, 'g');
    if (regex.test(content)) {
      content = content.replace(regex, `<${replacement}`);
      modified = true;
      console.log(`Replacing template usage of ${original} → ${replacement} in ${file}`);
    }
  });
  
  if (modified) {
    fs.writeFileSync(filePath, content, 'utf8');
    fixedFiles++;
  }
});

console.log(`Fixed ${fixedFiles} files`);
```

- [ ] **Step 2: 运行图标修复脚本**

```bash
cd propertyManagementSystem/frontend
node scripts/fix-icons.js
```

- [ ] **Step 3: 验证修复结果**

```bash
node scripts/check-icons.js
```

## 任务 3: 验证构建成功

**Files:**
- 无

- [ ] **Step 1: 运行 Vite 构建**

```bash
cd propertyManagementSystem/frontend
npx vite build
```

- [ ] **Step 2: 检查构建输出**

确认没有 "is not exported" 错误

## 任务 4: 清理临时文件

**Files:**
- 临时脚本: `scripts/check-icons.js`
- 临时脚本: `scripts/fix-icons.js`
- 临时报告: `docs/superpowers/plans/icon-audit.md`

- [ ] **Step 1: 删除临时脚本**

```bash
cd propertyManagementSystem/frontend
rm -rf scripts
```

- [ ] **Step 2: 删除审计报告**

```bash
rm docs/superpowers/plans/icon-audit.md
```

- [ ] **Step 3: 提交更改**

```bash
git add -A
git commit -m "fix: replace non-existent Element Plus icons with valid alternatives"
```