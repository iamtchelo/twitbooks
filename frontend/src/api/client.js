import auth0Client from '../auth/Auth';
const contentTypeJSON = {"ContentType": "application/json"};

const client = (baseUrl) => ({
    ignoreBook(bookId) {
        return window.fetch(`${baseUrl}/books?book_id=${bookId}`,
            { method: "PUT", headers: contentTypeJSON })
    },
    getBooks(page) {
        return window.fetch(`${baseUrl}/books?page=${page}`, {
            method: "GET", headers: contentTypeJSON
        })
    },
    getMessages(bookId, page) {
        return window.fetch(`${baseUrl}/messages/${bookId}?page=${page}`, {
            method: "GET",
            headers: contentTypeJSON
        })
    }
});

// client.interceptors.request.use(
//     config => {
//         config.headers.Authorization = `Bearer ${auth0Client.getIdToken()}`;
//         return config;
//     },
//     error => Promise.reject(error)
// );

export default client;