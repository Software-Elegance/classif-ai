
package net.softel.ai.classify.controller;


import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RestController
@RequestMapping("/session")
public class SessionController {

    @PostMapping("/prop/{key}/{value}")
	public String updateSessionAttribute(@PathVariable String key, @PathVariable String value, HttpSession session) {
		session.setAttribute(key, value);
		return "OK";
	    }

    @GetMapping("/prop/{key}")
	public String getSessionAttribute(@PathVariable String key, HttpSession session) {
		return (String)session.getAttribute(key);
	    }
}