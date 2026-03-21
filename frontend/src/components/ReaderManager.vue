<template>
  <div class="reader-manager">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>读者管理</span>
        <el-button type="primary" style="float: right; padding: 3px 0" @click="dialogVisible = true">
          <i class="el-icon-plus"></i> 添加读者
        </el-button>
      </div>

      <!-- 搜索条件 -->
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" @keyup.enter.native="searchReaders"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" @keyup.enter.native="searchReaders"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchReaders">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 读者列表 -->
      <el-table :data="readers" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="130"></el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="150"></el-table-column>
        <el-table-column prop="readerType" label="读者类型" width="100">
          <template slot-scope="scope">
            <el-tag type="info" v-if="scope.row.readerType === 'STUDENT'">学生</el-tag>
            <el-tag type="success" v-else-if="scope.row.readerType === 'TEACHER'">教师</el-tag>
            <el-tag type="warning" v-else>会员</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 'ACTIVE'">激活</el-tag>
            <el-tag type="danger" v-else>冻结</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination" style="margin-top: 20px; text-align: right;">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="50%"
      @close="resetForm">
      <el-form :model="readerForm" ref="readerForm" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="readerForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="readerForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="readerForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="readerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="readerForm.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="读者类型" prop="readerType">
          <el-select v-model="readerForm.readerType" placeholder="请选择读者类型">
            <el-option label="学生" value="STUDENT"></el-option>
            <el-option label="教师" value="TEACHER"></el-option>
            <el-option label="会员" value="MEMBER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="readerForm.status" placeholder="请选择状态">
            <el-option label="激活" value="ACTIVE"></el-option>
            <el-option label="冻结" value="INACTIVE"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ReaderManager',
  data() {
    return {
      readers: [],
      searchForm: {
        name: '',
        phone: ''
      },
      dialogVisible: false,
      dialogTitle: '添加读者',
      readerForm: {
        id: null,
        name: '',
        idCard: '',
        phone: '',
        email: '',
        address: '',
        readerType: 'STUDENT',
        status: 'ACTIVE'
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      editing: false
    }
  },
  mounted() {
    this.loadReaders()
  },
  methods: {
    // 加载读者列表
    loadReaders() {
      this.$http.get('/readers')
        .then(response => {
          this.readers = response.data
          this.total = response.data.length
        })
        .catch(error => {
          this.$message.error('加载读者失败: ' + error.message)
        })
    },
    // 搜索读者
    searchReaders() {
      if (this.searchForm.name) {
        this.$http.get(`/readers/search/name?name=${this.searchForm.name}`)
          .then(response => {
            this.readers = response.data
            this.total = response.data.length
          })
          .catch(error => {
            this.$message.error('搜索读者失败: ' + error.message)
          })
      } else if (this.searchForm.phone) {
        this.$http.get(`/readers/search/phone?phone=${this.searchForm.phone}`)
          .then(response => {
            this.readers = response.data
            this.total = response.data.length
          })
          .catch(error => {
            this.$message.error('搜索读者失败: ' + error.message)
          })
      } else {
        this.loadReaders()
      }
    },
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        phone: ''
      }
      this.loadReaders()
    },
    // 编辑读者
    handleEdit(row) {
      this.editing = true
      this.dialogTitle = '编辑读者'
      this.readerForm = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },
    // 删除读者
    handleDelete(row) {
      this.$confirm('确定要删除这个读者吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/readers/${row.id}`)
          .then(() => {
            this.$message.success('删除成功')
            this.loadReaders()
          })
          .catch(error => {
            this.$message.error('删除失败: ' + error.message)
          })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    // 提交表单
    submitForm() {
      if (this.editing) {
        this.$http.put(`/readers/${this.readerForm.id}`, this.readerForm)
          .then(() => {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.loadReaders()
          })
          .catch(error => {
            this.$message.error('更新失败: ' + error.message)
          })
      } else {
        this.$http.post('/readers', this.readerForm)
          .then(() => {
            this.$message.success('添加成功')
            this.dialogVisible = false
            this.loadReaders()
          })
          .catch(error => {
            this.$message.error('添加失败: ' + error.message)
          })
      }
    },
    // 重置表单
    resetForm() {
      this.readerForm = {
        id: null,
        name: '',
        idCard: '',
        phone: '',
        email: '',
        address: '',
        readerType: 'STUDENT',
        status: 'ACTIVE'
      }
      this.editing = false
      this.dialogTitle = '添加读者'
    },
    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.loadReaders()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadReaders()
    }
  }
}
</script>

<style scoped>
.reader-manager {
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>