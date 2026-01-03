<template>
  <div class="borrow-stats-container p-4">
      <el-row :gutter="20">
          <el-col :span="8">
              <el-card shadow="hover">
                  <template #header>借阅总数</template>
                  <div class="text-3xl font-bold text-center py-4">--</div>
              </el-card>
          </el-col>
           <el-col :span="8">
              <el-card shadow="hover">
                  <template #header>今日借出</template>
                  <div class="text-3xl font-bold text-center py-4 text-green-500">--</div>
              </el-card>
          </el-col>
           <el-col :span="8">
              <el-card shadow="hover">
                  <template #header>当前已超期</template>
                  <div class="text-3xl font-bold text-center py-4 text-red-500">--</div>
              </el-card>
          </el-col>
      </el-row>

      <el-card class="mt-4">
          <template #header>借阅趋势</template>
          <div class="h-64 flex items-center justify-center text-gray-400">
              暂无图表插件 (需集成 ECharts)
          </div>
      </el-card>
  </div>
</template>
