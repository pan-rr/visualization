const NODE_COLOR = '#409EFF'
export default function registerVisual(lf) {
  lf.register('visual', ({ PolygonNode, PolygonNodeModel, h }) => {
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
              d: "M497.229243 1023.969281c0-14.622281 6.855474-32.439347 20.561303-53.461436l6.855475 27.416777c-1.827785-26.500325-1.827785-42.033939 0-46.605962 0 0.911333 2.283451 10.511045 6.855474 28.783777v-15.077948c0-14.622281-1.827785-23.756087-5.483356-27.411657l1.366999-1.372119c9.138926 17.361399 16.450066 30.155895 21.933422 38.378368l1.259483-5.017449-0.957412-2.442167c-9.574113-24.309031-14.67348-41.67555-15.30834-52.094437l-0.071678-2.129856 5.483355-9.594592a40.462146 40.462146 0 0 0 13.705829-10.966711l-4.111237-4.111237a481.557393 481.557393 0 0 0 8.227594-23.98648c2.739118-8.68326 7.766807-13.936222 15.077947-15.769127-1.827785-2.739118-2.744238-5.939022-2.744237-9.594592 1.827785-3.65557 5.027689-7.766807 9.594592-12.33883 9.138926-42.945272 36.094917-115.595892 80.878213-217.951862 10.966711-28.32811 18.733518-49.800746 23.300421-64.423027 22.849875-53.917102 40.211274-94.584042 52.089318-122.00082 19.194304-50.261532 29.244563-76.306191 30.155895-78.128856 13.710949-31.077468 25.138446-51.638771 34.272252-61.694149 0-7.306021 6.625081-27.867324 19.875244-61.67879 13.250162-33.811466 19.880364-63.972481 19.880363-90.467686 0-3.66069-1.602512-8.913653-4.802416-15.769127-3.194784-6.855474-5.252962-12.108437-6.164295-15.764007l-13.710948-1.372119c-3.65557-5.478236-6.399808-10.505925-8.222474-15.077947v-4.111237l-2.744237-1.372119-1.366999-1.366999-2.744238 2.739118-9.594592-12.33883h-1.372119l-1.366999-2.739118-1.372119-2.744237h4.111237l-5.483356-5.478236-8.222473-2.744238-10.966711-8.222473h-4.116357v-6.860594l-6.850354 4.116356-16.450067-5.478235c-10.050258 7.306021-17.817065 12.33371-23.30554 15.072827-33.811466 50.261532-76.306191 148.045479-127.484176 293.34672-37.461916 107.834205-69.906383 212.01284-97.32316 312.535904a5217.051808 5217.051808 0 0 0-24.67254-132.962411 47971.112547 47971.112547 0 0 1-28.783777-134.33965c-5.483355-42.033939-17.366519-101.434397-35.64437-178.196254-7.306021-27.416777-21.928302-65.800266-43.861724-115.145346C331.822365 38.383488 312.172395 6.394688 298.466566 0c-4.566903 0-19.644851 7.766807-45.233843 23.305541-7.311141-2.744238-12.799616-5.027689-16.450066-6.860594 0-3.65045-2.283451 0-6.855475 10.97183l-1.366999-1.372118-1.372119 4.111236L221.704709 21.933422h-4.111237V30.155895L207.99376 37.01649c-1.822665 0-2.739118-0.916453-2.739118-2.744238l-4.111236 12.33883-5.483356-1.372119-2.744237-2.739118c0 4.566903-2.283451 11.65277-6.850355 21.247363C181.493435 73.33668 179.209984 82.245213 179.209984 90.467686c0 42.039059 25.588992 154.445287 76.761857 337.213564 42.033939 147.129026 75.850524 257.707469 101.439517 331.730208a10293.845425 10293.845425 0 0 0 32.900133 91.839804c18.277852 48.433747 27.411658 76.761857 27.411657 84.989451 0 5.478236-1.141726 13.250162-3.425177 23.300421-2.283451 10.055378-3.430297 17.817065-3.430297 23.305541 8.227593-23.756087 12.33883-37.01137 12.33883-39.755608 5.483355 9.138926 8.227593 17.822185 8.227593 26.044659 0 5.483355-1.372119 13.710949-4.111237 24.67766-2.744238 10.966711-4.116357 19.189184-4.116356 24.672539l12.33883-47.97296 4.111236 6.850354c-0.911333-1.827785 0.460786-3.65557 4.111237-5.483355l4.116356 4.111236-5.483355 6.855475 6.850354-2.744238 4.116357-21.928302c0.727018 0.363509 1.571793 1.607632 2.518964 3.727248l0.706539 1.720268 0.33791 1.402838a64.049279 64.049279 0 0 0 2.600882 8.099597l0.511985 1.126366v0.01536l0.860134 3.097507c2.841515 10.557123 4.423547 17.868264 4.740978 21.933422l0.056318 1.366999-1.372119 10.966711 2.744238-6.850354 1.372119 2.739117-1.372119 2.744238 1.372119 2.739118h4.111236l6.855475-21.933422-6.860595 13.710949a30.463086 30.463086 0 0 1-4.106116-9.594593l1.459156-4.428667 5.032809-7.818005 0.855014-0.803816c1.607632-1.392598 3.286941-2.559923 5.043049-3.512215l2.687919-1.259482 2.739118 15.077948c0-14.622281 0.460786-24.67766 1.372119-30.155896l4.111237-2.744237 4.111236 10.966711-4.111236 19.194304h-2.739118l-1.372119 2.739118c0.911333 2.744238 2.283451 5.027689 4.111237 6.855474l-6.855475 20.561303 6.860594-13.710948 1.366999 6.860594-1.372118 6.850354 4.111236-12.33883 1.372119 16.450067z m26.034419-219.472456l0.01024-3.962761c0.916453-0.916453 1.372119-1.827785 1.372119-2.744238v-8.222473l2.739117-1.372119c0 5.483355 0.460786 9.138926 1.372119 10.966711l-6.855474 31.528014v-15.077947l-1.172445-2.319291 0.491505-0.593902 0.68094-1.198044 0.261112-2.047939c0.742378-5.67279 1.111007-7.270182 1.111007-4.807535l-0.01024-0.148476z m1.382359 63.204504v-12.33883c13.705829-29.239443 23.300421-51.633651 28.783776-67.167265l1.372119 4.111237c-9.138926 33.816586-19.189184 58.944792-30.155895 75.394858z m-82.245213 39.750487c-2.744238-2.739118-4.572023-5.022569-5.483356-6.850354l6.850355-10.966711 4.116356 6.850354-5.483355 10.966711z m21.928302 0l-4.111237-8.222473 4.111237-6.855474 1.372119 6.855474-1.372119 8.222473z m20.561303 0l-2.739118-2.739117-2.739117-4.111237c0.911333-2.744238 1.827785-4.572023 2.739117-5.483356l2.739118 2.739118v9.594592z m26.049779 24.67766c-8.227593-12.799616-12.33883-21.933422-12.33883-27.416777l1.372119-4.111237 2.739118-1.372119h5.483355l2.744238 2.739118v1.372119l5.478235 12.33883-5.478235 16.450066z m-17.822186-12.33883c-1.827785-5.483355-2.283451-9.594592-1.372118-12.33883h-1.372119v-4.111236l1.372119-4.111237 1.372118 1.372119 2.739118 12.33883-2.739118 6.850354z m-8.227593 39.750488l-4.111236-2.739118-1.366999-8.227593c-2.744238-3.65557-5.027689-6.394688-6.860595-8.222474l5.488476-13.705828-6.855475 6.850354-6.855474-15.077948 6.860594-12.338829c6.394688 17.366519 11.417257 30.155895 15.072828 38.383488-0.911333 3.65557-1.372119 8.68326-1.372119 15.077948z m2.744238-21.928302c-0.916453-3.65557-1.827785-6.399808-2.744238-8.227594v-8.222473c0-0.916453 0.460786-2.744238 1.372119-5.483355v-5.483356l1.372119 4.111237 2.739118 4.111236v5.483356l-2.739118 13.710949z m-32.900133 1.366999h-2.739118v-6.850355l1.372119-6.860594 1.366999-1.366999 3.824525 7.690009-1.080287 0.537584a15.810086 15.810086 0 0 0-2.058179 4.454266l-0.409587 1.925063-0.276472 0.471026z m12.33883 56.205673l-2.744238-5.483355v-4.116357c-1.827785-5.478236-3.194784-9.138926-4.111237-10.966711l-0.174074-0.353269v-0.020479l-0.076798-0.281592a115.964521 115.964521 0 0 0-3.34838-10.306251l-0.025599-0.035839-0.209913-0.921572a145.060608 145.060608 0 0 1-1.100767-5.488475l-0.547824-3.153826v-12.33883c0-0.911333 0.056318-1.791946 0.174075-2.65208l0.102397-0.558063 3.839884-6.379329-0.291831-0.537584 1.65883-0.834535h4.111237c0 5.478236 2.513845 13.019769 7.541534 22.619482 5.027689 9.594592 7.541534 17.131006 7.541534 22.614361l-4.474746 6.947632-1.505235 1.443797c-0.767977 0.803816-1.515475 1.66395-2.247613 2.575282l-1.279961 3.798926-2.831275 4.428667z m28.783776-27.416777l-4.111236-20.561303v-6.855475l1.372118-1.372118h2.739118c-0.911333 5.483355 0 11.878044 2.744238 19.194304l-2.744238 9.594592z m-42.494725-9.594592l-2.739118-1.372119-2.739118-1.372119v-12.33883h1.366999c1.827785 6.399808 3.199904 11.427497 4.116357 15.083068z m-4.111237 10.961591c-6.399808-5.478236-9.594592-10.505925-9.594592-15.077948l1.372119-2.739118h2.739118l8.227593 12.33883-2.744238 5.478236z m109.66711 20.566423l-6.860594-26.044659-1.479635 5.949262 1.162205 2.867114c2.186174 5.427037 4.577143 11.161265 7.172905 17.228283z m-111.039228-82.245213l1.372118 1.366999-1.366999 10.966711h-2.744237l-1.372119-2.744237-1.366999-2.739118 5.478236-5.483356v-1.366999z m50.706958 16.470546l4.126597 19.163585-4.111237 8.227593-1.372119-20.561303 1.372119-6.855474-0.01536 0.025599z"
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
        this.taskType = 'VISUAL';
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