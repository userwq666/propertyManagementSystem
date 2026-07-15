import { defineStore } from 'pinia'

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    theme: 'light',
    themeColor: '#409eff',
    tagsView: true,
    fixedHeader: true,
    sidebarLogo: true,
    uniqueOpened: false,
    language: 'zh-cn',
    showSettings: false,
    showTagsView: true,
    showFooter: false,
    transitionName: 'fade',
    watermarkEnabled: false,
    watermarkText: 'Property Management System'
  }),
  getters: {
    getTheme: state => state.theme,
    getThemeColor: state => state.themeColor,
    getTagsView: state => state.tagsView,
    getFixedHeader: state => state.fixedHeader,
    getSidebarLogo: state => state.sidebarLogo,
    getLanguage: state => state.language
  },
  actions: {
    setTheme(theme) {
      this.theme = theme
      document.documentElement.setAttribute('data-theme', theme)
    },
    setThemeColor(color) {
      this.themeColor = color
      document.documentElement.style.setProperty('--el-color-primary', color)
    },
    setTagsView(tagsView) {
      this.tagsView = tagsView
    },
    setFixedHeader(fixedHeader) {
      this.fixedHeader = fixedHeader
    },
    setSidebarLogo(sidebarLogo) {
      this.sidebarLogo = sidebarLogo
    },
    setLanguage(language) {
      this.language = language
    },
    setTransitionName(name) {
      this.transitionName = name
    },
    resetSettings() {
      this.theme = 'light'
      this.themeColor = '#409eff'
      this.tagsView = true
      this.fixedHeader = true
      this.sidebarLogo = true
      this.language = 'zh-cn'
      this.transitionName = 'fade'
    }
  },
  persist: {
    key: 'settingsStore',
    paths: ['theme', 'themeColor', 'tagsView', 'fixedHeader', 'sidebarLogo', 'language', 'transitionName', 'watermarkEnabled', 'watermarkText']
  }
})