<template>
  <div class="borrow-manager">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span>借阅管理</span>
        <el-button type="primary" style="float: right; padding: 3px 0" @click="showBorrowDialog = true">
          <i class="el-icon-plus"></i> 借阅图书
        </el-button>
      </div>

      <!-- 借阅统计 -->
      <div class="stats" style="margin-bottom: 20px;">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">当前借阅</span>
                <span class="stat-value">{{ currentBorrowedCount }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">已归还</span>
                <span class="stat-value">{{ returnedCount }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">逾期未还</span>
                <span class="stat-value">{{ overdueCount }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card>
              <div class="stat-item">
                <span class="stat-label">总借阅量</span>
                <span class="stat-value">{{ totalCount }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 借阅记录列表 -->
      <el-table :data="borrowRecords" stripe style="width: 100%">
        <el-table-column prop="id" label="记录ID" width="100"></el-table-column>
        <el-table-column label="图书信息" min-width="250">
          <template slot-scope="scope">
            <div>{{ scope.row.book.title }}</div>
            <div style="font-size: 12px; color: #999;">作者: {{ scope.row.book.author }} | ISBN: {{ scope.row.book.isbn }}</div>
          </template>
        </el-table-column>
        <el-table-column label="读者信息" min-width="200">
          <template slot-scope="scope">
            <div>{{ scope.row.reader.name }}</div>
            <div style="font-size: 12px; color: #999;">手机号: {{ scope.row.reader.phone }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="borrowDate" label="借阅日期" width="150">
          <template slot-scope="scope">
            {{ new Date(scope.row.borrowDate).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="到期日期" width="150">
          <template slot-scope="scope">
            <span :style="{color: isOverdue(scope.row.dueDate) ? 'red' : ''}">
              {{ new Date(scope.row.dueDate).toLocaleDateString() }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="returnDate" label="归还日期" width="150">
          <template slot-scope="scope">
            {{ scope.row.returnDate ? new Date(scope.row.returnDate).toLocaleDateString() : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag type="warning" v-if="scope.row.status === 'BORROWED'">借阅中</el-tag>
            <el-tag type="success" v-else-if="scope.row.status === 'RETURNED'">已归还</el-tag>
            <el-tag type="danger" v-else>已逾期</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template slot-scope="scope">
            <el-button type="success" size="small" @click="handleReturn(scope.row)" v-if="scope.row.status === 'BORROWED'">
              归还
            </el-button>
            <el-button disabled size="small" v-else>
              已归还
            </el-button>
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

    <!-- 借阅对话框 -->
    <el-dialog
      title="借阅图书"
      :visible.sync="showBorrowDialog"
      width="50%"
      @close="resetBorrowForm">
      <el-form :model="borrowForm" ref="borrowForm" label-width="80px">
        <el-form-item label="图书ID" prop="bookId">
          <el-input v-model.number="borrowForm.bookId" placeholder="请输入图书ID"></el-input>
        </el-form-item>
        <el-form-item label="读者ID" prop="readerId">
          <el-input v-model.number="borrowForm.readerId" placeholder="请输入读者ID"></el-input>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number v-model="borrowForm.days" :min="1" :max="90" :step="1" placeholder="请输入借阅天数"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showBorrowDialog = false">取消</el-button>
        <el-button type="primary" @click="submitBorrow">确定借阅</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'BorrowManager',
  data() {
    return {
      borrowRecords: [],
      showBorrowDialog: false,
      borrowForm: {
        bookId: null,
        readerId: null,
        days: 30
      },
      currentPage: 1,
      pageSize: 10,
      total: 0,
      currentBorrowedCount: 0,
      returnedCount: 0,
      overdueCount: 0
    }
  },
  computed: {
    totalCount() {
      return this.borrowRecords.length
    }
  },
  mounted() {
    this.loadBorrowRecords()
  },
  methods: {
    // 加载借阅记录
    loadBorrowRecords() {
      this.$http.get('/borrow')
        .then(response => {
          this.borrowRecords = response.data
          this.total = response.data.length
          this.calculateStats()
        })
        .catch(error => {
          this.$message.error('加载借阅记录失败: ' + error.message)
        })
    },
    // 计算统计数据
    calculateStats() {
      this.currentBorrowedCount = this.borrowRecords.filter(record => record.status === 'BORROWED').length
      this.returnedCount = this.borrowRecords.filter(record => record.status === 'RETURNED').length
      this.overdueCount = this.borrowRecords.filter(record => record.status === 'OVERDUE').length
    },
    // 判断是否逾期
    isOverdue(dueDate) {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      const due = new Date(dueDate)
      due.setHours(0, 0, 0, 0)
      return today > due
    },
    // 归还图书
    handleReturn(row) {
      this.$confirm('确定要归还这本图书吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$http.put(`/borrow/return/${row.id}`)
          .then(() => {
            this.$message.success('归还成功')
            this.loadBorrowRecords()
          })
          .catch(error => {
            this.$message.error('归还失败: ' + error.message)
          })
      }).catch(() => {
        this.$message.info('已取消归还')
      })
    },
    // 提交借阅
    submitBorrow() {
      if (!this.borrowForm.bookId || !this.borrowForm.readerId) {
        this.$message.error('请填写完整的借阅信息')
        return
      }

      this.$http.post(`/borrow/borrow?bookId=${this.borrowForm.bookId}&readerId=${this.borrowForm.readerId}&days=${this.borrowForm.days}`)
        .then(() => {
          this.$message.success('借阅成功')
          this.showBorrowDialog = false
          this.loadBorrowRecords()
        })
        .catch(error => {
          this.$message.error('借阅失败: ' + error.message)
        })
    },
    // 重置借阅表单
    resetBorrowForm() {
      this.borrowForm = {
        bookId: null,
        readerId: null,
        days: 30
      }
    },
    // 分页处理
    handleSizeChange(val) {
      this.pageSize = val
      this.loadBorrowRecords()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadBorrowRecords()
    }
  }
}
</script>

<style scoped>
.borrow-manager {
  padding: 20px;
}

.stats {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>