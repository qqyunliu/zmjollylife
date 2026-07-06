import {
  ref,
  computed
} from 'vue'
import {
  defineStore
} from 'pinia'

export const useLoginStore = defineStore('loginStore', {
  state: () => {
    return {
      showLogin: false,
      userInfo: {},
    }
  },
  actions: {
    setLogin(show) {
      this.showLogin = show
    },
    saveUserInfo(Info) {
      this.userInfo = Info
    }
  }
})

