package alb.project.vacation.controller;

import alb.common.utils.ServletUtils;
import alb.common.utils.StringUtils;
import alb.framework.aspectj.lang.annotation.Log;
import alb.framework.aspectj.lang.enums.BusinessType;
import alb.framework.security.LoginUser;
import alb.framework.security.service.TokenService;
import alb.framework.web.controller.BaseController;
import alb.framework.web.domain.AjaxResult;
import alb.framework.web.page.TableDataInfo;
import alb.project.system.domain.SysDept;
import alb.project.system.domain.SysRole;
import alb.project.vacation.domain.Holiday;
import alb.project.vacation.domain.HolidayItem;
import alb.project.vacation.service.IHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HaoHao
 * Created on 2021/1/26.
 */

@RestController
@RequestMapping("/vacation/holiday")
public class HolidayController extends BaseController {

    @Resource // @Resource（这个注解属于J2EE的），默认按照名称进行装配
    private IHolidayService holidayService;

    @Autowired // @Autowired（这个注解是属于spring的），默认按类型装配
    private TokenService tokenService;

    /**
     * 查询单条数据
     *
     * @param holidayId 主键
     * @return 实例对象
     */
    @GetMapping("/{holidayId}")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:query')")
    public AjaxResult queryHoliday(@PathVariable("holidayId") Long holidayId) {
        Holiday result = this.holidayService.queryOne(holidayId);
        return result != null ? AjaxResult.success(result) : AjaxResult.error();
    }

    /**
     * 查询多条数据
     *
     * @param holiday
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:list')")
    public TableDataInfo queryList(Holiday holiday) {
        startPage();
        List<Holiday> list = this.holidayService.queryAll(holiday);
        return getDataTable(list);
    }

    /**
     * 查询多条数据
     *
     * @param holiday
     * @return
     */
    @GetMapping("/user/list")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:add')")
    public AjaxResult queryUserList(Holiday holiday) {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        if (!StringUtils.isNull(loginUser) && !CollectionUtils.isEmpty(loginUser.getUser().getRoles()))
        {
            SysDept dept = loginUser.getUser().getDept();
            List<SysRole> roles = loginUser.getUser().getRoles();
        }
        return AjaxResult.error("当前用户暂无权限！");
    }

    /**
     * 新增数据
     *
     * @param holiday 实例对象
     * @return 实例对象
     */
    @PostMapping("/")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:add')")
    @Log(title = "假期", businessType = BusinessType.INSERT)
    public AjaxResult addHoliday(@RequestBody Holiday holiday) {
        int count = this.holidayService.insert(holiday);
        return count > 0 ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败");
    }

    /**
     * 新增事项数据
     *
     * @param holidayItem 实例对象
     * @return 实例对象
     */
    @PostMapping("/item")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:add')")
    @Log(title = "假期", businessType = BusinessType.INSERT)
    public AjaxResult addHolidayItem(@RequestBody HolidayItem holidayItem) {
        int count = this.holidayService.insertItem(holidayItem);
        return count > 0 ? AjaxResult.success("新增成功") : AjaxResult.error("新增失败");
    }

    /**
     * 修改数据
     *
     * @param holiday 实例对象
     * @return 实例对象
     */
    @PutMapping("")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:modify')")
    @Log(title = "假期", businessType = BusinessType.UPDATE)
    public AjaxResult modifyHoliday(@RequestBody Holiday holiday) {
        int count = this.holidayService.update(holiday);
        return count > 0 ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 修改事项数据
     *
     * @param holidayItem 实例对象
     * @return 实例对象
     */
    @PutMapping("/item")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:modify')")
    @Log(title = "假期事项", businessType = BusinessType.UPDATE)
    public AjaxResult modifyHolidayItem(@RequestBody HolidayItem holidayItem) {
        int count = this.holidayService.updateItem(holidayItem);
        return count > 0 ? AjaxResult.success("修改成功") : AjaxResult.error("修改失败");
    }

    /**
     * 通过主键删除数据
     *
     * @param holidayId 主键
     * @return 是否成功
     */
    @DeleteMapping("/{holidayId}")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:delete')")
    @Log(title = "假期", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@PathVariable("holidayId") Long holidayId) {
        int count = this.holidayService.deleteById(holidayId);
        return count > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 通过主键删除数据
     *
     * @param holidayId 主键
     * @return 是否成功
     */
    @DeleteMapping("/{holidayId}")
    @PreAuthorize("@ss.hasPermi('vacation:holiday:delete')")
    @Log(title = "假期", businessType = BusinessType.DELETE)
    public AjaxResult deleteById(@PathVariable("holidayId") Long holidayId) {
        int count = this.holidayService.deleteById(holidayId);
        return count > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }
}
