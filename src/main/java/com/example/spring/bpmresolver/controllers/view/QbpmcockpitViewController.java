package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.dto.BpmFinishedProcessResponseDto;
import com.example.spring.bpmresolver.dto.BpmInstanceDto;
import com.example.spring.bpmresolver.dto.QbpmcockpitInstancesRequest;
import com.example.spring.bpmresolver.dto.RestResponsePage;
import com.example.spring.bpmresolver.localization.LocalizationService;
import com.example.spring.bpmresolver.services.QbpmcockpitService;

import java.util.List;

import com.example.spring.bpmresolver.util.ViewUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.QBPMCOCKPIT_INSTANCES_ACCESS_DENIED_MESSAGE;
import static com.example.spring.bpmresolver.util.UsersUtil.getCurrentUsernameOrNull;

@Controller
@RequestMapping("/qbpmcockpit")
@AllArgsConstructor
public class QbpmcockpitViewController {

    private final QbpmcockpitService qbpmcockpitService;
    private final LocalizationService localizationService;


    @GetMapping("/instances")
    public String instances(
            @RequestParam(value = "processName", required = false) String processName,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "serviceName", required = false) String serviceName,
            @RequestParam(value = "userLogin", required = false) String userLogin,
            @RequestParam(value = "lastStartDate", required = false) String lastStartDate,
            @RequestParam(value = "lastEndDate", required = false) String lastEndDate,
            @RequestParam(value = "businessKey", required = false) String businessKey,
            @RequestParam(value = "isRoot", required = false) Boolean isRoot,
            @RequestParam(value = "withOpenIncidents", required = false) Boolean withOpenIncidents,
            @RequestParam(value = "tenantId", required = false) String tenantId,
            @RequestParam(value = "sort", required = false, defaultValue = "startTime,desc") String sort,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        RestResponsePage<BpmInstanceDto> result = qbpmcockpitService.getInstances(QbpmcockpitInstancesRequest.builder()
                .processName(processName)
                .state(state)
                .serviceName(serviceName)
                .userLogin(userLogin)
                .lastStartDate(lastStartDate)
                .lastEndDate(lastEndDate)
                .businessKey(businessKey)
                .isRoot(isRoot)
                .withOpenIncidents(withOpenIncidents)
                .tenantId(tenantId)
                .sort(sort)
                .page(page)
                .size(size)
                .build());



        model.addAttribute("result", result);
        if ("accessDenied".equals(error)) {
            model.addAttribute("errorMessage",
                    localizationService.getMessage(QBPMCOCKPIT_INSTANCES_ACCESS_DENIED_MESSAGE));
        }
        model.addAttribute("processName", processName);
        model.addAttribute("state", state);
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("userLogin", userLogin);
        model.addAttribute("lastStartDate", lastStartDate);
        model.addAttribute("lastEndDate", lastEndDate);
        model.addAttribute("businessKey", businessKey);
        model.addAttribute("isRoot", isRoot);
        model.addAttribute("withOpenIncidents", withOpenIncidents);
        model.addAttribute("tenantId", tenantId);
        model.addAttribute("sort", sort);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        model.addAttribute("pageNumbers", ViewUtil.getPageNumbers(result, page));

        return "instances";
    }

    @PostMapping("/instances/finish")
    @PreAuthorize("hasRole('ROLE_BPM')")
    public String finishInstances(@RequestParam(value = "id", required = false) List<String> ids,
                                  HttpServletRequest request,
                                  @AuthenticationPrincipal Jwt jwt,
                                  Model model) {
        String referer = request.getHeader("Referer");
        String backUrl = (referer != null && !referer.isBlank()) ? referer : "/qbpmcockpit/instances";

        String finishedBy = getCurrentUsernameOrNull();
        List<BpmFinishedProcessResponseDto> response = qbpmcockpitService.finishInstances(ids, finishedBy);

        model.addAttribute("response", response);
        model.addAttribute("backUrl", backUrl);
        return "finishResult";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String backUrl = (referer != null && !referer.isBlank()) ? referer : "/qbpmcockpit/instances";

        String redirectUrl;
        try {
            redirectUrl = UriComponentsBuilder.fromUriString(backUrl)
                    .replaceQueryParam("error", "accessDenied")
                    .build()
                    .toUriString();
        } catch (IllegalArgumentException e) {
            redirectUrl = "/qbpmcockpit/instances?error=accessDenied";
        }

        return "redirect:" + redirectUrl;
    }
}
