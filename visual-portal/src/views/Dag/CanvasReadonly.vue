<template>
    <div>
        <!-- <LFCanvas :templateId="templateId" :submitable="false"></LFCanvas> -->
        <DAGCanvas  :templateId="templateId" :submitable="false"></DAGCanvas>
    </div>

</template>

<script>

import DAGCanvas from '../../layout/components/LogicFlow/DAGCanvas.vue';
import LFCanvas from '../../layout/components/LogicFlow/LFCanvas.vue';



export default {
    name: "VisualCanvasReadonly",
    components: {
        LFCanvas,DAGCanvas
    },
    props: {
        templateId: String,
    },
};
</script>