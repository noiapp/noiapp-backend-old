package org.dpppt.backend.sdk.ws.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/discovery")
public class DiscoveryController {

    @GetMapping("/discovery.json")
    public ResponseEntity<byte[]> getDiscovery() throws IOException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        byte[] discovery = IOUtils.toByteArray(new ClassPathResource("discovery.json").getInputStream());
        return ResponseEntity.ok().headers(httpHeaders).body(discovery);
    }
}
