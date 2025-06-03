package com.cropMatch.controller.Farmer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/farmers")
public class FarmerController {

    @GetMapping
    public String showFarmersPage() {
        return "farmers";
    }

}
