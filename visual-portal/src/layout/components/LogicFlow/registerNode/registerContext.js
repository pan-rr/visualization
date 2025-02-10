const NODE_COLOR = '#9a9a9b'
export default function registerContext(lf) {
  lf.register('context', ({ PolygonNode, PolygonNodeModel, h }) => {
    class Node extends PolygonNode {
      getIconShape() {
        return h(
          'svg',
          {
            x: 14,
            y: 13,
            width: 23,
            height: 23,
            viewBox: '0 0 1024 1024'
          },
          h(
            'path',
            {
              fill: NODE_COLOR,
              d: "M960 0H64a64 64 0 0 0-64 64v896a64 64 0 0 0 64 64h896a64 64 0 0 0 64-64V64a64 64 0 0 0-64-64z m0 960H64V64h896z"
            }
          ),
          h(
            'path',
            {
              fill: NODE_COLOR,
              d: "M128 176A48 48 0 0 1 176 128h288a48 48 0 0 1 48 48v96a48 48 0 0 1-48 48h-192v167.68H320v-23.68a48 48 0 0 1 48-48h288a48 48 0 0 1 48 48v96a48 48 0 0 1-48 48h-192v168.32H512v-24.32a48 48 0 0 1 48-48h288a48 48 0 0 1 48 48v96a48 48 0 0 1-48 48h-288a48 48 0 0 1-48-48v-23.68H416V608h-48A48 48 0 0 1 320 560v-24.32H224V320h-48A48 48 0 0 1 128 272z"
            }
          )
        )
      }
      getShape() {
        const { model } = this.props
        const { width, height, x, y, points } = model
        const {
          fill,
          fillOpacity,
          strokeWidth,
          stroke,
          strokeOpacity,
        } = model.getNodeStyle()
        const transform = `matrix(1 0 0 1 ${x - width / 2} ${y - height / 2})`
        const pointsPath = points.map(point => point.join(',')).join(' ')
        return h(
          'g',
          {
            transform
          },
          [
            h(
              'polygon',
              {
                points: pointsPath,
                fill,
                stroke,
                strokeWidth,
                strokeOpacity,
                fillOpacity
              }
            ),
            this.getIconShape()
          ]
        )
      }
    }
    class Model extends PolygonNodeModel {
      constructor(data, graphModel) {
        data.text = {
          value: (data.text && data.text.value) || '',
          x: data.x,
          y: data.y + 50
        }
        super(data, graphModel)
        const lenght = 25
        this.points = [
          [lenght, 0],
          [lenght * 2, lenght],
          [lenght, lenght * 2],
          [0, lenght]
        ]
        this.taskType = 'CONTEXT_INJECT';
      }
      getNodeStyle() {
        const style = super.getNodeStyle()
        style.stroke = NODE_COLOR
        return style
      }
    }
    return {
      view: Node,
      model: Model
    }
  })
}
