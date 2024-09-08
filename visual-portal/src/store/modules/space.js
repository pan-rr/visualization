

export default {
  namespaced: true,
  state: {
    spaceHolder: {
      spaceOptions: [],
      chosenSpaceLabel: '',
      chosenSpaceValue: ''
    },
  },
  mutations: {
    INIT_HOLDER(state, spaceOptions) {
      state.spaceHolder.spaceOptions = spaceOptions;
      if (spaceOptions?.length > 0) {
        state.spaceHolder.chosenSpaceLabel = spaceOptions[0].label
        state.spaceHolder.chosenSpaceValue = spaceOptions[0].value
      }
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
