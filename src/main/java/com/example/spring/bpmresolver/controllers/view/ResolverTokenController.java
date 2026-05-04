package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.dto.TokenRequestDto;
import com.example.spring.bpmresolver.services.ResolverTokenUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/token")
@AllArgsConstructor
public class ResolverTokenController {

    private final ResolverTokenUseCase resolverTokenUseCase;

    @GetMapping
    public String tokenPage(@RequestParam(value = "updated", required = false) String updated,
                            @RequestParam(value = "passwordRequired", required = false) String passwordRequired,
                            Model model) {
        ResolverTokenUseCase.TokenPageData data = resolverTokenUseCase.getTokenPageData(updated != null, passwordRequired != null);
        model.addAttribute("token", data.token());
        model.addAttribute("tokenRequest", data.tokenRequest());
        model.addAttribute("updated", data.updated());
        model.addAttribute("passwordRequired", data.passwordRequired());
        return "token";
    }

    @PostMapping
    public String updateToken(@RequestParam("token") String token) {
        resolverTokenUseCase.updateToken(token);
        return "redirect:/token?updated";
    }

    @PostMapping("/auto")
    public String autoUpdateToken(@ModelAttribute("tokenRequest") TokenRequestDto token) {
        ResolverTokenUseCase.AutoUpdateResult result = resolverTokenUseCase.autoUpdateToken(token);
        return (result == ResolverTokenUseCase.AutoUpdateResult.PASSWORD_REQUIRED)
                ? "redirect:/token?passwordRequired"
                : "redirect:/token?updated";
    }
}
