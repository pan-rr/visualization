<template>
  <div class="box t-center">
    <img src="@/assets/img/v.svg" alt="" />
    <h1 class="home pt_20 t-center font-size-20 c-3477F2">
      {{ message }}
    </h1>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      message: "Welcome to Visualization!"
    };
  }
};
</script>

<style lang="scss" scoped>
.box {
  padding-top: 100px;

  >img {
    width: 100px;
    height: auto;
    // margin-bottom: 10px;
    border-radius: 50%;
  }
}
</style>
