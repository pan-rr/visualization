const NODE_COLOR = '#9932CC'
export default function registerSQL(lf) {
  lf.register('sql', ({ PolygonNode, PolygonNodeModel, h }) => {
    class Node extends PolygonNode {
      getIconShape () {
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
              d:"M271.1 327.5 208.6 382.7c-22-30.6-44.3-45.8-67.1-45.8-11.1 0-20.1 3-27.2 8.9-7 5.9-10.6 12.6-10.6 20.1 0 7.4 2.5 14.5 7.6 21.1 6.8 8.9 27.5 27.9 61.9 57 32.2 26.9 51.8 43.9 58.6 51 17.1 17.3 29.3 33.8 36.4 49.6 7.1 15.8 10.7 33 10.7 51.7 0 36.4-12.6 66.5-37.7 90.2-25.2 23.7-58 35.6-98.4 35.6-31.6 0-59.1-7.7-82.6-23.2-23.4-15.5-43.5-39.8-60.2-73l71-42.8c21.3 39.2 45.9 58.8 73.7 58.8 14.5 0 26.7-4.2 36.6-12.7 9.9-8.4 14.8-18.2 14.8-29.3 0-10.1-3.7-20.1-11.2-30.2-7.5-10.1-23.9-25.4-49.2-46.1-48.3-39.4-79.6-69.8-93.7-91.2-14.1-21.4-21.1-42.8-21.1-64.1 0-30.8 11.7-57.2 35.2-79.2 23.4-22 52.4-33 86.8-33 22.1 0 43.2 5.1 63.3 15.4C226.1 281.6 247.8 300.3 271.1 327.5zM709.2 646l77.2 99.8-100 0-39.2-50.5c-32.4 17.8-68.6 26.6-108.4 26.6-66.6 0-122-23-166.1-68.9-44.1-45.9-66.1-100.7-66.1-164.2 0-42.4 10.3-81.4 30.8-116.9 20.5-35.5 48.7-63.7 84.7-84.6 35.9-20.9 74.5-31.4 115.7-31.4 63 0 117 22.7 162.2 68.2 45.2 45.4 67.8 100.8 67.8 166.2C767.8 550.5 748.2 602.3 709.2 646zM656.5 577.8c17.9-26.5 26.8-55.9 26.8-88.1 0-42-14.2-77.7-42.6-107.1-28.4-29.3-62.7-44-103-44-41.5 0-76.2 14.3-104.2 42.8-28 28.6-42 64.8-42 108.9 0 49.1 17.6 87.9 52.9 116.4 27.6 22.3 58.9 33.5 94 33.5 20.1 0 39.1-3.9 56.8-11.8l-79.4-102.2 100.7 0L656.5 577.8zM816.5 267.1l84.4 0 0 363.1L1024 630.2l0 80.5L816.5 710.7 816.5 267.1z"
            }
          )
        )
      }
      getShape () {
        const {model} = this.props
        const {width, height, x, y, points} = model
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
      constructor (data, graphModel) {
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
        this.taskType= 'SQL';
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