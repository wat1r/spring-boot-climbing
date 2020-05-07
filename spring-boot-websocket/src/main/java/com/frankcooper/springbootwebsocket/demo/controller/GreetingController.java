package com.frankcooper.springbootwebsocket.demo.controller;

import com.frankcooper.springbootwebsocket.demo.consts.GlobalConsts;
import com.frankcooper.springbootwebsocket.demo.model.ClientMessage;
import com.frankcooper.springbootwebsocket.demo.model.ServerMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping(GlobalConsts.HELLO_MAPPING)
    @SendTo(GlobalConsts.TOPIC)
    public ServerMessage greeting(ClientMessage message) throws Exception {
        // 模拟延时，以便测试客户端是否在异步工作
        Thread.sleep(1000);
        return new ServerMessage("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
