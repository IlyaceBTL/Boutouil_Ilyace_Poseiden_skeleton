package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/bidList")
public class BidListController {

    private static final Logger logger = LogManager.getLogger(BidListController.class);

    private final BidListService bidListService;

    public BidListController(BidListService bidListService) {
        this.bidListService = bidListService;
    }

    @GetMapping("/list")
    public String home(Model model) {
        logger.info("Displaying bid list");
        model.addAttribute("bidLists", bidListService.findAll());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() : "anonymous");
        return "bidList/list";
    }

    @GetMapping("/add")
    public String addBidForm(Model model) {
        logger.info("Displaying add bid form");
        model.addAttribute("bidList", new BidList());
        return "bidList/add";
    }

    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("bidList") BidList bid, BindingResult result) {
        logger.info("Validating new bid");
        if (result.hasErrors()) {
            logger.warn("Bid validation failed");
            return "bidList/add";
        }
        bidListService.create(bid);
        logger.info("Bid created successfully");
        return "redirect:/bidList/list";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for bid id: {}", id);
        model.addAttribute("bidList", bidListService.findById(id));
        return "bidList/update";
    }

    @PostMapping("/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("bidList") BidList bidList,
                            BindingResult result) {
        logger.info("Updating bid id: {}", id);
        bidList.setBidListId(id);
        if (result.hasErrors()) {
            logger.warn("Bid update validation failed for id: {}", id);
            return "bidList/update";
        }
        bidListService.update(id, bidList);
        logger.info("Bid updated successfully for id: {}", id);
        return "redirect:/bidList/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id) {
        logger.info("Deleting bid id: {}", id);
        bidListService.delete(id);
        logger.info("Bid deleted successfully for id: {}", id);
        return "redirect:/bidList/list";
    }
}
