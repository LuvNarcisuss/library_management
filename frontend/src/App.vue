<template>
  <div id="app">
    <el-container v-if="isLoggedIn">
      <!-- 顶部导航栏 -->
      <el-header style="background-color: #409EFF; color: white; display: flex; justify-content: space-between; align-items: center; padding: 0 20px;">
        <h1 style="margin: 0;">图书管理系统</h1>
        <div>
          <span>欢迎，{{ user.username }}</span>
          <el-button type="text" @click="handleLogout" style="color: white; margin-left: 20px;">退出登录</el-button>
        </div>
      </el-header>
      
      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px" style="background-color: #304156;">
          <el-menu :default-active="activeIndex" class="el-menu-vertical-dark" @select="handleMenuSelect">
            <el-menu-item index="1">
              <i class="el-icon-book"></i>
              <span slot="title">图书管理</span>
            </el-menu-item>
            <el-menu-item index="2">
              <i class="el-icon-user"></i>
              <span slot="title">读者管理</span>
            </el-menu-item>
            <el-menu-item index="3">
              <i class="el-icon-document"></i>
              <span slot="title">借阅管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        
        <!-- 主内容区域 -->
        <el-main>
          <component :is="currentComponent"></component>
        </el-main>
      </el-container>
    </el-container>
    
    <!-- 登录页面 -->
    <login v-else @login-success="handleLoginSuccess"></login>
  </div>
</template>

<script>
import BookManager from './components/BookManager.vue'
import ReaderManager from './components/ReaderManager.vue'
import BorrowManager from './components/BorrowManager.vue'
import Login from './components/Login.vue'

export default {
  name: 'App',
  components: {
    BookManager,
    ReaderManager,
    BorrowManager,
    Login
  },
  data() {
    return {
      isLoggedIn: false,
      user: null,
      activeIndex: '1',
      currentComponent: 'BookManager'
    }
  },
  created() {
    // 禁用自动登录，用户必须手动登录
  },
  methods: {
    handleMenuSelect(key) {
      this.activeIndex = key
      switch(key) {
        case '1':
          this.currentComponent = 'BookManager'
          break
        case '2':
          this.currentComponent = 'ReaderManager'
          break
        case '3':
          this.currentComponent = 'BorrowManager'
          break
        default:
          this.currentComponent = 'BookManager'
      }
    },
    
    // 检查登录状态
    checkLoginStatus() {
      const user = localStorage.getItem('user')
      if (user) {
        this.isLoggedIn = true
        this.user = JSON.parse(user)
      }
    },
    
    // 登录成功处理
    handleLoginSuccess(user) {
      this.isLoggedIn = true
      this.user = user
      this.$message.success('登录成功')
    },
    
    // 退出登录处理
    handleLogout() {
      localStorage.removeItem('user')
      this.isLoggedIn = false
      this.user = null
      this.$message.info('已退出登录')
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  margin: 0;
}

.el-container {
  height: 100vh;
}

.el-header {
  padding: 0;
}

.el-menu {
  height: 100%;
}
</style>