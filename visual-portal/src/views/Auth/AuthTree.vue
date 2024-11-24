<template>
    <div>
        <div class="graphBox" ref="graphBox">
            <el-button type="info" icon='el-icon-refresh' @click="refresh" size="mini">调整画布中心</el-button>
            <div id="graphContainer">
            </div>
        </div>
        <div><el-tag id="tooltip" type="info" style="display: none;"></el-tag></div>
        <div>
            <el-dialog title="节点操作面板" :visible.sync="nodeDialog.show">
                <ResourceCreate v-if="nodeDialog.level === 1 && nodeDialog.theme === 'auth:resource:add'"
                    @finish="closeDialog"></ResourceCreate>
                <!-- <PermissionManagement v-else-if="nodeDialog.level === 1 && nodeDialog.theme === 'auth:permission:add'"
                    @finish="closeDialog"></PermissionManagement> -->
                <PermissionCreate v-else-if="nodeDialog.level === 1 && nodeDialog.theme === 'auth:permission:add'"
                    @finish="closeDialog"></PermissionCreate>
                <div v-else>无可执行操作</div>
            </el-dialog>
        </div>

    </div>
</template>

<style lang="css">
.graphBox {
    width: 100%;
    border: 1px solid #DCDFE6;
}
</style>

<script>

import G6, { Tooltip } from '@antv/g6';
import { getCurrentTenant } from '../../utils/tenantUtil';
import { getGraph } from '../../api/authGraph';
import ResourceCreate from '../../layout/components/Resource/ResourceCreate.vue';
import { drawAsideButton } from '../../utils/authTree';
import PermissionCreate from '../../layout/components/Resource/PermissionCreate.vue';



export default {
    components: { ResourceCreate, PermissionCreate },
    data() {
        return {
            nodeDialog: {
                level: 0,
                show: false,
                theme: '',
                target: {}
            },
            observer: {},
            graphData: {
                nodes: [],
                edges: []
            },
            graph: undefined,
            set: new Set(),
        }
    },
    mounted() {
        this.registerNode();
        this.graphOption.width = window.innerWidth;
        this.setRoot();
        this.initGraph();
        this.graph.data(this.graphData);
        let container = document.getElementById('graphContainer');
        this.observer = new ResizeObserver(this.render);
        this.observer.observe(container, { box: "border-box" });
        this.graph.render();
    },
    beforeDestroy() {
        this.observer.disconnect();
    },
    methods: {
        closeDialog(value) {
            if (value === true) this.getChildren(this.nodeDialog.target);
            this.nodeDialog.show = false;
            this.nodeDialog.level = 0;
            this.nodeDialog.target = null;
        },
        refresh() {
            this.graph.fitCenter();
        },
        registerNode() {
            const _this = this;
            G6.registerNode('normal', {
                drawShape: function drawShape(cfg, group) {
                    let margin = 6;
                    let padding = 6;
                    let buttonSize = 13;
                    let rect = group.addShape('rect', {
                        attrs: {
                            fill: '#fff',
                            stroke: '#666',
                        },
                        draggable: true
                    });
                    let content;
                    switch (cfg.level) {
                        case 1: content = `组织:${cfg.name}\n层级:${cfg.level}`; break;
                        case 2: content = `权限:${cfg.name}\n层级:${cfg.level}`; break;
                        case 3: content = `用户:${cfg.name}\n层级:${cfg.level}`; break;
                        default: content = cfg.name;
                    }
                    content = content.replace(/(.{19})/g, '$1\n');
                    let text = group.addShape('text', {
                        attrs: {
                            text: content,
                            textAlign: 'left',
                            textBaseline: 'middle',
                            fill: '#061161',
                            fontSize: 18
                        },
                        draggable: true
                    });
                    let textBbox = text.getBBox();
                    let buttonX = textBbox.x + textBbox.width + padding;
                    let buttonY = textBbox.y;
                    let asideBox = drawAsideButton({
                        graph: _this.graph,
                        startX: buttonX,
                        startY: buttonY, padding: padding, buttonSize: buttonSize, cfg: cfg, group: group
                    });
                    rect.attr({
                        x: textBbox.minX - margin,
                        y: textBbox.minY - margin,
                        width: textBbox.width + asideBox.width + padding + margin * 2,
                        height: Math.max(asideBox.height, textBbox.height) + margin * 2
                    });
                    return rect;
                }
            }, 'single-shape');

        },
        render() {
            let container = document.getElementById('graphContainer');
            this.graphOption.width = container.clientWidth;
            this.graph.changeSize(container.clientWidth, 500);
            this.graph.fitCenter();
            // this.graph.fitView();
        },
        initGraph() {
            this.graph = new G6.Graph(this.graphOption);
            this.graph.autoPaint();
            this.graph.on('auth:load', (model) => {
                this.getChildren(model);
            });
            this.graph.on('auth:permission:add', (model) => {
                this.nodeDialog.show = true;
                this.nodeDialog.theme = 'auth:permission:add';
                this.nodeDialog.level = model.level;
                this.nodeDialog.target = model;
            });
            this.graph.on('auth:resource:add', (model) => {
                this.nodeDialog.show = true;
                this.nodeDialog.theme = 'auth:resource:add';
                this.nodeDialog.level = model.level;
                this.nodeDialog.target = model;
            });
        },
        setRoot() {
            let tenant = getCurrentTenant();
            let root = {
                id: tenant.id,
                name: tenant.name,
                level: 1,
                collapsed: false,
            };
            this.graphData.nodes.push(root);
            this.set.add(root.id);
        },
        getChildren(model) {
            return getGraph(model.id, model.level).then(res => {
                let mulx = 0;
                let dis = Math.random() * 100 + 50;
                for (let obj of res.data.result.nodes) {
                    if (!this.set.has(obj.id)) {
                        obj.x = mulx + model.x;
                        obj.y = dis + model.y;
                        mulx += Math.random() * 250
                        this.graphData.nodes.push(obj);
                        this.graph.add('node', obj);
                        this.set.add(obj.id);
                    }
                    let s = model.id + "--" + obj.id;
                    if (this.set.has(s)) continue;
                    this.set.add(s);
                    let e = { source: model.id, target: obj.id };
                    this.graph.add('edge', e);
                    this.graphData.edges.push(e);
                }
                this.graph.fitCenter();
            })
        },
    },
    computed: {
        graphOption() {
            let tooltip = new Tooltip({
                itemTypes: ['node'],
                shouldBegin: (e) => {
                    let tips = document.getElementById('tooltip');
                    let flag = e?.target?.attrs?.tips ? true : false
                    tips.style.display = flag ? 'flex' : 'none';
                    return flag;
                },
                getContent: (e) => {
                    let tips = document.getElementById('tooltip');
                    tips.innerText = e?.target?.attrs?.tips;
                    return tips;
                },
            });
            let config = {
                container: 'graphContainer',
                width: 800,
                height: 500,
                autoFit: 'center',
                renderer: 'svg',
                animate: true,
                layout: {
                    type: 'force',
                    preventOverlap: true,
                    // nodeSize: 150,
                    nodeStrength: -30,
                    nodeSpacing: 80,
                    linkDistance: 100,
                },
                plugins: [tooltip],
                defaultNode: {
                    type: 'normal',
                    size: [50, 50],
                    labelCfg: {
                        position: 'bottom',
                        style: {
                            fontSize: 14
                        }
                    }
                },
                defaultEdge: {
                    style: {
                        endArrow: true,
                        lineWidth: 2,
                        stroke: '#ccc'
                    }
                },
                modes: {
                    default: ['drag-node', 'zoom-canvas', 'drag-canvas'],
                },

            }

            return config;
        }
    },

}
</script>