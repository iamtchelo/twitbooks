import { MessageStore } from "./MessageStore";

const NOOP_CLIENT = {
    getMessages: () => {
        return Promise.resolve({
            json: () => {return Promise.resolve({content: [], totalPages: 2})
        }})
    }
};

test("test set page should normalize the page number for the API", () => {
    const store = MessageStore.create({
        messages: [],
        currentPage: 1,
        totalPages: 2
    }, {client: NOOP_CLIENT});
    store.setCurrentPage(3, 1);
    expect(store.currentPage).toEqual(2);
});

test("get messages should update the store", async () => {

    const messages = [{id: 1, text: "Hey"}, {id: 2, text: "Hai"}];

    const client = {
        getMessages: () => {
            return Promise.resolve({
                json: () => {return Promise.resolve({content: messages, totalPages: 2}) }})
        }
    };

    const store = MessageStore.create({
        messages: [],
        totalPages: 1,
        currentPage: 1
    }, {client: client});

    await store.getMessages(1);

    expect(store.totalPages).toEqual(2);
    expect(store.messages).toEqual(messages);

});

test("clear should return the store to the initial state", () => {
    const messages = [{id: 1, text: "Hey"}, {id: 2, text: "Hai"}];

    const store = MessageStore.create({
        messages: messages,
        totalPages: 1,
        currentPage: 1
    }, {client: NOOP_CLIENT});

    store.clear();

    expect(store.messages).toEqual([]);
    expect(store.currentPage).toEqual(0);
    expect(store.totalPages).toEqual(0);

});