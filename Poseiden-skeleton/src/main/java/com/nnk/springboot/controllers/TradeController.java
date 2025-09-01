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

@Controller
public class TradeController {
    private static final Logger logger = LogManager.getLogger(TradeController.class);

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @RequestMapping("/trade/list")
    public String home(Model model) {
        logger.info("Displaying trade list");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth != null ? auth.getName() :"anonymous");
        model.addAttribute("trades", tradeService.findAll());
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addUser(Trade trade) {
        logger.info("Displaying add trade form");
        return "trade/add";
    }

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

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Displaying update form for trade id: {}", id);
        model.addAttribute("trade", tradeService.findById(id));
        return "trade/update";
    }

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

    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id) {
        logger.info("Deleting trade id: {}", id);
        tradeService.delete(id);
        logger.info("Trade deleted successfully for id: {}", id);
        return "redirect:/trade/list";
    }
}
