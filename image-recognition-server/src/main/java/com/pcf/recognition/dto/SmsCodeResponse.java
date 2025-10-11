package com.pcf.recognition.dto;


import lombok.Data;

/**
 * 短信验证码响应
 */
@Data

public class SmsCodeResponse {


    private String phone;

    private Integer codeExpiry;


    private Long sendTime;


    private String status = "success";
}
