<template>
    <div>
        <div class="graphBox" ref="graphBox">
            <el-button type="info" icon='el-icon-refresh' @click="refresh" size="mini">刷新</el-button>
            <div id="graphContainer">
            </div>
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

    import G6 from '@antv/g6';
    import { getCurrentTenant } from '../../utils/tenantUtil';
    import { getGraph } from '../../api/authGraph';



    export default {

        data() {
            return {
                observer: {},
                graphData: {
                    nodes: [],
                    edges: []
                },
                graph: undefined,
                set: new Set(),
                root: {},

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
            COLLAPSE_ICON(x, y, r) {
                return [['M', x, y], ['a', r, r, 0, 1, 0, r * 2, 0], ['a', r, r, 0, 1, 0, -r * 2, 0], ['M', x + 2, y], ['L', x + 2 * r - 2, y]];
            },
            EXPAND_ICON(x, y, r) {
                return [['M', x, y], ['a', r, r, 0, 1, 0, r * 2, 0], ['a', r, r, 0, 1, 0, -r * 2, 0], ['M', x + 2, y], ['L', x + 2 * r - 2, y], ['M', x + r, y - r + 2], ['L', x + r, y + r - 2]];
            },
            refresh() {
                this.graph.fitCenter();
            },
            registerNode() {
                const _this = this;
                G6.registerNode('expandable-node', {
                    drawShape: function drawShape(cfg, group) {

                        let xx = _this.graph.getWidth(), yy = _this.graph.getHeight(), qx = xx / 4, qy = xx / 4;
                        xx = parseInt(Math.random() * xx / 4) - qx + xx / 2;
                        yy = parseInt(Math.random() * yy / 4) - qy + yy / 2;

                        let rect = group.addShape('rect', {
                            attrs: {
                                x: xx,
                                y: yy,
                                fill: '#fff',
                                stroke: '#666',
                                width: 50,
                                height: 50
                            },
                            draggable: true
                        });
                        let content;
                        switch (cfg.level) {
                            case 1: content = `组织名称:${cfg.name}\n层级:${cfg.level}`; break;
                            case 2: content = `权限名称:${cfg.name}\n层级:${cfg.level}`; break;
                            case 3: content = `用户名称:${cfg.name}\n层级:${cfg.level}`; break;
                            default: content = cfg.name;
                        }
                        content = content.replace(/(.{19})/g, '$1\n');
                        let text = group.addShape('text', {
                            attrs: {
                                text: content,
                                x: xx,
                                y: yy,
                                textAlign: 'left',
                                textBaseline: 'middle',
                                fill: '#666'
                            },
                            draggable: true
                        });
                        let bbox = text.getBBox();
                        let hasChildren = cfg.level < 3;
                        if (hasChildren) {
                            group.addShape('marker', {
                                attrs: {
                                    x: xx + bbox.width + 8,
                                    y: yy,
                                    r: 6,
                                    symbol: _this.EXPAND_ICON,
                                    stroke: '#966',
                                    lineWidth: 2
                                },
                                className: 'collapse-icon'
                            });
                        }
                        rect.attr({
                            x: bbox.minX - 4,
                            y: bbox.minY - 6,
                            width: bbox.width + (hasChildren ? 26 : 8),
                            height: bbox.height + 12
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
                this.graph.fitView();
            },
            initGraph() {
                this.graph = new G6.Graph(this.graphOption);
                this.graph.autoPaint();
                this.graph.on('node:click', e => {
                    this.handleExpand(e.item);
                })
            },
            setRoot() {
                let tenant = getCurrentTenant();
                let root = {
                    id: tenant.id,
                    name: tenant.name,
                    level: 1,
                    collapsed: false,
                }
                this.graphData.nodes.push(root);
                this.set.add(root.id);
            },
            getChildren(model) {
                return getGraph(model.id, model.level).then(res => {
                    let mulx = Math.random() > 0.5 ? 1 : -1;
                    let dis = Math.random() * 100 + 50;
                    for (let obj of res.data.result.nodes) {
                        if (!this.set.has(obj.id)) {
                            obj.x = mulx * dis + model.x;
                            obj.y = dis + model.y;
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
            handleExpand(item) {
                let model = item.get('model');
                if (model.collapsed === true) return;
                let icon = item.get('group').findByClassName('collapse-icon');
                if (model.collapsed !== true) {
                    this.getChildren(model).then(() => {
                        model.collapsed = true;
                        icon.attr('symbol', this.COLLAPSE_ICON);
                    })
                }
            },
        },
        computed: {
            graphOption() {
                return {
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
                    plugins: [],
                    defaultNode: {
                        type: 'expandable-node',
                        size: [50, 50],
                        labelCfg: {
                            position: 'bottom',
                            style: {
                                fontSize: 14
                            }
                        }
                    },
                    modes: {
                        default: ['drag-node', 'zoom-canvas', 'drag-canvas'],
                    },

                }
            }
        }
    }
</script>