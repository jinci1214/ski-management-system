package com.ski.ticketing.controller.user;

import com.ski.ticketing.pojo.entity.Facility;
import com.ski.ticketing.result.Result;
import com.ski.ticketing.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userFacilityController")
@RequestMapping("/user/facility")
public class FacilityController {
    @Autowired
    private FacilityService service;

    @GetMapping("/list")
    public Result<List<Facility>> list() { return Result.success(service.getAll()); }

    @GetMapping("/resort/{resortId}")
    public Result<List<Facility>> byResort(@PathVariable Long resortId) {
        return Result.success(service.getByResortId(resortId));
    }
}
