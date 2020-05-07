package com.frankcooper.springbootwebsocket.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerMessage {

    private String content;

    public ServerMessage() {
    }

    @Override
    public String toString() {
        return content;
    }

}
