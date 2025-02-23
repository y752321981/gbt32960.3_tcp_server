function getToken() {
    return Math.random().toString(36).substring(3, 8)
}

export {
    getToken
}