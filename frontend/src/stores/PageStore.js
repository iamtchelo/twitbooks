import { types } from 'mobx-state-tree';

export const PageStore = types.model({
    progress: types.boolean
}).actions(self => ({
    showProgress() {
        self.progress = true
    },
    hideProgress() {
        self.progress =false
    }
}));