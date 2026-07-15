export function debounce(fn, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

export function throttle(fn, delay = 300) {
  let lastTime = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastTime >= delay) {
      lastTime = now
      fn.apply(this, args)
    }
  }
}

export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj)
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (obj instanceof Object) {
    const clone = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clone[key] = deepClone(obj[key])
      }
    }
    return clone
  }
}

export function isEmpty(value) {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim() === ''
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

export function formatMoney(value, decimals = 2) {
  if (isEmpty(value)) return '0.00'
  const num = Number(value)
  if (isNaN(num)) return '0.00'
  return num.toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

export function parseTime(time, cFormat) {
  if (!time) return null
  const date = new Date(time)
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  return format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    if (key === 'a') return ['日', '一', '二', '三', '四', '五', '六'][value]
    return value.toString().padStart(2, '0')
  })
}

export function getDictLabel(dictList, value) {
  if (!dictList || !value) return ''
  const item = dictList.find(d => d.dictValue === value || d.value === value)
  return item ? item.dictLabel || item.label : ''
}

export function getDictValue(dictList, label) {
  if (!dictList || !label) return ''
  const item = dictList.find(d => d.dictLabel === label || d.label === label)
  return item ? item.dictValue || item.value : ''
}

export function arrayToTree(array, parentId = '0', idKey = 'id', parentKey = 'parentId', childrenKey = 'children') {
  const result = []
  const map = {}
  array.forEach(item => {
    map[item[idKey]] = item
  })
  array.forEach(item => {
    const parent = map[item[parentKey]]
    if (parent) {
      parent[childrenKey] = parent[childrenKey] || []
      parent[childrenKey].push(item)
    } else if (item[parentKey] === parentId || item[parentKey] === 0 || item[parentKey] === '0') {
      result.push(item)
    }
  })
  return result
}

export function treeToArray(tree, childrenKey = 'children') {
  const result = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      const { [childrenKey]: children, ...rest } = node
      result.push(rest)
      if (children && children.length) {
        traverse(children)
      }
    })
  }
  traverse(tree)
  return result
}

export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

export function downloadBlob(blob, filename) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

export function exportExcel(data, filename = 'export.xlsx') {
  import('xlsx').then(XLSX => {
    const worksheet = XLSX.utils.json_to_sheet(data)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1')
    XLSX.writeFile(workbook, filename)
  })
}

export function copyToClipboard(text) {
  return navigator.clipboard.writeText(text)
}

export function getFileExtension(filename) {
  return filename.slice(((filename.lastIndexOf('.') - 1) >>> 0) + 2)
}

export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

export function isMobile() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

export function isWechat() {
  return /MicroMessenger/i.test(navigator.userAgent)
}

export function getUrlParam(name) {
  const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)')
  const r = window.location.search.substr(1).match(reg)
  return r ? decodeURIComponent(r[2]) : null
}

export function setUrlParam(url, name, value) {
  const urlObj = new URL(url)
  urlObj.searchParams.set(name, value)
  return urlObj.toString()
}

export function removeUrlParam(url, name) {
  const urlObj = new URL(url)
  urlObj.searchParams.delete(name)
  return urlObj.toString()
}