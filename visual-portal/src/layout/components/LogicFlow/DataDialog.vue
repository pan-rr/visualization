<template>
  <div>
    <json-viewer :value="graphData" :expand-depth=3 :copyable=true></json-viewer>
  </div>
</template>
<script>


export default {
  props: {
    graphData: Object
  },
  components: {

  },
};
</script>
<style scoped>
</style>
