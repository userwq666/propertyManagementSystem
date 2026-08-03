import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'
import { existsSync, readdirSync, statSync, unlinkSync, rmdirSync } from 'fs'

function emptyDir(dir) {
  if (!existsSync(dir)) return
  for (const name of readdirSync(dir)) {
    const target = resolve(dir, name)
    const stat = statSync(target)
    if (stat.isDirectory()) {
      emptyDir(target)
      rmdirSync(target)
    } else {
      unlinkSync(target)
    }
  }
}

export default defineConfig({
  plugins: [
    {
      name: 'clean-out-dir-before-build',
      apply: 'build',
      configResolved(config) {
        emptyDir(resolve(config.root, config.build.outDir))
      }
    },
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts'
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts'
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ws': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
})
