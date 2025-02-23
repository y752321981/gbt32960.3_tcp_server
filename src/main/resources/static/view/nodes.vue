<template>
  <div>
    <h1>节点列表</h1>

    <!-- 卡片展示服务器列表 -->
    <div class="card-container">
      <el-card v-for="server in servers" :key="server.name" class="card-item" shadow="hover">
        <div slot="header" class="clearfix">
          <!-- 加粗显示服务器名称 -->
          <span class="server-name">{{ server.name }}</span>
        </div>
        <div>
          <p><strong>Host:</strong> {{ server.host }}</p>
          <p><strong>Alive Vehicles:</strong> {{ server.aliveVehicleCount }}</p>
          <p><strong>Alive Platforms:</strong> {{ server.alivePlatformCount }}</p>
          <el-button @click="gotoVehicle(server)" type="primary">View Details</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
const servers = ref([]);

onMounted(() => {
  getNodeList();
})

const getNodeList = () => {
  fetch('/node/list')
      .then(res => res.json())
      .then(data => {
        servers.value = data
      });
}
const gotoVehicle = (server) => {
}


</script>

<style scoped>
.card-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.card-item {
  width: 280px;
}
.server-name {
  font-weight: bold;  /* CSS 加粗 */
}
</style>
