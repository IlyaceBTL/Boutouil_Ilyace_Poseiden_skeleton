package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.services.RuleNameService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller for RuleName CRUD lifecycle.
 */
@Controller
@RequestMapping("/ruleName")
public class RuleNameController {

    private static final Logger logger = LogManager.getLogger(RuleNameController.class);

    private final RuleNameService ruleNameService;

    public RuleNameController(RuleNameService ruleNameService) {
        this.ruleNameService = ruleNameService;
    }

    /**
     * Display all rule names.
     * @param model view model
     * @return list view
     */
    @GetMapping("/list")
    public String home(Model model) {
        logger.info("Displaying rule name list");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "anonymous";
        model.addAttribute("username", username);
        model.addAttribute("ruleNames", ruleNameService.findAll());
        return "ruleName/list";
    }

    /**
     * Show add rule name form.
     * @param model view model
     * @return add view
     */
    @GetMapping("/add")
    public String addRuleForm(Model model) {
        logger.info("Displaying add rule name form");
        model.addAttribute("ruleName", new RuleName());
        return "ruleName/add";
    }

    /**
     * Validate and create a rule name.
     * @param ruleName entity
     * @param result validation result
     * @return redirect or form view
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("ruleName") RuleName ruleName, BindingResult result) {
        logger.info("Validating new rule name");
        if (result.hasErrors()) {
            logger.warn("Rule name validation failed");
            return "ruleName/add";
        }
        ruleNameService.create(ruleName);
        logger.info("Rule name created successfully");
        return "redirect:/ruleName/list";
    }

    /**
     * Show update form.
     * @param id rule id
     * @param model view model
     * @return update view
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for rule name id: {}", id);
        RuleName ruleName = ruleNameService.findById(id);
        model.addAttribute("ruleName", ruleName);
        return "ruleName/update";
    }

    /**
     * Update a rule name.
     * @param id rule id
     * @param ruleName updated values
     * @param result validation result
     * @return redirect or update view
     */
    @PostMapping("/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Valid @ModelAttribute("ruleName") RuleName ruleName,
                                 BindingResult result) {
        logger.info("Updating rule name id: {}", id);
        if (result.hasErrors()) {
            logger.warn("Rule name update validation failed for id: {}", id);
            ruleName.setId(id);
            return "ruleName/update";
        }
        ruleNameService.update(id, ruleName);
        logger.info("Rule name updated successfully for id: {}", id);
        return "redirect:/ruleName/list";
    }

    /**
     * Delete a rule name.
     * @param id rule id
     * @return redirect
     */
    @GetMapping("/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id) {
        logger.info("Deleting rule name id: {}", id);
        ruleNameService.delete(id);
        logger.info("Rule name deleted successfully for id: {}", id);
        return "redirect:/ruleName/list";
    }
}
