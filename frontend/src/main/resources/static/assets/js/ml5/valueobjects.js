class Message {

    constructor(id, title, message, payload, settings) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US');
        this.settings = settings;
        }
    
    }

