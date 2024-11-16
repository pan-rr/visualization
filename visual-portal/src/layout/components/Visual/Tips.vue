<template>
    <el-tooltip :content=message placement="top" effect="light">
        <i class="el-icon-info"></i>
    </el-tooltip>
</template>

<script>
    export default {
        props: {
            message: {
                type: String
            },
        },

    };
</script>