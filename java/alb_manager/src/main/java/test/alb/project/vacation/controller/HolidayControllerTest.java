package test.alb.project.vacation.controller;

import alb.StartApplication;
import alb.project.vacation.domain.Holiday;
import com.alibaba.fastjson.JSON;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * HolidayController Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>12/12/2021</pre>
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HolidayControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        // 实例化方式一
//        mvc = MockMvcBuilders.standaloneSetup(new HolidayController()).build();
        // 实例化方式二
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: queryHoliday(@PathVariable("holidayId") Long holidayId)
     */
    @Test
    public void testQueryHoliday() throws Exception {
        Long mock = 1463543151288520704L;

        /*
         * 1、mvc.perform执行一个请求。
         * 2、MockMvcRequestBuilders.get("XXX")构造一个请求。
         * 3、ResultActions.param添加请求传值
         * 4、ResultActions.accept(MediaType.TEXT_HTML_VALUE))设置返回类型
         * 5、ResultActions.andExpect添加执行完成后的断言。
         * 6、ResultActions.andDo添加一个结果处理器，表示要对结果做点什么事情
         *   比如此处使用MockMvcResultHandlers.print()输出整个响应结果信息。
         * 7、ResultActions.andReturn表示执行完成后返回相应的结果。
         */
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .get("/vacation/holiday/{holidayId}", mock)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
//                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
//                    .param("holidayId", String.valueOf(53L))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("$..holidayId").value(mock))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        String result = response.getContentAsString();
        Holiday holiday = JSON.toJavaObject(JSON.parseObject(result).getJSONObject("data"),
                Holiday.class);

        Assert.assertEquals(mock, holiday.getHolidayId());
    }

    /**
     * Method: hasNext(@PathVariable("holidayId") Long holidayId)
     */
    @Test
    public void testHasNext() throws Exception {
        Long mock = 1463544612969910272L`;

        /*
         * 1、mvc.perform执行一个请求。
         * 2、MockMvcRequestBuilders.get("XXX")构造一个请求。
         * 3、ResultActions.param添加请求传值
         * 4、ResultActions.accept(MediaType.TEXT_HTML_VALUE))设置返回类型
         * 5、ResultActions.andExpect添加执行完成后的断言。
         * 6、ResultActions.andDo添加一个结果处理器，表示要对结果做点什么事情
         *   比如此处使用MockMvcResultHandlers.print()输出整个响应结果信息。
         * 7、ResultActions.andReturn表示执行完成后返回相应的结果。
         */
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                        .get("/vacation/holiday/next/{holidayId}", mock)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
//                    .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
//                    .param("holidayId", String.valueOf(53L))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(jsonPath("$..holidayId").value(mock))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();
    }

    /**
     * Method: queryList(Holiday holiday)
     */
    @Test
    public void testQueryList() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: queryApprovalList(Holiday holiday)
     */
    @Test
    public void testQueryApprovalList() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: queryUserList(Holiday holiday)
     */
    @Test
    public void testQueryUserList() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: addHoliday(@RequestBody Holiday holiday)
     */
    @Test
    public void testAddHoliday() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: modifyHoliday(@RequestBody Holiday holiday)
     */
    @Test
    public void testModifyHoliday() throws Exception {
        //TODO: Test goes here...
    }

    /**
     * Method: deleteById(@PathVariable("holidayId") Long holidayId)
     */
    @Test
    public void testDeleteById() throws Exception {
        //TODO: Test goes here...
    }


} 
