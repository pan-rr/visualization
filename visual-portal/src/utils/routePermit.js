const getPermitRoutes = (routes, permissionSet) => {
    let arr = routes.map(item => {
        let obj = Object.assign({}, item);
        if (item?.meta?.needAuthFilter === true) {
            if (permissionSet.has(item.meta.title)) {
                if (obj.children) {
                    obj.children = getPermitRoutes(item.children, permissionSet);
                }
                return obj;
            }
            return null;
        } else {
            return obj;
        }
    });
    return arr.filter(i => i);
}



export default getPermitRoutes