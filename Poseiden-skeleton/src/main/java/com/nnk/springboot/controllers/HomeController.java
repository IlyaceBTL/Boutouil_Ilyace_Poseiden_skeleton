package com.nnk.springboot.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for general navigation (home and admin redirect).
 */
@Controller
public class HomeController
{
    private static final Logger logger = LogManager.getLogger(HomeController.class);

    /**
     * Display public home page.
     * @param model view model
     * @return home view
     */
    @RequestMapping("/")
    public String home(Model model)
    {
        logger.info("Displaying home page");
        return "home";
    }

    /**
     * Redirect admin home to bid list.
     * @param model view model
     * @return redirect path
     */
    @RequestMapping("/admin/home")
    public String adminHome(Model model)
    {
        logger.info("Redirecting to bid list from admin home");
        return "redirect:/bidList/list";
    }
}
