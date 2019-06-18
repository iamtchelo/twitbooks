const NOOP_CLIENT = {
    getMessages: () => {
        return Promise.resolve({
            json: Promise.resolve([])
        })
    }
};

test("test set page should normalize the page number for the API", () => {
    const store = MessageStore.create({

    })
});

test("get messages should update the store", async () => {
});