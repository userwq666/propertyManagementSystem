import permission from './permission'
import clickOutside from './clickOutside'
import debounce from './debounce'
import throttle from './throttle'
import copy from './copy'
import watermark from './watermark'
import focus from './focus'
import lazyLoad from './lazyLoad'

const directives = {
  permission,
  clickOutside,
  debounce,
  throttle,
  copy,
  watermark,
  focus,
  lazyLoad
}

export default {
  install(app) {
    Object.keys(directives).forEach(key => {
      app.directive(key, directives[key])
    })
  }
}