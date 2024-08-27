

const permissionResourceSet = (auth) => {
    let list = auth.resourceList;
    let set = new Set();
    list.forEach(item => {
        let arr = item.split('::')
        arr.shift()
        let str = arr.join('::')
        set.add(str)
    })
    return set;
}

export default permissionResourceSet;