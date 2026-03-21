import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

axios.defaults.baseURL = '/api'
Vue.prototype.$http = axios

axios.interceptors.request.use(
  function(config) {
    return config
  },
  function(error) {
    return Promise.reject(error)
  }
)

axios.interceptors.response.use(
  function(response) {
    return response
  },
  function(error) {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('user')
      window.location.reload()
    } else if (error.response && error.response.status === 403) {
      Vue.prototype.$message.error('您没有权限执行此操作')
    } else {
      Vue.prototype.$message.error('请求失败，请稍后重试')
    }
    return Promise.reject(error)
  }
)

Vue.use(ElementUI)

new Vue({
  render: h => h(App)
}).$mount('#app')