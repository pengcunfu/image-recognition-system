package com.pcf.recognition.dto;

import lombok.Data;
import lombok.Builder;

/**
 * 系统信息DTO
 */
@Data
@Builder
public class SystemInfoDto {
    
    /** 系统名称 */
    private String name;
    
    /** 系统版本 */
    private String version;
    
    /** 系统描述 */
    private String description;
    
    /** Swagger UI地址 */
    private String swaggerUi;
    
    /** API文档地址 */
    private String apiDocs;
    
    /** 当前时间戳 */
    private Long timestamp;
    
    /** 系统状态 */
    private String status;
    
    /** 作者信息 */
    private String author;
}
