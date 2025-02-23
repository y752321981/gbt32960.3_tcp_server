

<template>
  <el-header>
    <el-menu class="menu" :default-active="'Nodes'" mode="horizontal" @select="handleMenuSelect">
      <el-menu-item index="Nodes">服务器节点</el-menu-item>
      <el-menu-item index="Platforms">车辆列表</el-menu-item>
      <el-menu-item index="Vehicles">外部平台列表</el-menu-item>
    </el-menu>
  </el-header>
  <div class="main-page">
    <el-card>
      <div class="p3">
        <component :is="currentComponent"></component>
      </div>
    </el-card>
  </div>
</template>
<script>
import {defineAsyncComponent, ref} from "vue"
import {routes} from "../js/router.mjs";
import Nodes from './nodes.vue'
import Platforms from './platforms.vue'
import Vehicles from './vehicles.vue'

export default {
  name: 'index',
  components: {
    Node: Nodes,
    Platform: Platforms,
    Vehicle: Vehicles
  },
  setup() {
    const currentComponent = ref(defineAsyncComponent(() => {
      return import(routes["Nodes"])
    }))

    const handleMenuSelect = (index) => {
      currentComponent.value = defineAsyncComponent(() => {
        return import(routes[index])
      })
    }

    return {
      handleMenuSelect,
      currentComponent}
  }
}
</script>
<style scoped lang="less">
.main-page {
  padding: 20px;
  height: 100%;
}

.menu {
  margin-right: 20px;
  --el-menu-bg-color: #409EFF;
  --el-menu-text-color: #ffffff;
  --el-menu-active-color: #52e537;
}

.el-menu-item {
  font-size: 18px;
}
</style>