let tabId = ''

export function getTabId() {
  if (!tabId) {
    tabId = typeof crypto !== 'undefined' && crypto.randomUUID
      ? crypto.randomUUID()
      : `tab_${Date.now()}_${Math.random().toString(36).slice(2)}`
  }
  return tabId
}
