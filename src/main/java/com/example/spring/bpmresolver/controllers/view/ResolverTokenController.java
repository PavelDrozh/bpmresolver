package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.dto.AccessTokenResponseDto;
import com.example.spring.bpmresolver.dto.TokenRequestDto;
import com.example.spring.bpmresolver.services.AccessTokenService;
import com.example.spring.bpmresolver.services.ResolverAccessTokenService;
import com.example.spring.bpmresolver.services.ResolverTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/token")
@AllArgsConstructor
public class ResolverTokenController {

    private final ResolverTokenService resolverTokenService;
    private final AccessTokenService accessTokenService;
    private final ResolverAccessTokenService resolverAccessTokenService;

    @GetMapping
    public String tokenPage(@RequestParam(value = "updated", required = false) String updated,
                            @RequestParam(value = "passwordRequired", required = false) String passwordRequired,
                            Model model) {
        model.addAttribute("token", resolverTokenService.getToken());
        model.addAttribute("tokenRequest", TokenRequestDto.builder()
                .realm(resolverAccessTokenService.getRealm())
                .clientId(resolverAccessTokenService.getClientId())
                .userName(resolverAccessTokenService.getUserName())
                .url(resolverAccessTokenService.getUrl())
                .build());
        model.addAttribute("updated", updated != null);
        model.addAttribute("passwordRequired", passwordRequired != null);
        return "token";
    }

    @PostMapping
    public String updateToken(@RequestParam("token") String token) {
        resolverTokenService.setToken(token);
        return "redirect:/token?updated";
    }

    @PostMapping("/auto")
    public String autoUpdateToken(@ModelAttribute("tokenRequest") TokenRequestDto token) {
        if (token.getPassword() == null || token.getPassword().isBlank()) {
            return "redirect:/token?passwordRequired";
        }
        resolverAccessTokenService.updateAccessSettings(token);
        AccessTokenResponseDto accessTokenResponse = accessTokenService.getAccessTokenForPasswordGrant(
                resolverAccessTokenService.getUserName(),
                token.getPassword(),
                resolverAccessTokenService.getClientId()
        );
        resolverTokenService.setToken(accessTokenResponse.getAccessToken());
        return "redirect:/token?updated";
    }
}
