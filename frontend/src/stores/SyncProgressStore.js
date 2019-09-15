import { types, flow, getEnv } from 'mobx-state-tree';

export const SyncProgress = types.model({
    totalMessages: types.number,
    syncedMessages: types.number,
    bookCount: types.number
});

export const SyncProgressStore = types.model({
    progress: SyncProgress
}).actions(self => ({
    getProgress: flow(function*() {
        const response = yield getEnv(self).client.getSyncProgress();
        const responseData = yield response.json();
        self.progress = responseData;
    })
}));