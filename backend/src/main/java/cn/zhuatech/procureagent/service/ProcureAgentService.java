/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.procureagent.service;
import cn.zhuatech.procureagent.common.BusinessException; import cn.zhuatech.procureagent.dto.ProcureAgentDto.*; import cn.zhuatech.procureagent.model.*; import cn.zhuatech.procureagent.repository.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service @Transactional(readOnly=true) public class ProcureAgentService {
 private final WorkRecordRepository records; private final ActivityRecordRepository activities; private final ResourceRegisterRepository resources; private final ReviewRecordRepository reviews; private final CurrentUserService current;
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public ProcureAgentService(WorkRecordRepository records,ActivityRecordRepository activities,ResourceRegisterRepository resources,ReviewRecordRepository reviews,CurrentUserService current){this.records=records;this.activities=activities;this.resources=resources;this.reviews=reviews;this.current=current;}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public Dashboard shopfloorDashboard(){String unit=current.get().getOperatingUnitCode();List<WorkRecord> list=unit==null?records.findAllByOrderByDueDateAsc():records.findByOperatingUnitCodeOrderByDueDateAsc(unit);return dashboard(list);}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public Dashboard adminDashboard(){return dashboard(records.findAllByOrderByDueDateAsc());}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 public List<WorkRecordView> workRecords(){return records.findAllByOrderByDueDateAsc().stream().map(this::view).toList();}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 @Transactional public ReportResult report(Long id,ReportRequest request){WorkRecord record=records.findById(id).orElseThrow(()->new BusinessException("业务事项不存在"));if(record.getStatus()==WorkRecord.Status.COMPLETED)throw new BusinessException("已完成事项不能重复提交");if(record.getCompletedQty()+request.goodQty()>record.getPlannedQty())throw new BusinessException("完成步骤不能超过计划步骤总数");record.report(request.goodQty(),request.defectQty());activities.save(new ActivityRecord(record,request.operationName(),request.goodQty(),request.defectQty(),current.get().getFullName(),request.remark()));return new ReportResult(record.getRecordNo(),record.getCompletedQty(),record.getDefectQty(),progress(record),record.getStatus().name());}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private Dashboard dashboard(List<WorkRecord> list){int planned=list.stream().mapToInt(WorkRecord::getPlannedQty).sum(),done=list.stream().mapToInt(WorkRecord::getCompletedQty).sum(),risks=list.stream().mapToInt(WorkRecord::getDefectQty).sum();int rate=planned==0?0:Math.round(done*100f/planned);List<Metric> metrics=List.of(new Metric("在途寻源动作",String.format("%,d",planned),list.size()+" 个在途事项","blue"),new Metric("寻源执行进度",rate+"%",String.format("%,d / %,d",done,planned),"green"),new Metric("评审通过率",String.format("%.1f%%",done+risks==0?100d:done*100d/(done+risks)),risks+" 个待确认项","warn"),new Metric("风险待复核",resources.countByStatus(ResourceRegister.Status.ALARM)+"",reviews.countByResult(ReviewRecord.Result.PENDING)+" 项等待审批","red"));return new Dashboard(metrics,list.stream().map(this::view).toList(),resources.findAllByOrderByCodeAsc().stream().map(e->new ControlView(e.getCode(),e.getName(),e.getOperatingUnit().getName(),e.getStatus().name(),e.getOee(),e.getLastHeartbeat())).toList(),reviews.findTop10ByOrderByIdDesc().stream().map(i->new ReviewRecordView(i.getReviewRecordNo(),i.getWorkRecord().getRecordNo(),i.getWorkRecord().getSubjectName(),i.getReviewRecordType(),i.getReviewRecordQty(),i.getDefectQty(),i.getResult().name(),i.getInspector())).toList());}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private WorkRecordView view(WorkRecord o){return new WorkRecordView(o.getId(),o.getRecordNo(),o.getSubjectCode(),o.getSubjectName(),o.getOperatingUnit().getName(),o.getOperatingUnit().getWorkshop(),o.getPlannedQty(),o.getCompletedQty(),o.getDefectQty(),o.getDueDate(),o.getStatus().name(),o.getVersionNo(),progress(o));}
 /**
  * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
  */
 private int progress(WorkRecord o){return o.getPlannedQty()==0?0:Math.min(100,Math.round(o.getCompletedQty()*100f/o.getPlannedQty()));}
}

