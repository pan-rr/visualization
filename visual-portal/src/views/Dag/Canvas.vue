<template>
    <div>
        <!-- <LFCanvas :submitable="true"></LFCanvas> -->
         <DAGCanvas :submitable="true"></DAGCanvas>
    </div>

</template>

<script>

import LFCanvas from '../../layout/components/LogicFlow/LFCanvas.vue';
import DAGCanvas from '../../layout/components/LogicFlow/DAGCanvas.vue';



export default {
    name: "VisualCanvas",
    components: {
        LFCanvas,DAGCanvas
    },


};
</script>