/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.procureagent.service;
import jakarta.validation.constraints.*; import org.springframework.stereotype.Service; import java.util.*;
/**
 * 透明的供应商多维评估示例；不会自动邀请、淘汰或定标。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service public class BidEvaluationService {
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record BidRequest(@NotBlank String supplier,@DecimalMin("0.0") @DecimalMax("100.0") double priceScore,@DecimalMin("0.0") @DecimalMax("100.0") double qualityScore,@DecimalMin("0.0") @DecimalMax("100.0") double deliveryScore,@DecimalMin("0.0") @DecimalMax("100.0") double complianceScore,boolean conflictDeclared){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public record BidResult(double weightedScore,String recommendation,boolean humanApprovalRequired,List<String> evidenceChecks){}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public BidResult evaluate(BidRequest r){double score=Math.round((r.priceScore()*.35+r.qualityScore()*.3+r.deliveryScore()*.2+r.complianceScore()*.15)*10d)/10d;String rec=!r.conflictDeclared()?"HOLD_FOR_DISCLOSURE":score>=80?"SHORTLIST":"REVIEW";return new BidResult(score,rec,true,List.of("核对报价范围与币种","核验供应商资质和关联关系","保留采购委员会独立评分"));}}

