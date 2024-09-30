import { resetSpaceOption } from "../../utils/spaceUtil";


export default {
  namespaced: true,
  state: {
    spaceHolder: {
      spaceOptions: [],
      primitiveOptions: [],
      chosenSpaceLabel: '',
      chosenSpaceValue: ''
    },
  },
  mutations: {
    INIT_HOLDER(state, spaceOptions) {
      state.spaceHolder.primitiveOptions = spaceOptions;
      resetSpaceOption();
    },
  },
  actions: {
    initHolder({ commit }, spaceOptions) {
      return new Promise((resolve) => {
        commit('INIT_HOLDER', spaceOptions)
        resolve('success')
      })
    },
  },
  modules: {

  },
}
