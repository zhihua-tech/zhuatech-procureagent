/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.procureagent.config;
import cn.zhuatech.procureagent.model.*; import cn.zhuatech.procureagent.repository.*; import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.*; import org.springframework.security.crypto.password.PasswordEncoder; import java.time.LocalDate; import java.util.List;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Configuration public class DataInitializer {
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Bean CommandLineRunner seed(OperatingUnitRepository units,WorkRecordRepository records,ResourceRegisterRepository resources,ReviewRecordRepository reviews,UserRepository users,PasswordEncoder encoder){return args->{if(units.count()>0)return;
  OperatingUnit first=units.save(new OperatingUnit("BUY-INDIRECT","间接采购组","采购运营中心",2200)),second=units.save(new OperatingUnit("BUY-ADMIN","行政采购组","采购运营中心",1400)),third=units.save(new OperatingUnit("BUY-IT","IT 采购组","数字化中心",1100));
  WorkRecord a=records.save(new WorkRecord("SRC-260808-018","CAT-IT-CLOUD","研发云资源年度框架采购",first,10,6,2,LocalDate.now().plusDays(2),WorkRecord.Status.RELEASED,"SOURCING-V5")); WorkRecord b=records.save(new WorkRecord("SRC-260808-012","CAT-OFFICE","华东办公耗材集采",second,8,8,0,LocalDate.now().plusDays(0),WorkRecord.Status.COMPLETED,"SOURCING-V4")); WorkRecord c=records.save(new WorkRecord("SRC-260808-021","CAT-IT-NET","门店网络运维服务续约",third,9,4,1,LocalDate.now().plusDays(3),WorkRecord.Status.RUNNING,"SOURCING-V3"));
  resources.saveAll(List.of(new ResourceRegister("POOL-SUP-01","已准入供应商池",first,ResourceRegister.Status.RUNNING,95),new ResourceRegister("DATA-PRICE-02","历史价格只读视图",first,ResourceRegister.Status.RUNNING,92),new ResourceRegister("GUARD-RISK-03","供应商风险核验",third,ResourceRegister.Status.ALARM,74)));
  reviews.saveAll(List.of(new ReviewRecord("REV-PR-028",a,"公平性评测",22,2,ReviewRecord.Result.PENDING,"孟川"),new ReviewRecord("REV-PR-017",b,"计算准确性",48,0,ReviewRecord.Result.PASSED,"周砚"),new ReviewRecord("REV-PR-039",c,"合规审查",18,2,ReviewRecord.Result.FAILED,"任行")));
  String demo=encoder.encode("Demo@2026");
  users.saveAll(List.of(new UserAccount("operator",demo,"周砚",UserAccount.Role.DOMAIN_USER,"BUY-INDIRECT"),new UserAccount("planner",demo,"孟川",UserAccount.Role.DOMAIN_OPERATOR,null),new UserAccount("quality",demo,"评测负责人",UserAccount.Role.QUALITY,null),new UserAccount("admin",encoder.encode("ZhuaTech@2026"),"系统管理员",UserAccount.Role.ADMIN,null)));
 };}}

