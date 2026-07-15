import { defineStore } from 'pinia'

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [],
    cachedViews: []
  }),
  actions: {
    addView(view) {
      this.addVisitedView(view)
      this.addCachedView(view)
    },
    addVisitedView(view) {
      if (this.visitedViews.some(v => v.path === view.path)) return
      if (view.meta?.affix) {
        this.visitedViews.unshift({ ...view })
      } else {
        this.visitedViews.push({ ...view })
      }
    },
    addCachedView(view) {
      if (this.cachedViews.includes(view.name)) return
      if (!view.meta?.noCache) {
        this.cachedViews.push(view.name)
      }
    },
    delView(view) {
      return new Promise(resolve => {
        this.delVisitedView(view)
        this.delCachedView(view)
        resolve({ visitedViews: [...this.visitedViews], cachedViews: [...this.cachedViews] })
      })
    },
    delVisitedView(view) {
      const index = this.visitedViews.findIndex(v => v.path === view.path)
      if (index !== -1 && !this.visitedViews[index].meta?.affix) {
        this.visitedViews.splice(index, 1)
      }
    },
    delCachedView(view) {
      const index = this.cachedViews.indexOf(view.name)
      if (index !== -1) {
        this.cachedViews.splice(index, 1)
      }
    },
    delOthersViews(view) {
      return new Promise(resolve => {
        this.visitedViews = this.visitedViews.filter(v => v.meta?.affix || v.path === view.path)
        this.cachedViews = this.cachedViews.filter(v => v === view.name)
        resolve({ visitedViews: [...this.visitedViews], cachedViews: [...this.cachedViews] })
      })
    },
    delAllViews() {
      return new Promise(resolve => {
        this.visitedViews = this.visitedViews.filter(v => v.meta?.affix)
        this.cachedViews = []
        resolve({ visitedViews: [...this.visitedViews], cachedViews: [] })
      })
    },
    updateVisitedView(view) {
      const index = this.visitedViews.findIndex(v => v.path === view.path)
      if (index !== -1) {
        this.visitedViews[index] = { ...view }
      }
    }
  }
})