package alb.project.vacation.controller;

import alb.framework.aspectj.lang.annotation.Log;
import alb.framework.aspectj.lang.enums.BusinessType;
import alb.framework.web.controller.BaseController;
import alb.framework.web.domain.AjaxResult;
import alb.framework.web.page.TableDataInfo;
import alb.project.vacation.domain.HolidayApproval;
import alb.project.vacation.service.IHolidayApprovalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HaoHao
 * Created on 2021/1/26.
 */

@RestController
@RequestMapping("/vacation/holiday/approval")
public class HolidayApprovalController extends BaseController {

    @Resource // @Resource（这个注解属于J2EE的），默认按照名称进行装配
    private IHolidayApprovalService holidayApprovalService;

    /**
     * 查询单条数据
     *
     * @param holidayApprovalId 主键
     * @return 实例对象
     */
    @GetMapping("/{holidayApprovalId}")
    @PreAuthorize("@ss.hasPermi('vacation:holidayApproval:query')")
    public AjaxResult queryHolidayApproval(@PathVariable("holidayApprovalId") Long holidayApprovalId) {
        HolidayApproval result = this.holidayApprovalService.queryOne(holidayApprovalId);
        return result != null ? AjaxResult.success(result) : AjaxResult.error();
    }

    /**
     * 查询多条数据
     *
     * @param holidayApproval
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('vacation:holidayApproval:list')")
    public TableDataInfo queryList(HolidayApproval holidayApproval) {
        startPage();
        List<HolidayApproval> list = this.holidayApprovalService.queryAll(holidayApproval);
        return getDataTable(list);
    }

    /**
     * 查询角色列表
     *
     * @return 对象列表
     */
    @GetMapping("/roleList")
    @PreAuthorize("@ss.hasPermi('vacation:holidayApproval:list')")
    public AjaxResult queryHolidayApprovalList() {
        return AjaxResult.success(this.holidayApprovalService.selectRoleList());
    }

    /**
     * 新增数据
     *
     * @param holidayApproval 实例对象
     * @return 实例对象
     */
    @PostMapping("")
    @PreAuthorize("@ss.hasPermi('vacation:holidayApproval:add')")
    @Log(title = "假期", businessType = BusinessType.INSERT)
    public AjaxResult addHolidayApproval(@RequestBody HolidayApproval holidayApproval) {
        if (this.holidayApprovalService.hasRing(holidayApproval)) {
            return AjaxResult.error("新增失败，无法添加该项");
        }
        int count = this.holidayApprovalService.insert(holidayApproval);
        return count > 0 ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败");
    }

    /**
     * 通过主键删除数据
     *
     * @param holidayApprovalId 主键
     * @return 是否成功
     */
    @DeleteMapping("/{holidayApprovalId}")
    @PreAuthorize("@ss.hasPermi('vacation:holidayApproval:delete')")
    @Log(title = "假期", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@PathVariable("holidayApprovalId") Long holidayApprovalId) {
        int count = this.holidayApprovalService.deleteById(holidayApprovalId);
        return count > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}
