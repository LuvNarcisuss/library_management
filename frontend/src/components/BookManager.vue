<template>
  <div class="book-manager">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>图书管理</span>
        <el-button type="primary" style="float: right; padding: 3px 0" @click="dialogVisible = true">
          <i class="el-icon-plus"></i> 添加图书
        </el-button>
      </div>

      <!-- 搜索条件 -->
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="书名">
          <el-input v-model="searchForm.title" placeholder="请输入书名" @keyup.enter.native="searchBooks"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="searchForm.author" placeholder="请输入作者" @keyup.enter.native="searchBooks"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchBooks">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 图书列表 -->
      <el-table :data="books" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="书名" min-width="150"></el-table-column>
        <el-table-column prop="author" label="作者" width="120"></el-table-column>
        <el-table-column prop="isbn" label="ISBN" width="150"></el-table-column>
        <el-table-column prop="category" label="分类" width="100"></el-table-column>
        <el-table-column prop="publisher" label="出版社" min-width="120"></el-table-column>
        <el-table-column prop="price" label="价格" width="80">
          <template slot-scope="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 'AVAILABLE'">可借</el-tag>
            <el-tag type="warning" v-else-if="scope.row.status === 'BORROWED'">已借</el-tag>
            <el-tag type="danger" v-else>丢失</el-tag>
          </template>
        </el-table-column>
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
      <el-form :model="bookForm" ref="bookForm" label-width="80px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="bookForm.title" placeholder="请输入书名"></el-input>
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" placeholder="请输入作者"></el-input>
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" placeholder="请输入ISBN"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="bookForm.category" placeholder="请输入分类"></el-input>
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" placeholder="请输入出版社"></el-input>
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="bookForm.publishDate"
            type="date"
            placeholder="选择出版日期"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="bookForm.price" :min="0" :precision="2" placeholder="请输入价格"></el-input-number>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="bookForm.stock" :min="0" placeholder="请输入库存"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="bookForm.status" placeholder="请选择状态">
            <el-option label="可借" value="AVAILABLE"></el-option>
            <el-option label="已借" value="BORROWED"></el-option>
            <el-option label="丢失" value="LOST"></el-option>
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
  name: 'BookManager',
  data() {
    return {
      books: [],
      searchForm: {
        title: '',
        author: ''
      },
      dialogVisible: false,
      dialogTitle: '添加图书',
      bookForm: {
        id: null,
        title: '',
        author: '',
        isbn: '',
        category: '',
        publisher: '',
        publishDate: null,
        price: 0,
        stock: 0,
        status: 'AVAILABLE'
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      editing: false
    }
  },
  mounted() {
    this.loadBooks()
  },
  methods: {
    // 加载图书列表
    loadBooks() {
      this.$http.get('/books')
        .then(response => {
          this.books = response.data
          this.total = response.data.length
        })
        .catch(error => {
          this.$message.error('加载图书失败: ' + error.message)
        })
    },
    // 搜索图书
    searchBooks() {
      if (this.searchForm.title) {
        this.$http.get(`/books/search/title?title=${this.searchForm.title}`)
          .then(response => {
            this.books = response.data
            this.total = response.data.length
          })
          .catch(error => {
            this.$message.error('搜索图书失败: ' + error.message)
          })
      } else if (this.searchForm.author) {
        this.$http.get(`/books/search/author?author=${this.searchForm.author}`)
          .then(response => {
            this.books = response.data
            this.total = response.data.length
          })
          .catch(error => {
            this.$message.error('搜索图书失败: ' + error.message)
          })
      } else {
        this.loadBooks()
      }
    },
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        title: '',
        author: ''
      }
      this.loadBooks()
    },
    // 编辑图书
    handleEdit(row) {
      this.editing = true
      this.dialogTitle = '编辑图书'
      this.bookForm = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },
    // 删除图书
    handleDelete(row) {
      this.$confirm('确定要删除这本图书吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/books/${row.id}`)
          .then(() => {
            this.$message.success('删除成功')
            this.loadBooks()
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
        this.$http.put(`/books/${this.bookForm.id}`, this.bookForm)
          .then(() => {
            this.$message.success('更新成功')
            this.dialogVisible = false
            this.loadBooks()
          })
          .catch(error => {
            this.$message.error('更新失败: ' + error.message)
          })
      } else {
        this.$http.post('/books', this.bookForm)
          .then(() => {
            this.$message.success('添加成功')
            this.dialogVisible = false
            this.loadBooks()
          })
          .catch(error => {
            this.$message.error('添加失败: ' + error.message)
          })
      }
    },
    // 重置表单
    resetForm() {
      this.bookForm = {
        id: null,
        title: '',
        author: '',
        isbn: '',
        category: '',
        publisher: '',
        publishDate: null,
        price: 0,
        stock: 0,
        status: 'AVAILABLE'
      }
      this.editing = false
      this.dialogTitle = '添加图书'
    },
    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.loadBooks()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadBooks()
    }
  }
}
</script>

<style scoped>
.book-manager {
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>