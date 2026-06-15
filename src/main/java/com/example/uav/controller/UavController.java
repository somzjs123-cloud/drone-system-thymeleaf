package com.example.uav.controller;

import com.example.uav.common.PageResult;
import com.example.uav.domain.dto.UavCreateRequest;
import com.example.uav.domain.dto.UavDTO;
import com.example.uav.domain.dto.UavUpdateRequest;
import com.example.uav.domain.query.UavQuery;
import com.example.uav.service.AiAttributeService;
import com.example.uav.service.UavService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UavController {

    private final UavService uavService;
    private final AiAttributeService aiAttributeService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping({"/", "/uav/list"})
    public String listPage(Model model, UavQuery query) {
        PageResult<UavDTO> pageResult = uavService.listUav(query);
        model.addAttribute("pageData", pageResult);
        model.addAttribute("query", query);
        return "list";
    }

    @GetMapping("/uav/add")
    public String addPage(Model model) {
        model.addAttribute("request", new UavCreateRequest());
        return "add";
    }

    @PostMapping("/uav/add")
    public String doAdd(@Valid UavCreateRequest request, Model model) {
        uavService.createUav(request);
        return "redirect:/uav/list";
    }

    @GetMapping("/uav/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        UavDTO dto = uavService.getUavById(id);
        UavUpdateRequest request = new UavUpdateRequest();
        request.setId(dto.getId());
        request.setModel(dto.getModel());
        request.setManufacturer(dto.getManufacturer());
        request.setMaxPayload(dto.getMaxPayload());
        request.setMaxAltitude(dto.getMaxAltitude());
        request.setMaxFlightTime(dto.getMaxFlightTime());
        request.setMaxSpeed(dto.getMaxSpeed());
        request.setWingspan(dto.getWingspan());
        request.setWeight(dto.getWeight());
        request.setStatus(dto.getStatus());
        request.setRemark(dto.getRemark());
        model.addAttribute("request", request);
        return "edit";
    }

    @PostMapping("/uav/edit/{id}")
    public String doEdit(@PathVariable Long id, @Valid UavUpdateRequest request) {
        request.setId(id);
        uavService.updateUav(request);
        return "redirect:/uav/list";
    }

    @GetMapping("/uav/delete/{id}")
    public String delete(@PathVariable Long id) {
        uavService.deleteUav(id);
        return "redirect:/uav/list";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "403";
    }
}
