package com.pengcunfu.recognition.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengcunfu.recognition.entity.SystemLog;
import com.pengcunfu.recognition.repository.SystemLogRepository;
import com.pengcunfu.recognition.response.PageResponse;
import com.pengcunfu.recognition.response.StatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 系统日志服务
 * 处理系统操作日志记录
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    /**
     * 记录日志（异步，使用 Builder 模式）
     */
    @Async
    public void logAsync(String module, String action, String requestMethod, String requestParams, 
                        String description, Long userId, String ipAddress, Integer responseTime) {
        try {
            SystemLog systemLog = SystemLog.builder()
                    .module(module)
                    .action(action)
                    .requestMethod(requestMethod)
                    .requestParams(requestParams)
                    .description(description)
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .responseTime(responseTime)
                    .status(0) // 0-SUCCESS
                    .build();

            systemLogRepository.insert(systemLog);
        } catch (Exception e) {
            log.error("记录系统日志失败", e);
        }
    }

    /**
     * 获取日志列表（使用 SQL 查询）
     */
    public PageResponse<StatsResponse.SystemLogInfo> getLogs(
            Integer page, Integer size, String module, Long userId, 
            LocalDateTime startTime, LocalDateTime endTime) {
        log.info("获取系统日志: page={}, size={}, module={}, userId={}", 
                page, size, module, userId);

        Page<SystemLog> pageRequest = new Page<>(page, size);
        Page<SystemLog> pageResult = systemLogRepository.findLogsWithConditions(
                pageRequest, module, userId, startTime, endTime
        );

        return PageResponse.<StatsResponse.SystemLogInfo>builder()
                .data(pageResult.getRecords().stream()
                        .map(this::convertToLogInfo)
                        .collect(Collectors.toList()))
                .total(pageResult.getTotal())
                .page((int) pageResult.getCurrent())
                .size((int) pageResult.getSize())
                .pages((int) pageResult.getPages())
                .build();
    }

    /**
     * 转换为日志信息 DTO
     */
    private StatsResponse.SystemLogInfo convertToLogInfo(SystemLog log) {
        return StatsResponse.SystemLogInfo.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .operation(log.getAction())
                .method(log.getRequestMethod())
                .params(log.getRequestParams())
                .result(log.getDescription())
                .ip(log.getIpAddress())
                .duration(log.getResponseTime() != null ? log.getResponseTime().longValue() : 0L)
                .createdAt(log.getCreatedAt())
                .createTime(log.getCreatedAt() != null ? log.getCreatedAt().toString() : null)
                .build();
    }
}

