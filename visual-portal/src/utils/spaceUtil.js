import store from '@/store'

const resetSpaceOption = () => {
    
    let currentTenant = store.getters.userInfo.chosenTenant;
    let userId = store.getters.userInfo.userId;
    let spaceHolder = store.getters.spaceHolder;
    let primitiveOptions = spaceHolder.primitiveOptions;
    let res = primitiveOptions.filter(i => {
        return i.value === currentTenant || i.value === userId;
    })
    spaceHolder.spaceOptions = res;
    if (res?.length > 0) {
        spaceHolder.chosenSpaceLabel = res[0].label
        spaceHolder.chosenSpaceValue = res[0].value
    }
    return res;
}

const changeChoosenSpace = (value)=>{
    let spaceHolder = store.getters.spaceHolder;
    for(let option of spaceHolder.spaceOptions){
        if(option.value === value){
            spaceHolder.chosenSpaceLabel = option.label;
            spaceHolder.chosenSpaceValue = value;
            return option.label;
        }
    }
}

export {
    resetSpaceOption,
    changeChoosenSpace
}