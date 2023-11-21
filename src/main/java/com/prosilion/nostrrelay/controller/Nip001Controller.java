package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.event.Nip001Message;
import com.prosilion.nostrrelay.Nip001Response;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class Nip001Controller {


	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Nip001Response greeting(Nip001Message message) throws Exception {
		Thread.sleep(1000); // simulated delay
		return new Nip001Response("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

}
