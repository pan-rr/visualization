<template>
    <div style="display: contents;">
        <div style="margin-bottom: 20px;">
            <div class="el-descriptions__title">统计数据时间
                <el-date-picker type="date" v-model="time" placeholder="选择统计日期" format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd">
                </el-date-picker>
                <el-tooltip content="历史数据会不定期处理，因此会出现查无数据">
                    <el-button @click="execute">查询</el-button>
                </el-tooltip>
            </div>
        </div>
        <SpaceSelector :spaceRef="spaceRef" style="margin-bottom: 20px;"></SpaceSelector>
        <div v-show="this.chart">
            <el-descriptions title="流程任务执行统计结果" direction="vertical" border :column="3">
                <el-descriptions-item label="统计组">
                    {{ metricGroup }}
                </el-descriptions-item>
                <el-descriptions-item label="数据日期">
                    {{ metric?.time }}
                </el-descriptions-item>
                <el-descriptions-item label="总执行任务数">
                    <div v-if="metric?.totalCount != null">
                        <Tips message="总执行任务数指进入engine后执行过的任务数量,包括失败重试的任务数量"></Tips>
                        {{ metric?.totalCount }}
                    </div>
                    <div v-else>
                        暂无数据
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="已完成任务数">
                    <Tips message="进入engine后执行成功的任务数量，包含失败重试但最终成功的任务数、被终止实例中执行过的任务数"></Tips>
                    <div v-if="metric?.finishedCount != null">
                        {{ metric?.finishedCount }}
                    </div>
                    <div v-else>
                        暂无数据
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="仍然失败任务数">
                    <Tips message="在查询时间范围内，仍然执行失败的任务数量"></Tips>
                    <div v-if="metric?.errorCount != null">
                        {{ metric?.errorCount }}
                    </div>
                    <div v-else>
                        暂无数据
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="任务执行情况分布">
                    <div v-if="!chart?.['task_result_distribution']">
                        暂无数据
                    </div>
                    <div>
                        <canvas id="task_result_distribution"></canvas>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="实例耗时排行,单位:s">
                    <div v-if="!chart?.['instance_time_cost']">
                        暂无数据
                    </div>
                    <div>
                        <canvas id="instance_time_cost"></canvas>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="任务耗时排行,单位:s">
                    <div v-if="!chart?.['task_time_cost']">
                        暂无数据
                    </div>
                    <div>
                        <canvas id="task_time_cost"></canvas>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="模版失败任务数排行">
                    <div v-if="!chart?.['template_error_rank']">
                        暂无数据
                    </div>
                    <div>
                        <canvas id="template_error_rank"></canvas>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="实例失败任务数排行">
                    <div v-if="!chart?.['template_error_rank']">
                        暂无数据
                    </div>
                    <div>
                        <canvas id="instance_error_rank"></canvas>
                    </div>
                </el-descriptions-item>
                <el-descriptions-item label="未完成的任务">
                    <div>
                        <el-table border :data="metric?.unfinishedTasks">
                            <el-table-column prop="templateId" label="模版"></el-table-column>
                            <el-table-column prop="instanceId" label="实例"></el-table-column>
                            <el-table-column prop="taskId" label="任务"></el-table-column>
                        </el-table>
                    </div>
                </el-descriptions-item>
            </el-descriptions>
        </div>
    </div>
</template>

<script>

import { Message } from 'element-ui'
import Tips from '../../layout/components/Visual/Tips.vue';
import { Chart } from 'chart.js';
import { taskMetrics } from '../../api/metric';
import SpaceSelector from '../../layout/components/Visual/SpaceSelector.vue';
import { randomRGB, randomRGBArr } from '../../utils/rgb';


export default {
    components: {
        Tips, Chart, SpaceSelector
    },
    props: {
        options: {
            type: Object,
        }
    },
    data() {
        return {
            spaceRef: {
                data: ''
            },
            time: this.getCurrentDateStr(),
            metric: {

            },
            chart: {
            },
        }
    },
    mounted() {


    },
    methods: {
        destroyCharts() {
            for (let key in this.chart) {
                this.chart[key]?.destroy();
            }
        },
        validateTime() {
            if (!this.time) return '统计日期不能为空';
            let current = Date.parse(this.getCurrentDateStr());
            let chosenTime = new Date(this.time.replace(/-/g, ',')).getTime();
            if (current < chosenTime) return '统计日期不能选未来的日期';
            let dayDiff = (current - chosenTime) / 86400000;
            return dayDiff <= 14 ? null : '统计日期范围[T-14，T],T为今天';
        },
        injectRequestParam() {
            this.metric.space = this.spaceRef.data
            this.metric.id = this.config?.id;
            this.metric.mode = this.config?.mode;
            this.metric.time = this.time;
        },
        renderChart() {
            this.chart = {};
            if (!this?.metric?.nameValueMetric) {
                this.$set(this, 'chart', this.chart);
                return;
            }
            let tar = this?.metric?.nameValueMetric
            for (let key in tar) {
                let arr = this.chartData(tar[key])
                if (arr != null && arr.data.length > 0) {
                    switch (key) {
                        case 'task_result_distribution':
                            this.chart[key] = new Chart(document.getElementById('task_result_distribution'), {
                                type: 'doughnut',
                                data: {
                                    labels: arr.labels,
                                    datasets: [{
                                        data: arr.data,
                                        borderWidth: 1,
                                        backgroundColor: randomRGBArr(arr?.data?.length),
                                    }],

                                },
                            });
                            break;
                        case 'task_time_cost':
                            this.chart[key] = new Chart(document.getElementById('task_time_cost'), {
                                type: 'line',
                                data: {
                                    labels: arr.labels,
                                    datasets: [{
                                        label: '任务耗时排行',
                                        data: arr.data, fill: false,
                                        borderColor: randomRGB(),
                                        backgroundColor: randomRGBArr(arr?.data?.length),
                                        tension: 10
                                    }],
                                },
                            });
                            break;
                        case 'instance_time_cost':
                            this.chart[key] = new Chart(document.getElementById('instance_time_cost'), {
                                type: 'line',
                                data: {
                                    labels: arr.labels,
                                    datasets: [{
                                        label: '实例耗时排行',
                                        data: arr.data, fill: false,
                                        borderColor: randomRGB(),
                                        backgroundColor: randomRGBArr(arr?.data?.length),
                                        tension: 10
                                    }],
                                },
                            }); break;
                        case 'template_error_rank':
                            this.chart[key] = new Chart(document.getElementById('template_error_rank'), {
                                type: 'line',
                                data: {
                                    labels: arr.labels,
                                    datasets: [{
                                        label: '流程模版任务错误排行',
                                        data: arr.data, fill: false,
                                        borderColor: 'rgb(75, 192, 192)',
                                        backgroundColor: randomRGBArr(arr?.data?.length),
                                        tension: 10
                                    }],
                                },
                            }); break;
                        case 'instance_error_rank':
                            this.chart[key] = new Chart(document.getElementById('instance_error_rank'), {
                                type: 'line',
                                data: {
                                    labels: arr.labels,
                                    datasets: [{
                                        label: '实例任务错误排行',
                                        data: arr.data, fill: false,
                                        borderColor: 'rgb(75, 192, 192)',
                                        backgroundColor: randomRGBArr(arr?.data?.length),
                                        tension: 10
                                    }],
                                },
                            }); break;
                    }
                } else {
                    this.chart[key] = null;
                }
                this.$set(this, 'chart', this.chart);
            }
        },
        chartData(nameValueMetric) {
            if (nameValueMetric == null) return null;
            let res = {};
            res.labels = [];
            res.data = [];
            for (let item of nameValueMetric) {
                res.labels.push(item.name);
                res.data.push(item.value);
            }
            return res;
        },
        execute() {
            this.destroyCharts();
            this.injectRequestParam();
            let error = this.validateTime()
            if (error) {
                Message({
                    message: error,
                    type: 'error',
                    duration: 5 * 1000,
                })
            }
            taskMetrics(this.metric).then(res => {
                this.$set(this, 'metric', res.data.result);
            }).then(() => {
                this.renderChart();
            }).then(() => {
                this.renderChart();
            });

        },
        getCurrentDateStr() {
            let date = new Date();
            let y = date.getFullYear();
            let M = date.getMonth() + 1;
            M = M < 10 ? `0${M}` : M;
            let d = date.getDate();
            d = d < 10 ? `0${d}` : d;
            let res = `${y}-${M}-${d}`;
            return res;
        }
    },
    computed: {
        metricGroup() {
            switch (this?.config?.mode) {
                case 'templateId': return '模版';
                case 'instanceId': return '实例';
                default: return '存储空间';
            }
        },
        config() {
            let conf = {
                mode: 'space',
                id: this.spaceRef.data
            };
            conf = Object.assign(conf, this.options);
            return conf;
        }
    },
};
</script>