const NODE_COLOR = '#d81e06'
export default function registerHttp(lf) {
  lf.register('http', ({ PolygonNode, PolygonNodeModel, h }) => {
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
              d: "M192 469.333333H106.666667v-85.333333H42.666667v256h64v-106.666667h85.333333v106.666667h64V384H192v85.333333z m106.666667-21.333333h64v192h64v-192h64v-64h-192v64z m234.666666 0h64v192h64v-192h64v-64h-192v64z m384-64h-149.333333v256h64v-85.333333h85.333333c36.266667 0 64-27.733333 64-64v-42.666667c0-36.266667-27.733333-64-64-64z m0 106.666667h-85.333333v-42.666667h85.333333v42.666667z"
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
