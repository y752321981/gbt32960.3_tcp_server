<template>
  <div>
    <el-table :data="platformInfoList" style="width: 100%">
      <el-table-column label="Username" prop="username" width="180"></el-table-column>
      <el-table-column label="Node Name" prop="nodeName" width="180"></el-table-column>
      <el-table-column label="Channel ID" prop="channelId" width="180"></el-table-column>
      <el-table-column label="IP" prop="ip" width="180"></el-table-column>
      <el-table-column label="Last Heartbeat Time" prop="lastHeartbeatTime" width="200">
        <template slot-scope="scope">
          {{ formatDate(scope.row.lastHeartbeatTime) }}
        </template>
      </el-table-column>
      <el-table-column label="Login Time" prop="loginTime" width="200">
        <template slot-scope="scope">
          {{ formatDate(scope.row.loginTime) }}
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if='totalPages > 1'
      :current-page="current"
      :page-size="size"
      :total="total"
      layout="total, prev, pager, next, jumper"
      @current-change="handlePage"
    >
    </el-pagination>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
const platformInfoList = ref([])
const totalPages = ref(1)
const current = ref(1)
const size = ref(10)
const total = ref(0)

onMounted(() => {

});

const getNodeList = () => {
  fetch('/node/list')
      .then(res => res.json())
      .then(data => {
        servers.value = data
      });
}

const getVehicleList = () => {

}

</script>

<style scoped>
/* 可以根据需要自定义样式 */
</style>
