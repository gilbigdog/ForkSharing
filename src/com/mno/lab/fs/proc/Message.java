package com.mno.lab.fs.proc;

import java.util.UUID;

import com.samsung.ssl.smeshnet.data.Data;


public class Message extends Data{
    private static final long serialVersionUID = UUID.randomUUID().hashCode();
    
    public final String message;

    public Message(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
