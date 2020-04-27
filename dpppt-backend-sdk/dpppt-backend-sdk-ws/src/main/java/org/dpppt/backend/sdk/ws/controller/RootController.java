package org.dpppt.backend.sdk.ws.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class RootController {

    private final String websiteUrl;

    public RootController(@Value("${website.url}") String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @GetMapping(value = "/")
    public void method(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", websiteUrl);
        httpServletResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    }
}
