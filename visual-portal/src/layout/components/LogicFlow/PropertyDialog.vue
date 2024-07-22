<template>
  <div class="property-dialog">
      <CommonProperty
      :nodeData="nodeData"
      :lf="lf"
      @onClose="handleClose"/>
  </div>
</template>
<script>
import CommonProperty from './CommonProperty'


export default {
  name: 'PropertyDialog',
  components: {
    CommonProperty
  },
  props: {
    nodeData: Object,
    lf: Object
  },
  data () {
    return {}
  },
  methods: {
    handleClose () {
      this.$emit('setPropertiesFinish')
    }
  }
}
</script>
<style>
.property-dialog{
  padding: 20px;
}
</style>
