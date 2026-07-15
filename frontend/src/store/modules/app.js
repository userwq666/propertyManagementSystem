import { defineStore } from 'pinia'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: true,
      withoutAnimation: false
    },
    device: 'desktop',
    size: 'default',
    language: 'zh-cn',
    layout: 'sidebar',
    fixedHeader: true,
    showTagsView: true,
    showLogo: true,
    showFooter: false,
    theme: 'light'
  }),
  getters: {
    sidebarOpened: state => state.sidebar.opened
  },
  actions: {
    toggleSidebar(withoutAnimation) {
      this.sidebar.opened = !this.sidebar.opened
      this.sidebar.withoutAnimation = withoutAnimation
    },
    closeSidebar(withoutAnimation) {
      this.sidebar.opened = false
      this.sidebar.withoutAnimation = withoutAnimation
    },
    openSidebar(withoutAnimation) {
      this.sidebar.opened = true
      this.sidebar.withoutAnimation = withoutAnimation
    },
    toggleDevice(device) {
      this.device = device
    },
    setSize(size) {
      this.size = size
    },
    setLanguage(language) {
      this.language = language
    },
    setLayout(layout) {
      this.layout = layout
    },
    setFixedHeader(fixedHeader) {
      this.fixedHeader = fixedHeader
    },
    setShowTagsView(showTagsView) {
      this.showTagsView = showTagsView
    },
    setShowLogo(showLogo) {
      this.showLogo = showLogo
    },
    setTheme(theme) {
      this.theme = theme
    }
  }
})