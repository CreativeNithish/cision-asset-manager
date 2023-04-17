package com.cision.cisionassetmanager.controller;

import com.cision.cisionassetmanager.service.CisionSFTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class CisionSFTPController {

    @Autowired
    CisionSFTPService cisionSFTPService;

    @GetMapping("/cision/log/assets")
    public void readAssets() {
        cisionSFTPService.readAssets();
    }
}
