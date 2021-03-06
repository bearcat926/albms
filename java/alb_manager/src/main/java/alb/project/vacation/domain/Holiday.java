package alb.project.vacation.domain;

import alb.framework.web.domain.BaseEntity;
import alb.project.vacation.utils.LongJsonDeserializer;
import alb.project.vacation.utils.LongJsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author HaoHao
 * Created on 2021/10/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class Holiday extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /* 假期主键 */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long holidayId;

    /* 假期类型 */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long holidayTypeId;

    private String typeName;

    private String typeValue;

    /* 假期时长 */
    private Double holidayDuration;

    /* 请假说明/审批说明 */
    private String holidayInstruction;

    /* 假期事项列表 */
    private List<HolidayItem> items;

    /* 申请人ID */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long proposerId;
    private String proposerName;

    /* 当前审批人ID */
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long currentApproverId;
    private String currentApproverName;

    /* 当前审批序号 从1开始计数 */
    private Integer currentApprovedIndex;

    /* 当前审批状态 0审批中，1通过，2驳回 */
    private Integer status;

    /**
     * 假期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date holidayStartDate;

    /**
     * 假期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date holidayEndDate;

    /**
     * 查询假期时间 - 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date selectStartDate;

    /**
     * 查询假期时间 - 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date selectEndDate;

    /**
     * 删除状态0未1已
     */
    private Integer delFlag;
}
