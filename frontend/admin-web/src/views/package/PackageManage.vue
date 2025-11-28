<template>
  <div class="package-manage">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2 class="page-title">套餐管理</h2>
      <div class="page-actions">
        <el-button type="primary" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          新建套餐
        </el-button>
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <el-card class="search-card">
        <el-form :model="searchForm" inline>
          <el-form-item label="套餐名称">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入套餐名称或代码"
              clearable
              style="width: 240px"
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          
          <el-form-item label="分类">
            <el-select
              v-model="searchForm.categoryId"
              placeholder="选择分类"
              clearable
              style="width: 200px"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select
              v-model="searchForm.status"
              placeholder="选择状态"
              clearable
              style="width: 120px"
            >
              <el-option label="全部" :value="null" />
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetSearch">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedPackages.length > 0">
      <el-card class="batch-card">
        <div class="batch-content">
          <span>已选择 {{ selectedPackages.length }} 项</span>
          <el-button-group>
            <el-button type="success" @click="batchPublish">
              <el-icon><Upload /></el-icon>
              批量上架
            </el-button>
            <el-button type="warning" @click="batchUnpublish">
              <el-icon><Download /></el-icon>
              批量下架
            </el-button>
            <el-button type="danger" @click="batchDelete">
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
          </el-button-group>
        </div>
      </el-card>
    </div>

    <!-- 套餐列表 -->
    <div class="package-list">
      <el-table
        ref="packageTable"
        v-loading="loading"
        :data="packageList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
        border
        stripe
      >
        <el-table-column type="selection" width="50" align="center" />
        
        <el-table-column prop="coverImage" label="封面" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.coverImage || '/images/default-package.jpg'"
              :preview-src-list="[row.coverImage || '/images/default-package.jpg']"
              style="width: 60px; height: 60px"
              fit="cover"
              :lazy="true"
            />
          </template>
        </el-table-column>

        <el-table-column prop="name" label="套餐名称" min-width="200" show-overflow-tooltip />
        
        <el-table-column prop="categoryName" label="分类" width="120" />
        
        <el-table-column prop="code" label="套餐代码" width="150" />
        
        <el-table-column prop="price" label="价格" width="100" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
            <span class="original-price" v-if="row.originalPrice && row.originalPrice > row.price">
              ¥{{ row.originalPrice }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="80" align="center" />
        
        <el-table-column prop="salesCount" label="销量" width="80" align="center" />
        
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="isRecommended" label="推荐" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.isRecommended"
              @change="handleRecommendedChange(row)"
              active-text="是"
              inactive-text="否"
            />
          </template>
        </el-table-column>

        <el-table-column prop="isHot" label="热门" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.isHot"
              @change="handleHotChange(row)"
              active-text="是"
              inactive-text="否"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button type="primary" size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button 
                :type="row.status === 1 ? 'warning' : 'success'" 
                size="small" 
                @click="handleToggleStatus(row)"
              >
                <el-icon><Switch /></el-icon>
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 创建/编辑套餐对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'create' ? '新建套餐' : '编辑套餐'"
      width="900px"
      destroy-on-close
    >
      <el-form
        ref="packageFormRef"
        :model="packageForm"
        :rules="packageRules"
        label-width="100px"
        label-position="left"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐名称" prop="name">
              <el-input v-model="packageForm.name" placeholder="请输入套餐名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="套餐代码" prop="code">
              <el-input v-model="packageForm.code" placeholder="请输入套餐代码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐分类" prop="categoryId">
              <el-select v-model="packageForm.categoryId" placeholder="选择分类">
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存数量" prop="stock">
              <el-input-number
                v-model="packageForm.stock"
                :min="0"
                :max="999999"
                placeholder="请输入库存数量"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="套餐价格" prop="price">
              <el-input-number
                v-model="packageForm.price"
                :min="0.01"
                :precision="2"
                placeholder="请输入套餐价格"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number
                v-model="packageForm.originalPrice"
                :min="0"
                :precision="2"
                placeholder="请输入原价"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采样方式" prop="samplingMethod">
              <el-select v-model="packageForm.samplingMethod" placeholder="选择采样方式">
                <el-option label="自采样" :value="1" />
                <el-option label="上门采样" :value="2" />
                <el-option label="两种方式" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="交付天数" prop="reportDeliveryDays">
              <el-input-number
                v-model="packageForm.reportDeliveryDays"
                :min="1"
                :max="30"
                placeholder="报告交付天数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="套餐描述" prop="description">
          <el-input
            v-model="packageForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入套餐描述"
          />
        </el-form-item>

        <el-form-item label="检测项目">
          <el-select
            v-model="packageForm.testItems"
            multiple
            filterable
            placeholder="选择检测项目"
            style="width: 100%"
          >
            <el-option
              v-for="item in testItemsOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="封面图片">
          <el-upload
            ref="coverUploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :before-upload="beforeCoverUpload"
            :on-success="handleCoverSuccess"
            :on-error="handleUploadError"
            accept="image/*"
            list-type="picture-card"
            :limit="1"
            v-model:file-list="coverFileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="详情图片">
          <el-upload
            ref="detailUploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :before-upload="beforeDetailUpload"
            :on-success="handleDetailSuccess"
            :on-error="handleUploadError"
            accept="image/*"
            list-type="picture-card"
            :limit="5"
            v-model:file-list="detailFileList"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="推荐套餐">
              <el-switch v-model="packageForm.isRecommended" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="热门套餐">
              <el-switch v-model="packageForm.isHot" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="套餐状态">
              <el-switch
                v-model="packageForm.status"
                :active-value="1"
                :inactive-value="0"
                active-text="上架"
                inactive-text="下架"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标签">
          <el-select
            v-model="packageForm.tags"
            multiple
            filterable
            allow-create
            placeholder="输入标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in tagOptions"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="适用人群">
          <el-input
            v-model="packageForm.suitableFor"
            type="textarea"
            :rows="2"
            placeholder="请描述适用人群"
          />
        </el-form-item>

        <el-form-item label="禁用人群">
          <el-input
            v-model="packageForm.notSuitableFor"
            type="textarea"
            :rows="2"
            placeholder="请描述禁用人群"
          />
        </el-form-item>

        <el-form-item label="注意事项">
          <el-input
            v-model="packageForm.notice"
            type="textarea"
            :rows="3"
            placeholder="请输入注意事项"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            {{ dialogMode === 'create' ? '创建' : '更新' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  Plus,
  Refresh,
  Search,
  Upload,
  Download,
  Delete,
  Edit,
  Switch
} from '@element-plus/icons-vue'

export default {
  name: 'PackageManage',

  components: {
    Plus,
    Refresh,
    Search,
    Upload,
    Download,
    Delete,
    Edit,
    Switch
  },

  data() {
    return {
      loading: false,
      packageList: [],
      selectedPackages: [],
      categories: [],
      testItemsOptions: [
        '血常规', '尿常规', '肝功能', '肾功能', '血糖', '血脂',
        '甲状腺功能', '肿瘤标志物', '心电图', '胸部CT', '腹部B超',
        '骨密度', '维生素D', '免疫功能', '过敏原检测'
      ],
      tagOptions: [
        '推荐', '热门', '限时优惠', '新品', '权威', '精准',
        '快速报告', '上门服务', '双采样', 'VIP专享'
      ],
      
      // 搜索表单
      searchForm: {
        keyword: '',
        categoryId: null,
        status: null
      },
      
      // 分页信息
      pagination: {
        current: 1,
        size: 20,
        total: 0
      },
      
      // 对话框状态
      dialogVisible: false,
      dialogMode: 'create',
      submitting: false,
      
      // 套餐表单
      packageForm: {
        name: '',
        code: '',
        categoryId: null,
        description: '',
        price: 0,
        originalPrice: 0,
        samplingMethod: 1,
        reportDeliveryDays: 7,
        stock: 0,
        testItems: [],
        coverImage: '',
        detailImages: [],
        isRecommended: false,
        isHot: false,
        status: 1,
        tags: [],
        suitableFor: '',
        notSuitableFor: '',
        notice: ''
      },
      
      packageRules: {
        name: [
          { required: true, message: '请输入套餐名称', trigger: 'blur' },
          { min: 2, max: 100, message: '套餐名称长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入套餐代码', trigger: 'blur' },
          { pattern: /^[A-Z_]{2,30}$/, message: '套餐代码只能包含大写字母和下划线，长度2-30位', trigger: 'blur' }
        ],
        categoryId: [{ required: true, message: '请选择套餐分类', trigger: 'change' }],
        price: [{ required: true, message: '请输入套餐价格', trigger: 'blur' }],
        samplingMethod: [{ required: true, message: '请选择采样方式', trigger: 'change' }],
        reportDeliveryDays: [{ required: true, message: '请输入报告交付天数', trigger: 'blur' }]
      },
      
      // 文件上传相关
      coverFileList: [],
      detailFileList: [],
      uploadHeaders: {
        'Authorization': `Bearer ${this.$store.getters.token}`
      }
    }
  },

  computed: {
    uploadAction() {
      return `${this.$config.baseURL}/admin/upload/image`
    }
  },

  methods: {
    // 获取套餐列表
    async fetchPackages() {
      try {
        this.loading = true
        
        const params = {
          page: this.pagination.current,
          size: this.pagination.size,
          ...this.searchForm
        }
        
        if (!params.keyword) delete params.keyword
        if (!params.categoryId) delete params.categoryId
        if (params.status === null) delete params.status
        
        const response = await this.$api.admin.getPackages(params)
        
        this.packageList = response.data.records || []
        this.pagination.total = response.data.total
      } catch (error) {
        console.error('获取套餐列表失败:', error)
        this.$message.error('获取套餐列表失败')
      } finally {
        this.loading = false
      }
    },

    // 获取分类列表
    async fetchCategories() {
      try {
        const response = await this.$api.admin.getCategories()
        this.categories = response.data || []
      } catch (error) {
        console.error('获取分类列表失败:', error)
      }
    },

    // 搜索相关方法
    handleSearch() {
      this.pagination.current = 1
      this.fetchPackages()
    },

    resetSearch() {
      this.searchForm = {
        keyword: '',
        categoryId: null,
        status: null
      }
      this.handleSearch()
    },

    // 分页相关方法
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.fetchPackages()
    },

    handleCurrentChange(page) {
      this.pagination.current = page
      this.fetchPackages()
    },

    // 选中相关方法
    handleSelectionChange(selection) {
      this.selectedPackages = selection
    },

    // 批量操作
    async batchPublish() {
      if (this.selectedPackages.length === 0) {
        this.$message.warning('请选择要操作的套餐')
        return
      }
      
      const packageIds = this.selectedPackages.map(p => p.id).join(',')
      await this.$api.admin.batchUpdatePackageStatus(packageIds, 1)
      
      this.$message.success('批量上架成功')
      this.fetchPackages()
      this.$refs.packageTable.clearSelection()
    },

    async batchUnpublish() {
      if (this.selectedPackages.length === 0) {
        this.$message.warning('请选择要操作的套餐')
        return
      }
      
      const packageIds = this.selectedPackages.map(p => p.id).join(',')
      await this.$api.admin.batchUpdatePackageStatus(packageIds, 0)
      
      this.$message.success('批量下架成功')
      this.fetchPackages()
      this.$refs.packageTable.clearSelection()
    },

    async batchDelete() {
      if (this.selectedPackages.length === 0) {
        this.$message.warning('请选择要删除的套餐')
        return
      }
      
      await this.$confirm('确定要删除选中的套餐吗？此操作不可恢复！', '警告', {
        type: 'warning'
      })
      
      const packageIds = this.selectedPackages.map(p => p.id).join(',')
      await this.$api.admin.batchDeletePackages(packageIds)
      
      this.$message.success('批量删除成功')
      this.fetchPackages()
      this.$refs.packageTable.clearSelection()
    },

    // 单个操作
    async handleRecommendedChange(packageData) {
      try {
        await this.$api.admin.setRecommended(packageData.id, packageData.isRecommended)
        this.$message.success(packageData.isRecommended ? '已设为推荐' : '已取消推荐')
      } catch (error) {
        packageData.isRecommended = !packageData.isRecommended
        this.$message.error('操作失败')
      }
    },

    async handleHotChange(packageData) {
      try {
        await this.$api.admin.setHot(packageData.id, packageData.isHot)
        this.$message.success(packageData.isHot ? '已设为热门' : '已取消热门')
      } catch (error) {
        packageData.isHot = !packageData.isHot
        this.$message.error('操作失败')
      }
    },

    async handleToggleStatus(packageData) {
      const newStatus = packageData.status === 1 ? 0 : 1
      try {
        await this.$api.admin.updatePackageStatus(packageData.id, newStatus)
        packageData.status = newStatus
        this.$message.success(newStatus === 1 ? '套餐已上架' : '套餐已下架')
      } catch (error) {
        this.$message.error('操作失败')
      }
    },

    handleEdit(packageData) {
      this.dialogMode = 'edit'
      Object.assign(this.packageForm, packageData)
      this.dialogVisible = true
      
      // 设置文件列表
      if (packageData.coverImage) {
        this.coverFileList = [{
          name: '封面图片',
          url: packageData.coverImage
        }]
      }
      
      if (packageData.detailImages && packageData.detailImages.length > 0) {
        this.detailFileList = packageData.detailImages.map((url, index) => ({
          name: `详情图${index + 1}`,
          url: url
        }))
      }
    },

    async handleDelete(packageData) {
      await this.$confirm('确定要删除这个套餐吗？此操作不可恢复！', '警告', {
        type: 'warning'
      })
      
      try {
        await this.$api.admin.deletePackage(packageData.id)
        this.$message.success('套餐删除成功')
        this.fetchPackages()
      } catch (error) {
        this.$message.error('删除失败')
      }
    },

    // 对话框操作
    openCreateDialog() {
      this.dialogMode = 'create'
      this.resetForm()
      this.dialogVisible = true
    },

    resetForm() {
      this.packageForm = {
        name: '',
        code: '',
        categoryId: null,
        description: '',
        price: 0,
        originalPrice: 0,
        samplingMethod: 1,
        reportDeliveryDays: 7,
        stock: 0,
        testItems: [],
        coverImage: '',
        detailImages: [],
        isRecommended: false,
        isHot: false,
        status: 1,
        tags: [],
        suitableFor: '',
        notSuitableFor: '',
        notice: ''
      }
      this.coverFileList = []
      this.detailFileList = []
      this.$refs.packageFormRef?.resetFields()
    },

    submitForm() {
      this.$refs.packageFormRef.validate(async (valid) => {
        if (!valid) return
        
        this.submitting = true
        try {
          if (this.dialogMode === 'create') {
            await this.$api.admin.createPackage(this.packageForm)
            this.$message.success('套餐创建成功')
          } else {
            await this.$api.admin.updatePackage(this.packageForm.id, this.packageForm)
            this.$message.success('套餐更新成功')
          }
          
          this.dialogVisible = false
          this.fetchPackages()
        } catch (error) {
          this.$message.error('保存失败')
        } finally {
          this.submitting = false
        }
      })
    },

    // 文件上传相关
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
      }
      
      return isImage && isLt5M
    },

    handleCoverSuccess(response, file) {
      if (response.success) {
        this.packageForm.coverImage = response.data.fileUrl
        this.$message.success('封面上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },

    beforeDetailUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
      }
      if (!isLt5M) {
        this.$message.error('图片大小不能超过 5MB!')
      }
      
      return isImage && isLt5M
    },

    handleDetailSuccess(response, file) {
      if (response.success) {
        this.packageForm.detailImages.push(response.data.fileUrl)
        this.$message.success('详情图片上传成功')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },

    handleUploadError(error) {
      this.$message.error('上传失败: ' + error.message)
    },

    // 刷新数据
    refreshData() {
      this.fetchPackages()
    }
  },

  mounted() {
    this.fetchPackages()
    this.fetchCategories()
  }
}
</script>

<style lang="scss" scoped>
.package-manage {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .page-title {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: #333;
    }

    .page-actions {
      display: flex;
      gap: 12px;
    }
  }

  .search-section {
    margin-bottom: 20px;

    .search-card {
      padding: 20px;
    }
  }

  .batch-actions {
    margin-bottom: 20px;

    .batch-card {
      padding: 16px;

      .batch-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
    }
  }

  .package-list {
    background: #fff;
    border-radius: 8px;
    padding: 20px;

    .price {
      color: #ff4757;
      font-weight: 600;
    }

    .original-price {
      color: #999;
      text-decoration: line-through;
      margin-left: 8px;
      font-size: 12px;
    }

    .pagination-section {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }

  .dialog-footer {
    text-align: right;
  }
}
</style>