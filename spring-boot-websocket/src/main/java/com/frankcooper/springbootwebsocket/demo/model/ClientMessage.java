package com.frankcooper.springbootwebsocket.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ClientMessage {

    private String name;

    public ClientMessage() {
    }

}
