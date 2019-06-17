const contentTypeJSON = {"ContentType": "application/json"};

const client = (baseUrl) => {
    const ignoreBook = (bookId) => {
        return window.fetch(`${baseUrl}/books?book_id=${bookId}`,
            { method: "PUT", headers: contentTypeJSON })
    };
    const getBooks = (page) => {
        return window.fetch(`${baseUrl}/books?page=${page}`, {
            method: "GET", headers: contentTypeJSON
        })
    };
    const getMessages = (bookId, page) => {
        return window.fetch(`${baseUrl}/messages/${bookId}?page=${page}`, {
            method: "GET",
            headers: contentTypeJSON
        })
    }
};

export default client;