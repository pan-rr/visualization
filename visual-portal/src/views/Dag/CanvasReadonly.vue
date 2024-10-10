<template>
    <div>
        <LFCanvas :templateId="templateId" :submitable="false"></LFCanvas>
    </div>

</template>

<script>

import LFCanvas from '../../layout/components/LogicFlow/LFCanvas.vue';



export default {
    name: "VisualCanvasReadonly",
    components: {
        LFCanvas
    },
    props: {
        templateId: String,
    },
};
</script>