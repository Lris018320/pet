package com.pet.platform.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 领养寄养申请 DTO
 */
@Data
public class AdoptionApplyDTO {

    @Size(max = 500, message = "留言不能超过 500 字")
    private String message;

    private String contact;
}
