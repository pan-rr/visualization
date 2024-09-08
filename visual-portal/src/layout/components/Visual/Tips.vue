<template>
    <div>
        <el-tooltip :content=message placement="top" effect="light">
            <i class="el-icon-info"></i>
        </el-tooltip>
    </div>
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