package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.services.TradeService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Trade entity operations.
 */
@Controller
public class TradeController {
    private static final Logger logger = LogManager.getLogger(TradeController.class);

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * Display all trades.
     * @param model view model
     * @return list view
     */
    @RequestMapping("/trade/list")
    public String home(Model model) {
        logger.info("Displaying trade list");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() :"anonymous");
        model.addAttribute("trades", tradeService.findAll());
        return "trade/list";
    }

    /**
     * Show add trade form.
     * @param trade backing object
     * @return add view
     */
    @GetMapping("/trade/add")
    public String addUser(Trade trade) {
        logger.info("Displaying add trade form");
        return "trade/add";
    }

    /**
     * Validate and create a trade.
     * @param trade entity
     * @param result validation result
     * @return redirect or add view on error
     */
    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result) {
        logger.info("Validating new trade");
        if (result.hasErrors()) {
            logger.warn("Trade validation failed");
            return "trade/add";
        }
        tradeService.save(trade);
        logger.info("Trade created successfully");
        return "redirect:/trade/list";
    }

    /**
     * Show update form.
     * @param id trade id
     * @param model view model
     * @return update view
     */
    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for trade id: {}", id);
        model.addAttribute("trade", tradeService.findById(id));
        return "trade/update";
    }

    /**
     * Update a trade.
     * @param id trade id
     * @param trade updated values
     * @param result validation result
     * @return redirect or update view
     */
    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id,
                              @Valid Trade trade,
                              BindingResult result) {
        logger.info("Updating trade id: {}", id);
        if (result.hasErrors()) {
            logger.warn("Trade update validation failed for id: {}", id);
            trade.setTradeId(id);
            return "trade/update";
        }
        tradeService.update(id, trade);
        logger.info("Trade updated successfully for id: {}", id);
        return "redirect:/trade/list";
    }

    /**
     * Delete a trade.
     * @param id trade id
     * @return redirect
     */
    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id) {
        logger.info("Deleting trade id: {}", id);
        tradeService.delete(id);
        logger.info("Trade deleted successfully for id: {}", id);
        return "redirect:/trade/list";
    }
}
