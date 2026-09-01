package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.dto.PlanRequest;
import com.travel.system.dto.PlanResult;
import com.travel.system.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 旅游规划接口
 */
@RestController
@RequestMapping("/api/plan")
public class PlanController {

    @Autowired
    private PlanService planService;

    /**
     * 生成推荐行程
     * POST /api/plan
     * Body: {"province":"浙江","days":3,"budget":800}
     */
    @PostMapping
    public Result<PlanResult> plan(@RequestBody PlanRequest request) {
        return Result.success(planService.plan(request));
    }
}