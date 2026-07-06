import {
    defineStore
} from 'pinia'

const useCategoryStore = defineStore('categoryStore', {
    state: () => {
        return {
            categorieMap: {},
            categoriesList: [],
            currentPCategory: {},
        }
    },
    actions: {
        saveCategoryMap(data) {
            this.categoriesList = data
        },
        saveCategoryList(data) {
            this.currentPCategory = data
        },
        setCurrentPCategory(data) {
            if(data){
                this.currentPCategory = this.categorieMap[data] || {};
            }else{
                this.currentPCategory = {};
            }
        }
    }
})
export {
    useCategoryStore
};