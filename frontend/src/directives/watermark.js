export default {
  mounted(el, binding) {
    const { value } = binding
    const text = value?.text || 'Property Management System'
    const fontSize = value?.fontSize || '16px'
    const color = value?.color || 'rgba(0, 0, 0, 0.1)'
    const angle = value?.angle || -20
    const zIndex = value?.zIndex || 9999

    const canvas = document.createElement('canvas')
    const ctx = canvas.getContext('2d')
    canvas.width = 200
    canvas.height = 150

    ctx.font = `${fontSize} Microsoft YaHei`
    ctx.fillStyle = color
    ctx.textAlign = 'center'
    ctx.textBaseline = 'middle'
    ctx.translate(canvas.width / 2, canvas.height / 2)
    ctx.rotate((angle * Math.PI) / 180)
    ctx.fillText(text, 0, 0)

    const base64 = canvas.toDataURL('image/png')

    el.style.position = 'relative'
    el.style.zIndex = 0

    const watermarkDiv = document.createElement('div')
    watermarkDiv.style.cssText = `
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: url(${base64}) repeat;
      pointer-events: none;
      z-index: ${zIndex};
    `

    el.appendChild(watermarkDiv)
    el._watermarkDiv = watermarkDiv
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue && el._watermarkDiv) {
      el.removeChild(el._watermarkDiv)
      delete el._watermarkDiv
    }
  },
  unmounted(el) {
    if (el._watermarkDiv) {
      el.removeChild(el._watermarkDiv)
      delete el._watermarkDiv
    }
  }
}