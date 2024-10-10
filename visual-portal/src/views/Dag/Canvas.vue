<template>
    <div>
        <LFCanvas :submitable="true"></LFCanvas>
    </div>

</template>

<script>

import LFCanvas from '../../layout/components/LogicFlow/LFCanvas.vue';



export default {
    name: "VisualCanvas",
    components: {
        LFCanvas
    },


};
</script>