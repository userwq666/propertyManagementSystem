import element from './element'

const plugins = {
  install(app) {
    app.use(element)
  }
}

export default plugins