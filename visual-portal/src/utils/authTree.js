const asideButtonAttrs = [
  {
    tips: '新增资源',
    text: '新增资源',
    level: 1,
    imgPath: require('../assets/img/root.svg'),
    clickEmitEvent: 'auth:resource:add'
  },
  {
    tips: '加载子节点',
    text: '加载子节点',
    level: 1,
    imgPath: require('../assets/img/sub.svg'),
    clickEmitEvent: 'auth:load'
  },
  {
    tips: '新建权限',
    text: '新建权限',
    level: 1,
    imgPath: require('../assets/img/avatar.svg'),
    clickEmitEvent: 'auth:permission:add'
  },
  {
    tips: '加载子节点',
    text: '加载子节点',
    level: 2,
    imgPath: require('../assets/img/sub.svg'),
    clickEmitEvent: 'auth:load'
  },
];

const drawAsideButton = (wrapper) => {
  let { graph, startX, startY, padding, buttonSize, cfg, group } = { ...wrapper }
  let asideBox = {
    startX: startX,
    startY: startY,
    endX: startX,
    endY: startY + padding,
    width: 0,
    height: 0,
    computeEndX(x) {
      if (x > asideBox.endX) asideBox.endX = x;
    },
    compute() {
      asideBox.width = asideBox.endX - asideBox.startX;
      asideBox.height = asideBox.endY - asideBox.startY;
    }
  }
  let arr = [];
  for (let conf of asideButtonAttrs) {
    if (conf.level === cfg.level) {
      let button = group.addShape('image', {
        attrs: {
          type: 'image',
          width: buttonSize,
          height: buttonSize,
          tips: conf?.tips,
          img: conf.imgPath,
        },
      });
      if (conf?.text) {
        let text = group.addShape('text', {
          attrs: {
            text: conf.text,
            textAlign: 'right',
            textBaseline: 'middle',
            fill: '#666'
          },
          draggable: true
        });
        if (conf.clickEmitEvent) {
          text.on('click', () => {
            graph.emit(conf.clickEmitEvent, cfg);
          });
        }
        arr.push({ a: button, ab: button.getBBox(), b: text, bb: text.getBBox() });
      } else {
        arr.push({ a: button, ab: null, b: null, bb: null });
      }
      if (conf.clickEmitEvent) {
        button.on('click', () => {
          graph.emit(conf.clickEmitEvent, cfg);
        });
      }
    }
  }
  setPosition(asideBox, padding, arr)
  return asideBox;
}

const setPosition = (asideBox, padding, arr) => {
  let n = arr.length - 1
  arr.forEach((item, idx) => {
    item.a.attrs.x = asideBox.startX;
    item.a.attrs.y = asideBox.endY;
    if (item.b) {
      item.b.attrs.x = asideBox.startX + item.ab.width + padding + item.bb.width;
      item.b.attrs.y = asideBox.endY + item.ab.height / 2;
      asideBox.computeEndX(item.b.attrs.x);
    } else {
      asideBox.computeEndX(item.a.attrs.x);
    }
    asideBox.endY += item.ab.height;
    asideBox.endY += ((idx < n) ? padding : 0);
  });
  asideBox.compute();
}

export {
  drawAsideButton
}