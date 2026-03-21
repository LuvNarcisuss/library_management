<template>
  <div class="login-container">
    <el-form :model="loginForm" :rules="loginRules" ref="loginForm" label-width="80px" class="login-form">
      <h3 class="login-title">欢迎登录图书管理系统</h3>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="loginForm.password" placeholder="请输入密码" @keyup.enter.native="handleLogin"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // 发送登录请求
          this.$http.post('/users/login', this.loginForm)
            .then(response => {
              if (response.data.success) {
                // 登录成功，保存用户信息到localStorage
                localStorage.setItem('user', JSON.stringify(response.data.user));
                // 通知父组件登录成功
                this.$emit('login-success', response.data.user);
              } else {
                this.$message.error(response.data.message);
              }
            })
            .catch(error => {
              console.error('登录失败:', error);
              let errorMessage = '登录失败，请检查网络连接';
              if (error.response) {
                errorMessage = error.response.data.message || errorMessage;
              }
              this.$message.error(errorMessage);
            });
        } else {
          return false;
        }
      });
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-form {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}
</style>