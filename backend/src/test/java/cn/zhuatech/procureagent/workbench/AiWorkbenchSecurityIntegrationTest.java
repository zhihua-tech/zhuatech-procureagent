/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.procureagent.workbench;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cn.zhuatech.procureagent.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@SpringBootTest
@AutoConfigureMockMvc
class AiWorkbenchSecurityIntegrationTest {
  @Autowired MockMvc mvc;
  @Autowired JwtService jwt;

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void securityFilterRejectsAnonymous() throws Exception {
    mvc.perform(
            post("/api/workbench/landed-quote-comparison")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"currency\":\"CNY\",\"items\":[{\"id\":\"SKU1\",\"quantity\":10}],\"quotes\":[{\"supplier\":\"A\",\"currency\":\"CNY\",\"freight\":0,\"taxRate\":0.13,\"lines\":[{\"itemId\":\"SKU1\",\"unitPrice\":20}]},{\"supplier\":\"B\",\"currency\":\"CNY\",\"freight\":10,\"taxRate\":0.13,\"lines\":[{\"itemId\":\"SKU1\",\"unitPrice\":21}]}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void securityFilterRejectsInvalidBearer() throws Exception {
    mvc.perform(
            post("/api/workbench/landed-quote-comparison")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"currency\":\"CNY\",\"items\":[{\"id\":\"SKU1\",\"quantity\":10}],\"quotes\":[{\"supplier\":\"A\",\"currency\":\"CNY\",\"freight\":0,\"taxRate\":0.13,\"lines\":[{\"itemId\":\"SKU1\",\"unitPrice\":20}]},{\"supplier\":\"B\",\"currency\":\"CNY\",\"freight\":10,\"taxRate\":0.13,\"lines\":[{\"itemId\":\"SKU1\",\"unitPrice\":21}]}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedCallerCanCompute() throws Exception {
    mvc.perform(
            post("/api/workbench/landed-quote-comparison")
                .header("Authorization", "Bearer " + jwt.generate("operator"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"currency\":\"CNY\",\"items\":[{\"id\":\"SKU1\",\"quantity\":10}],\"quotes\":[{\"supplier\":\"A\",\"currency\":\"CNY\",\"freight\":0,\"taxRate\":0.13,\"lines\":[{\"itemId\":\"SKU1\",\"unitPrice\":20}]},{\"supplier\":\"B\",\"currency\":\"CNY\",\"freight\":10,\"taxRate\":0.13,\"lines\":[{\"itemId\":\"SKU1\",\"unitPrice\":21}]}]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void authenticatedCallerGetsValidationError() throws Exception {
    mvc.perform(
            post("/api/workbench/landed-quote-comparison")
                .header("Authorization", "Bearer " + jwt.generate("operator"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }
}
