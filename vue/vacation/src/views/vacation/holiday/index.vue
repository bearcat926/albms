<template>
  <div>
    <div class="holiday">
      <!-- 底部表格 -->
      <div class="aui-padded-15 aui-padded-t-0 table_data">

<!--        新增按钮-->
        <div class="flex flex_start aui-padded-b-10 aui-margin-t-10">
          <p class="font-size-20">假期管理</p>
          <div class="aui-padded-l-10 flex">
            <div
              class="addnew bg-theme text-white border-radius font-size-14"
              @click="handleAdd"
              v-hasPermi="['vacation:holiday:add']"
            >新增</div>
          </div>
        </div>

<!--        搜索框 + 列表-->
        <div class="bg-white bg_shadow bg-radius">
          <div class="aui-padded-15 aui-padded-t-10 aui-padded-b-0">
            <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="100px">
              <el-row>
                <el-col :span="20">
                  <el-form-item label="假期类型：" prop="holidayTypeId">
                    <el-select
                      v-model="queryParams.holidayTypeId"
                      placeholder="请选择"
                      clearable
                      filterable
                      size="small"
                      style="width: 200px"
                    >
                      <el-option
                        v-for="dict in holidayTypeList"
                        :key="dict.dictCode"
                        :label="dict.dictLabel"
                        :value="dict.dictCode"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="假期状态：" prop="status">
                    <el-select
                      v-model="queryParams.status"
                      placeholder="请选择"
                      clearable
                      filterable
                      size="small"
                      style="width: 200px"
                    >
                      <el-option
                        v-for="dict in statusList"
                        :key="dict.value"
                        :label="dict.name"
                        :value="dict.value"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="时间：">
                    <el-date-picker
                      v-model="dateRange"
                      style="width: 240px"
                      value-format="yyyy-MM-dd"
                      type="daterange"
                      range-separator="-"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                    ></el-date-picker>
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <div class>
                    <div class="flex export_box" style="float:right">
                      <div class="text-black font-size-14 aui-padded-r-10">共{{total}}条数据</div>
                    </div>
                    <el-form-item style="margin-top:5px">
                      <el-button type="primary" size="mini" @click="handleQuery" class="find_btn">查询</el-button>
                      <el-button size="mini" @click="resetQuery" class="reset_btn">重置</el-button>
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
            </el-form>
          </div>
          <el-table v-loading="loading" :data="holidayList"   @selection-change="handleSelectionChange" stripe >
            <el-table-column type="selection" width="80" align="center" />
            <el-table-column label="申请人" prop="proposerName" align="center" />
            <el-table-column label="当前审批人" prop="currentApproverName" align="center"/>
            <el-table-column label="假期类型" prop="typeName" align="center" />
            <el-table-column label="假期开始时间" prop="holidayStartDate" align="center" />
            <el-table-column label="假期结束时间" prop="holidayEndDate" align="center" />
<!--            <el-table-column label="假期时长" prop="holidayDuration" align="center" />-->
            <el-table-column label="当前审批状态" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.status == 0">审批中</span>
                <span v-if="scope.row.status == 1">已通过</span>
                <span v-if="scope.row.status == 2">已驳回</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  @click="handleDataScope(scope.row)"
                  style="font-size:16px"
                  v-hasPermi="['vacation:holiday:detail']"
                  title="查看详情"
                >
                  <img src="../../../assets/image/chaxun.png" alt />
                </el-button>
<!--                <el-button-->
<!--                  size="mini"-->
<!--                  type="text"-->
<!--                  icon="el-icon-edit"-->
<!--                  @click="handleUpdate(scope.row)"-->
<!--                  v-hasPermi="['vacation:holiday:edit']"-->
<!--                  style="color:#5ECC59 !important;font-size:16px"-->
<!--                  title="编辑"-->
<!--                ></el-button>-->
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                  v-if="scope.row.status == 1"
                  style="color: #E8522A !important;font-size:16px"
                  v-hasPermi="['vacation:holiday:remove']"
                  title="销假"
                ></el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <pagination
          v-show="total>10"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getHolidayList"
        />

        <!-- 添加窗 -->
        <el-dialog class :title="title" :visible.sync="open" width="40%" append-to-body>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-row>
              <el-col :span="12">
                <el-form-item label="假期类型：" prop="holidayTypeId">
                  <el-select
                    v-model="form.holidayTypeId"
                    @change="selectChanged"
                    placeholder="请选择"
                    clearable
                    style="width:100%"
                  >
                    <el-option
                      v-for="dict in holidayTypeList"
                      :key="dict.dictCode"
                      :label="dict.dictLabel"
                      :value="dict.dictCode"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="开始时间：" prop="holidayStartDate">
                  <el-date-picker
                    v-model="form.holidayStartDate"
                    style="width: 240px"
                    format="yyyy-MM-dd HH:mm:ss"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    type="datetime"
                  ></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="结束时间：" prop="holidayEndDate">
                  <el-date-picker
                    v-model="form.holidayEndDate"
                    style="width: 240px"
                    format="yyyy-MM-dd HH:mm:ss"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    type="datetime"
                  ></el-date-picker>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="审批人：" prop="currentApproverId">
                  <el-select
                    v-model="form.currentApproverId"
                    clearable
                    filterable
                    placeholder="请选择审批人"
                    :filter-method="brandKeyChange"
                    style="width:100%"
                    @change="fnEdit"
                  >
                    <el-option
                      v-for="dict in userList"
                      :key="dict.userId"
                      :label="dict.displayName"
                      :value="dict.userId"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="请假说明：" prop="holidayInstruction">
              <el-input type="textarea" v-model="form.holidayInstruction"></el-input>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </el-dialog>

        <!-- 信息窗 -->
        <el-dialog
          title="假期信息"
          :visible.sync="dialogVisible"
          width="40%"
          :before-close="handleClose"
        >
          <div>
            <div class="text-grayer flex">
              <div>假期详情</div>
              <div class="line_dash"></div>
            </div>
            <div class="aui-padded-15 font-size-14">
              <el-row>
                <el-col :span="12" class="aui-padded-b-10">
                  <span>申请人：</span>
                  <span class="aui-padded-l-5 text-graybc">{{holidayInfo.proposerName}}</span>
                </el-col>
                <el-col :span="12" class="aui-padded-b-10">
                  <span>假期类型：</span>
                  <span class="aui-padded-l-5 text-graybc">{{holidayInfo.typeName}}</span>
                </el-col>
<!--                <el-col :span="12" class="aui-padded-b-10">-->
<!--                  <span>假期时长：</span>-->
<!--                  <span class="aui-padded-l-5 text-graybc">{{holidayInfo.holidayDuration}}</span>-->
<!--                </el-col>-->
                <el-col :span="24" class="aui-padded-b-10">
                  <span>假期开始时间：</span>
                  <span class="aui-padded-l-5 text-graybc">{{holidayInfo.holidayStartDate}}</span>
                </el-col>
                <el-col :span="24" class="aui-padded-b-10">
                  <span>假期结束时间：</span>
                  <span class="aui-padded-l-5 text-graybc">{{holidayInfo.holidayEndDate}}</span>
                </el-col>
                <el-col :span="12" class="aui-padded-b-10">
                  <span>当前审批状态：</span>
                  <span class="aui-padded-l-5 text-graybc">{{holidayInfo.status == 0 ? '审批中' : holidayInfo.status == 1 ? '已通过' : '已驳回'}}</span>
                </el-col>
              </el-row>
            </div>
            <div class="text-grayer flex">
              <div>审批详情</div>
              <div class="line_dash"></div>
            </div>
            <div class="aui-padded-15 font-size-14 aui-padded-b-0">
              <el-table :data="holidayInfo.items" stripe >
                <el-table-column label="审批人" prop="approvedUserName" align="center" />
                <el-table-column label="审批状态"align="center">
                  <template slot-scope="scope">
                    <span v-if="scope.row.status == 0">审批中</span>
                    <span v-if="scope.row.status == 1">已通过</span>
                    <span v-if="scope.row.status == 2">已驳回</span>
                  </template>
                </el-table-column>
                <el-table-column label="审批时间" prop="approveTime" align="center" />
                <el-table-column label="审批说明" prop="approveInstruction" align="center" />
              </el-table>
            </div>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getHolidayInfo,
  holidayAdd,
  holidayList,
  holidayRemove,
  holidayUpdate,
  userListForVacation
} from '../../../api/vacation'

export default {
  name: "holiday",
  components: {
  },
  data() {
    return {
      dialogVisible: false,
      // 假期列表
      title: "",
      open: false,
      loading: false,
      total: 0,
      dateRange: "",
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        status: undefined,
        selectStartDate: undefined,
        selectEndDate: undefined,
        holidayTypeId: undefined
      },
      // 表单参数
      form: {
      },
      // 用户列表
      userList: [],
      // 假期类型
      holidayTypeList: [],
      statusList:[
        {
          name: '审批中',
          value: 0
        },
        {
          name: '已通过',
          value: 1
        },
        {
          name: '被驳回',
          value: 2
        },
      ],
      ageList: [],
      holidayList: [],
      rules: {
        holidayTypeId: [
          { required: true, message: "假期类型不能为空", trigger: "blur" },
        ],
        currentApproverId: [
          { required: true, message: "审批人不能为空", trigger: "blur" },
        ],
        holidayStartDate: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
        ],
        holidayEndDate: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
        ],
        holidayInstruction: [
          { required: true, message: "请假说明不能为空", trigger: "blur" },
        ],
      },
      // 客户详情
      textarea: "",
      holidayInfo: {
      },
      infoHoliday: "",
      recordForm: {
        holidayId: undefined,
        type: 1,
        content: "",
      },
    };
  },
  created() {
    this.getHolidayList();
    this.getDicts("holiday_type").then((response) => {
      this.holidayTypeList = response.data;
      console.log(this.holidayTypeList)
    });
  },
  methods: {
    // 多选框选中数据
    handleSelectionChange(e) {
      this.ids = selection.map((item) => item.roleId);
      this.single = selection.length != 1;
      this.multiple = !selection.length;
    },
    // 审批人选择
    fnEdit(vId) {
      this.form.parentHolidayId = vId;
    },
    // 查看假期详细信息
    handleDataScope(row) {
      this.infoHoliday = row;
      this.infoCheck();
    },
    infoCheck() {
      const id = this.infoHoliday.holidayId;
      getHolidayInfo(id).then((response) => {
        if (response.code == 200) {
          console.log(response.data)
          this.holidayInfo = response.data;
          this.dialogVisible = true;
        }
      });
    },
    // 新增确定按钮
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.holidayId != undefined) {
            holidayUpdate(this.form).then((response) => {
              if (response.code == 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.getHolidayList();
              }
            });
          } else {
            holidayAdd(this.form).then((response) => {
              if (response.code == 200) {
                this.msgSuccess("新增成功");
                this.open = false;
                this.getHolidayList();
              }
            });
          }
        }
      });
    },
    // 新增取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    handleDelete(row) {
      const ids = row.holidayId;
      this.$confirm('是否确认对此假期进行销假?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(function () {
          return holidayRemove(ids);
        })
        .then((res) => {
          this.getHolidayList();
          this.msgSuccess("删除成功");
        })
        .catch(function () {});
    },
    /** 新增按钮操作 */
    selectChanged(value){
      let data = {
        holidayTypeId: value
      }
      userListForVacation(data).then((response) => {
        if (response.code == 200) {
          console.log(response.data)
          this.userList = response.data;
        }
      })
    },
    handleAdd() {
      this.reset();
      this.title = "添加假期信息";
      this.open = true;
    },
    /** 修改按钮操作 */
    // handleUpdate(row) {
    //   this.reset();
    //   const id = row.holidayId;
    //   getHolidayInfo(id).then((response) => {
    //     let data = {
    //         holidayId: id,
    //         holidayTypeId: response.data.holidayTypeId,
    //         currentApprovedIndex: response.data.currentApprovedIndex,
    //     }
    //     userListForVacation(data).then((response2) => {
    //       if (response2.code == 200) {
    //         this.form = {
    //           holidayId: response.data.holidayId,
    //           holidayTypeId: response.data.holidayTypeId,
    //           // 可能缺参数
    //         }
    //         this.userList = response2.data;
    //         this.title = "修改假期信息";
    //         this.open = true;
    //       }
    //     })
    //   });
    // },
    brandKeyChange(inputKey){

    },
    // 表单重置
    reset() {
      this.addressForm = [];
      this.form = {
        holidayTypeId: undefined,
        holidayStartDate: undefined,
        holidayEndDate: undefined,
        currentApproverId: undefined,
        holidayInstruction: undefined,
      };
      this.resetForm("form");
    },
    // 查询假期列表
    getHolidayList() {
      this.loading = true;
      holidayList(this.queryParams).then((response) => {
        console.log(response);
        if (response.code == 200) {
          this.holidayList = response.rows;
          this.total = response.total;
          this.loading = false;
        }
      });
    },
    // 重置表单
    resetQuery() {
      this.dateRange = "";
      this.queryParams = {
        pageNum: 1,
        pageSize: 20,
        status: undefined,
        selectStartDate: undefined,
        selectEndDate: undefined,
        holidayTypeId: undefined
      };
      this.getHolidayList();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryParams.selectStartDate = this.dateRange[0]; //开始时间
      this.queryParams.selectEndDate = this.dateRange[1]; //结束时间
      this.getHolidayList();
    },
    // 关闭页面
    handleClose(done) {
      done();
    },
  },
};
</script>

<style  >
.holiday .top_four {
  height: 250px;
}
.holiday .top_four_item {
  width: 24%;
  height: 100%;
  border-radius: 10px;
  position: relative;
}
.holiday .top_four_item_text {
  position: absolute;
  left: 0;
  top: 0;
}

.el-dialog__header {
  border-bottom: 1px solid #eeeeee;
}
.el-dialog__body {
  padding: 20px;
  color: #222;
}
.el-dialog:not(.is-fullscreen) {
  margin-top: 0 !important;
}

.holiday .line_dash {
  border-top: 1px dashed #eeeeee;
  width: 88%;
}
</style>

