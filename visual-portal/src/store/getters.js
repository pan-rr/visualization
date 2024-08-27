export default {
  userInfo: (state) => state.user.userInfo,
  auth: (state) => state.user.auth,
  routes: (state) => state.permission.routes,
  isCollapse: (state) => state.setting.isCollapse,
  cachedViews: (state) => state.tagsView.cachedViews,
  visitedViews: (state) => state.tagsView.visitedViews
}
