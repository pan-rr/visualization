<template>
  <el-aside :class="{ collapse: isCollapse }">
    <Logo />
    <el-menu :default-active="$route.name" :collapse="isCollapse">
      <SlideBarItem v-for="item in routes" :key="item.path" :item="item" />
    </el-menu>
  </el-aside>
</template>

<script>
import Logo from "./Logo";
import SlideBarItem from "./Item.vue";
import routeItemPermit from "../../../utils/routePermit";
import permissionResourceSet from "../../../utils/pemissionResource";


export default {
  name: "",
  components: { Logo, SlideBarItem },
  data() {
    return {};
  },
  computed: {
    isCollapse() {
      return this.$store.getters.isCollapse;
    },
    routes() {

      // let auth = this.$store.getters.auth;
      // if (auth.permitAll) {
      //   return this.$store.getters.routes;
      // }
      // let set = permissionResourceSet(auth);
      // let arr = this.$store.getters.routes;
      // arr = arr.filter(item => {
      //   return routeItemPermit(item, set)
      // })
      // return arr;
      return this.$store.getters.routes;
    }
  }
};
</script>

<style lang="scss" scoped>
.el-aside {
  width: 230px !important;
  background-color: #3477f2;
  overflow-x: hidden;
  transition: 0.4s;

  ::v-deep .el-menu-item>a>span,
  ::v-deep .el-submenu__title>span {
    position: relative;
    right: 0;
    transition: right 1s;
  }

  &.collapse {
    width: 64px !important;

    ::v-deep .el-menu-item>a>span,
    ::v-deep .el-submenu__title>span {
      right: -100px;
    }
  }

  ::v-deep .el-menu {
    max-width: 230px;
    background-color: transparent;
    border: none;

    &:hover {
      i {
        color: #3477f2;
      }
    }

    .el-submenu {
      &.is-active {
        .el-submenu__title {
          background-color: #1564f7;

          &:hover {
            background-color: #fff;
          }
        }
      }

      .el-submenu__title {
        color: #fff;

        &:hover {
          color: #3477f2;

          i {
            color: #3477f2;
          }
        }

        >i {
          color: #fff;
        }
      }
    }

    .el-menu-item {

      &.is-active,
      &:hover {
        background-color: #fff;

        .el-submenu__title,
        a,
        i {
          color: #3477f2;
        }
      }

      .el-submenu__title,
      a,
      i {
        color: #fff;

        &:hover {

          &,
          i {
            color: #3477f2;
          }
        }
      }
    }
  }
}
</style>
