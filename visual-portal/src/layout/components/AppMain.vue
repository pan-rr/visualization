<template>
  <section class="app_main flex">
    <SliderBar />
    <el-container>
      <el-header height="80px">
        <NavBar />
      </el-header>
      <el-main>
        <transition name="fade-transform" mode="out-in">
          <keep-alive :include="cachedViews">
            <router-view :key="$route.fullPath" />
          </keep-alive>
        </transition>
      </el-main>
    </el-container>
  </section>
</template>

<script>
import SliderBar from "./SliderBar/index.vue";
import NavBar from "./NavBar/index.vue";
import watermark from 'watermark-dom'

export default {
  components: { SliderBar, NavBar },
  data() {
    return {
      cachedViews: this.$store.getters.cachedViews,
      conf: {
        watermark_txt: 'author:pan-rr',
        watermark_alpha: 0.15,
        watermark_width: 150
      },
      timmer: null
    };
  },
  mounted() {
    const conf = this.conf
    this.timmer = setInterval(() => {
      if (conf.watermark_alpha === 0.15) {
        conf.watermark_alpha = 0.05
      } else {
        conf.watermark_alpha = 0.15
      }
      watermark.load(conf)
    }, 8000)
  },
  destroyed() {
    clearInterval(this.timmer);
  }
};
</script>

<style lang="scss" scoped>
.app_main {
  height: 100%;

  .el-header {
    box-shadow: 0 3px 5px rgba(0, 0, 0, 0.3);
  }
}

.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.2s;
  opacity: 0;
  transform: translateX(30px);
}
</style>
