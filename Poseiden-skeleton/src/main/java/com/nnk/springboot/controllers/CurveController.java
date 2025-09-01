package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.services.CurveService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class CurveController {

    private static final Logger logger = LogManager.getLogger(CurveController.class);

    private final CurveService curveService;

    public CurveController(CurveService curveService) {
        this.curveService = curveService;
    }

    @RequestMapping("/curvePoint/list")
    public String home(Model model) {
        logger.info("Displaying curve point list");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "anonymous";
        model.addAttribute("username", username);
        model.addAttribute("curvePoints", curveService.findAll());
        return "curvePoint/list";
    }

    @GetMapping("/curvePoint/add")
    public String addBidForm(CurvePoint curvePoint) {
        logger.info("Displaying add curve point form");
        return "curvePoint/add";
    }

    @PostMapping("/curvePoint/validate")
    public String validate(@Valid CurvePoint curvePoint, BindingResult result) {
        logger.info("Validating new curve point");
        if (result.hasErrors()) {
            logger.warn("Curve point validation failed");
            return "curvePoint/add";
        }
        curveService.create(curvePoint);
        logger.info("Curve point created successfully");
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for curve point id: {}", id);
        model.addAttribute("curvePoint", curveService.findById(id));
        return "curvePoint/update";
    }

    @PostMapping("/curvePoint/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid CurvePoint curvePoint,
                            BindingResult result) {
        logger.info("Updating curve point id: {}", id);
        if (result.hasErrors()) {
            logger.warn("Curve point update validation failed for id: {}", id);
            curvePoint.setId(id);
            return "curvePoint/update";
        }
        curveService.update(id, curvePoint);
        logger.info("Curve point updated successfully for id: {}", id);
        return "redirect:/curvePoint/list";
    }

    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        logger.info("Deleting curve point id: {}", id);
        curveService.delete(id);
        logger.info("Curve point deleted successfully for id: {}", id);
        return "redirect:/curvePoint/list";
    }
}
