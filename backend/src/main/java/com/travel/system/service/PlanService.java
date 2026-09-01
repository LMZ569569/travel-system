package com.travel.system.service;

import com.travel.system.dto.PlanRequest;
import com.travel.system.dto.PlanResult;

/**
 * 旅游规划服务：根据省份、天数、预算智能推荐行程
 */
public interface PlanService {

    /**
     * 按省份 + 天数 + 预算生成推荐行程
     *
     * @param request 规划请求参数
     * @return 按天分组的行程安排
     */
    PlanResult plan(PlanRequest request);
}