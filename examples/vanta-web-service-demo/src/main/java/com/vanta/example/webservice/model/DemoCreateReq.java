package com.vanta.example.webservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DemoCreateReq {

    @NotBlank(message = "名称不能为空")
    @Size(max = 32, message = "名称长度不能超过 32")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
