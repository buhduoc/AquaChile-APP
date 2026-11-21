package com.example.AquaCheck_Backend.Controller;

import com.example.AquaCheck_Backend.Service.ItemCheckListService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itemchecklist")
public class ItemCheckListController {

    private final ItemCheckListService service;

    public ItemCheckListController(ItemCheckListService service) {
        this.service = service;
    }
}
