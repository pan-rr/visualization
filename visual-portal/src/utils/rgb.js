const randomRGB = () => {
    let r = parseInt(Math.random() * 255);
    let g = parseInt(Math.random() * 255);
    let b = parseInt(Math.random() * 255);
    return `rgb(${r},${g},${b})`;
}

const randomRGBArr = (len) => {
    let arr = [];
    while (len-- > 0) {
        arr.push(randomRGB());
    }
    return arr;
}

export { randomRGB ,randomRGBArr}