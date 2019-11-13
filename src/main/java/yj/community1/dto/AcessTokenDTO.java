package yj.community1.dto;

import lombok.Data;

/*
Created by yj on 2019/10/26
 */
@Data
public class AcessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;


}
