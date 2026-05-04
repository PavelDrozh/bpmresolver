package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.entities.BpmFinishedProcess;
import com.example.spring.bpmresolver.services.BpmFinishedProcessService;
import com.example.spring.bpmresolver.util.ViewParamUtil;
import com.example.spring.bpmresolver.util.ViewUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/finished-processes")
@AllArgsConstructor
public class BpmFinishedProcessViewController {

    private final BpmFinishedProcessService service;

    @GetMapping("/deleted")
    public String deletedProcesses(
            @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "fromFinishedAt", required = false) String fromFinishedAt,
            @RequestParam(value = "toFinishedAt", required = false) String toFinishedAt,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt,
            Model model
    ) {
        String finishedBy = (jwt == null) ? null : jwt.getSubject();

        LocalDateTime from = ViewParamUtil.parseDateStart(fromFinishedAt);
        LocalDateTime to = ViewParamUtil.parseDateEnd(toFinishedAt);

        Pageable pageable = PageRequest.of(page, size);
        Page<BpmFinishedProcess> result = service.search(
                finishedBy,
                ViewParamUtil.blankToNull(processInstanceId),
                ViewParamUtil.blankToNull(status),
                ViewParamUtil.blankToNull(message),
                from,
                to,
                pageable
        );

        model.addAttribute("result", result);
        model.addAttribute("processInstanceId", processInstanceId);
        model.addAttribute("status", status);
        model.addAttribute("message", message);
        model.addAttribute("fromFinishedAt", fromFinishedAt);
        model.addAttribute("toFinishedAt", toFinishedAt);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        model.addAttribute("pageNumbers", ViewUtil.getPageNumbers(result, page));

        return "deletedFinishedProcesses";
    }
}
