package com.wanglei.zhixing.controller;

import com.wanglei.zhixing.common.Result;
import com.wanglei.zhixing.service.StatsService;
import com.wanglei.zhixing.vo.PlatformStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 今日数据。
 */
@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatsService statsService;

    @GetMapping
    public Result<PlatformStatsVO> today() {
        return Result.success(statsService.today());
    }
}
