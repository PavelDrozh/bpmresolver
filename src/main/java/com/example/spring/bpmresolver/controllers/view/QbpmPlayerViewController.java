package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.services.QbpmPlayerBaseUrlService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/qbpmplayer")
@AllArgsConstructor
public class QbpmPlayerViewController {

    private final QbpmPlayerBaseUrlService qbpmPlayerBaseUrlService;

    @PostMapping("/baseUrl")
    public String setBaseUrl(
            @RequestParam("baseUrl") String baseUrl,
            @RequestParam("context") String context
    ) {
        qbpmPlayerBaseUrlService.setBaseUrl(baseUrl);
        qbpmPlayerBaseUrlService.setContext(context);
        return "redirect:/qbpmcockpit/baseUrl";
    }
}
