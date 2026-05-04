package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.services.QbpmPlayerServiceNameService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/qbpmplayer")
@AllArgsConstructor
public class QbpmPlayerViewController {

    private final QbpmPlayerServiceNameService qbpmPlayerServiceNameService;


    @GetMapping("/basePlayer")
    public String getBaseUrl(Model model) {
        model.addAttribute("qbpmPlayerServiceName", qbpmPlayerServiceNameService.getServiceNameOrNull());
        model.addAttribute("qbpmPlayerContext", qbpmPlayerServiceNameService.getContextOrNull());
        return "bpmPlayer";
    }

    @PostMapping("/baseUrl")
    public String setBaseUrl(
            @RequestParam("serviceName") String serviceName,
            @RequestParam("context") String context,
            RedirectAttributes redirectAttributes
    ) {
        try {
            qbpmPlayerServiceNameService.setServiceName(serviceName);
            qbpmPlayerServiceNameService.setContext(context);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/qbpmcockpit/baseUrl";
    }
}
