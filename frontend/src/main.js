// Patch: force passive event listeners for scroll-blocking events
(function() {
  var supportsPassive = false;
  try {
    var opts = Object.defineProperty({}, "passive", { get: function() { supportsPassive = true; } });
    window.addEventListener("test", null, opts);
  } catch (e) {}
  if (supportsPassive) {
    var orig = EventTarget.prototype.addEventListener;
    EventTarget.prototype.addEventListener = function(type, listener, options) {
      var blocked = ["wheel", "mousewheel", "touchstart", "touchmove"];
      if (blocked.indexOf(type) !== -1) {
        if (typeof options === "boolean") {
          options = { capture: options, passive: true };
        } else if (options && typeof options === "object") {
          options = Object.assign({}, options, { passive: true });
        } else {
          options = { passive: true };
        }
      }
      orig.call(this, type, listener, options);
    };
  }
})();

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import { setupDirectives } from './directives'
import './assets/styles/index.scss'

const app = createApp(App)
setupDirectives(app)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
