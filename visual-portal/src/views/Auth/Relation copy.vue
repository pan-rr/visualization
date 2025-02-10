<!-- <template>
    <div>

        <div class="graphBox" ref="graphBox">
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

    .menu {
        position: absolute;
        justify-content: center;
        z-index: 10000;
        align-self: center;
        margin: 0%;
        padding: 0%;
        border: 1px solid #DCDFE6;
    }

    .menuItem {
        width: 100%;
        align-self: flex-end;
        justify-content: center;
        margin-bottom: 0%;
        padding: 0%;
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

                },
                graph: undefined,
                set: new Set(),
                loadSet: new Set(),
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
            registerNode() {
                const _this = this;
                G6.registerNode('tree-node', {
                    drawShape: function drawShape(cfg, group) {
                        let rect = group.addShape('rect', {
                            attrs: {
                                fill: '#fff',
                                stroke: '#666'
                            }
                        });
                        let content = cfg.name.replace(/(.{19})/g, '$1\n');
                        let text = group.addShape('text', {
                            attrs: {
                                text: content,
                                x: 0,
                                y: 0,
                                textAlign: 'left',
                                textBaseline: 'middle',
                                fill: '#666'
                            }
                        });
                        let bbox = text.getBBox();
                        let hasChildren = cfg.level < 3;
                        if (hasChildren) {
                            group.addShape('marker', {
                                attrs: {
                                    x: bbox.maxX + 8,
                                    y: bbox.minX + bbox.height / 2 - 6,
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
                // this.graph.fitView();
            },
            initGraph() {
                this.graph = new G6.TreeGraph(this.graphOption);//console.log(this.graphOption)
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
                    children: [],
                }
                this.graphData = root;
                this.set.add(root.id);
            },
            getChildren(model) {
                return getGraph(model.id, model.level).then(res => {
                    for (let obj of res.data.result.children) {
                        let s = model.id + "--" + obj.id;
                        if (this.set.has(s)) continue;
                        this.set.add(s);
                        model.children.push(obj);
                    }
                })
            },
            handleExpand(item) {
                let model = item.get('model');
                if (this.loadSet.has(model.id)) return;

                this.loadSet.add(model.id);
                let icon = item.get('group').findByClassName('collapse-icon');
                console.log(model)

                if (model.collapsed !== true) {
                    this.getChildren(model).then(() => {this.graph.paint();
                        model.collapsed = false;
                        icon.attr('symbol', this.COLLAPSE_ICON);
                    })
                } else {
                    model.collapsed = true;
                    icon.attr('symbol', this.EXPAND_ICON);
                }
                this.graph.paint();
                this.loadSet.delete(model.id)

            },
        },
        computed: {
            graphOption() {
                let _this = this;
                return {
                    container: 'graphContainer',
                    width: 800,
                    height: 500,
                    autoFit: 'center',
                    renderer: 'svg',
                    animate: true,
                    plugins: [],
                    defaultNode: {
                        type: 'tree-node',
                        anchorPoints: [[0, 0.5], [1, 0.5]],
                        labelCfg: {
                            position: 'bottom',
                            style: {
                                fontSize: 14
                            }
                        }
                    },
                    modes: {
                        default: ['drag-node', 'zoom-canvas', 'drag-canvas', {
                            type: 'collapse-expand',
                            onChange: function onChange(item, collapsed) {
                                // let data = item.get('model');
                                // let icon = item.get('group').findByClassName('collapse-icon');
                                // console.log(collapsed)
                                // if (collapsed) {
                                //     _this.getChildren(item.get('model'));
                                //     icon.attr('symbol', _this.EXPAND_ICON);
                                // } else {

                                //     icon.attr('symbol', _this.COLLAPSE_ICON);
                                // }
                                // data.collapsed = collapsed;
                                return true;
                            }
                        }],
                    },
                    layout: {
                        type: 'compactBox',
                        direction: 'LR',
                        getId: function getId(d) {
                            return d.id;
                        },
                        getHeight: function getHeight() {
                            return 16;
                        },
                        getWidth: function getWidth() {
                            return 16;
                        },
                        getVGap: function getVGap() {
                            return 20;
                        },
                        getHGap: function getHGap() {
                            return 80;
                        }
                    }
                }
            }
        }
    }
</script> -->