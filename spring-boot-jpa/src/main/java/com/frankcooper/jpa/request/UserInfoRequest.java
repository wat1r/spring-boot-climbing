package com.frankcooper.jpa.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRequest {
    private String firstName;
    private String telephone;
    private String version;
    private String beginCreateTime;
    private String endCreateTime;
    private String addressCity;
}
