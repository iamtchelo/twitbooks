import auth0Client from '../auth/Auth';

const baseUrl = process.env.REACT_APP_ENDPOINT || "http://localhost:8080/api/v1";

const baseHeaders = () => {
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${auth0Client.getIdToken()}`
    }
};

const client = (baseUrl) => ({
    ignoreBook(bookId) {
        return window.fetch(`${baseUrl}/books?book_id=${bookId}`,
            { method: "PUT", headers: baseHeaders() })
    },
    getBooks(page) {
        return window.fetch(`${baseUrl}/books?page=${page}`, {
            method: "GET", headers: baseHeaders()
        })
    },
    getMessages(bookId, page) {
        return window.fetch(`${baseUrl}/messages/${bookId}?page=${page}`, {
            method: "GET",
            headers: baseHeaders()
        })
    },
    login() {
        return window.fetch(`${baseUrl}/login`, {
            method: "GET",
            headers: baseHeaders()
        })
    }
});

export default client(baseUrl);