package test.alb.project.vacation.controller;

import alb.StartApplication;
import alb.common.constant.Constants;
import alb.common.exception.CustomException;
import alb.common.exception.user.CaptchaException;
import alb.common.exception.user.CaptchaExpireException;
import alb.common.exception.user.UserPasswordNotMatchException;
import alb.common.utils.IdUtils;
import alb.common.utils.MessageUtils;
import alb.common.utils.ServletUtils;
import alb.common.utils.ip.AddressUtils;
import alb.common.utils.ip.IpUtils;
import alb.framework.manager.AsyncManager;
import alb.framework.manager.factory.AsyncFactory;
import alb.framework.redis.RedisCache;
import alb.framework.security.LoginUser;
import alb.framework.security.service.SysLoginService;
import alb.framework.security.service.TokenService;
import alb.project.vacation.domain.Holiday;
import alb.project.vacation.resultVO.HolidayUserResultVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    /* Services */
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    MockHttpSession mockHttpSession = new MockHttpSession();

    SecurityContext ctx;

    /* params */
    @Value("${token.secret}")
    private String secret;

    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    /* Model */
    private MockMvc mvc;

    private String token;

    private String userKey;

    @Before
    public void setUp() throws Exception {
        // Instantiation method 1
//        mvc = MockMvcBuilders.standaloneSetup(new HolidayController()).build();
        // Instantiation method 2
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Before
    public void before() throws Exception {
        ctx = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(ctx);

        login(RoleType.ADMIN);
    }

    @After
    public void after() throws Exception {
        // remove
        redisCache.deleteObject(userKey);
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
        /*MockHttpServletResponse response = */mvc.perform(MockMvcRequestBuilders
                .get("/vacation/holiday/{holidayId}", mock)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("holidayId", String.valueOf(53L))
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$..holidayId").value(mock))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

//        String result = response.getContentAsString();
//        Holiday holiday = JSON.toJavaObject(JSON.parseObject(result).getJSONObject("data"),
//                Holiday.class);

//        Assert.assertEquals(mock, holiday.getHolidayId());
    }

    /**
     * Method: hasNext(@PathVariable("holidayId") Long holidayId)
     */
    @Test
    public void testHasNext() throws Exception {
        Long mock = 1463544612969910272L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/vacation/holiday/next/{holidayId}", mock)
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();
    }

    /**
     * Method: queryList(Holiday holiday)
     */
    @Test
    public void testQueryList() throws Exception {
        Long holidayTypeId = 33L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/vacation/holiday/list")
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
                .param("holidayTypeId", String.valueOf(holidayTypeId))
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        String result = response.getContentAsString();
        JSONArray array = (JSONArray) JSON.parseObject(result).get("rows");
        for (Object o : array) {
            Holiday holiday = JSON.toJavaObject((JSONObject) o, Holiday.class);
            Assert.assertEquals(holidayTypeId, holiday.getHolidayTypeId());
        }
    }

    /**
     * Method: queryApprovalList(Holiday holiday)
     */
    @Test
    public void testQueryApprovalList() throws Exception {
        Long holidayTypeId = 33L;

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/vacation/holiday/approvalList")
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
                .param("holidayTypeId", String.valueOf(holidayTypeId))
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        String result = response.getContentAsString();
        JSONArray array = (JSONArray) JSON.parseObject(result).get("rows");
        for (Object o : array) {
            Holiday holiday = JSON.toJavaObject((JSONObject) o, Holiday.class);
            Assert.assertEquals(holidayTypeId, holiday.getHolidayTypeId());
        }
    }

    /**
     * Method: queryUserList(Holiday holiday)
     */
    @Test
    public void testQueryUserList() throws Exception {
        login(RoleType.STAFF);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("holidayTypeId", "31");
        params.add("currentApprovedIndex", "1");

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .get("/vacation/holiday/user/list")
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
                .params(params)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        String result = response.getContentAsString();
        JSONArray array = (JSONArray) JSON.parseObject(result).get("data");
        Assert.assertTrue(array.size() > 0);
    }

    /**
     * Method: addHoliday(@RequestBody Holiday holiday)
     */
    @Test
    public void testAddHoliday() throws Exception {
        login(RoleType.STAFF);

        Holiday mock = Holiday.builder()
                .holidayTypeId(33L)
                .holidayStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-12-01 08:00:00"))
                .holidayEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-12-01 18:00:00"))
                .currentApproverId(3L) // James
                .holidayInstruction("unitTest")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(mock);

        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders
                .post("/vacation/holiday")
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();
    }

    /**
     * Method: modifyHoliday(@RequestBody Holiday holiday)
     */
    @Test
    public void testModifyHoliday() throws Exception {
        login(RoleType.MANAGER);
        Holiday mock = Holiday.builder()
                .holidayId(1470685235363581952L)
                .holidayTypeId(33L)
                .currentApprovedIndex(1)
                .proposerId(2L) // Olajuwon
                .currentApproverId(4L) // Jordan
                .status(1)
                .holidayInstruction("unitTest_manager")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(mock);

        mvc.perform(MockMvcRequestBuilders
                .put("/vacation/holiday")
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();

        login(RoleType.BOSS);
        mock = Holiday.builder()
                .holidayId(1470685235363581952L)
                .holidayTypeId(33L)
                .currentApprovedIndex(2)
                .proposerId(2L) // Olajuwon
                .status(1)
                .holidayInstruction("unitTest_boss")
                .build();

        requestBody = objectMapper.writeValueAsString(mock);

        mvc.perform(MockMvcRequestBuilders
                .put("/vacation/holiday")
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
                .contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();
    }

    /**
     * Method: deleteById(@PathVariable("holidayId") Long holidayId)
     */
    @Test
    public void testDeleteById() throws Exception {
        Long mock = 1470685235363581952L;

        mvc.perform(MockMvcRequestBuilders
                .delete("/vacation/holiday/{holidayId}", mock)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("authorization", "Bearer " + token)
                .requestAttr("token", token)
        )
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse();
    }

    /* Other Method */
    public void login(RoleType type) {
        // Get Authentication for the Current User
        String username;
        String password;

        switch (type) {
            case ADMIN:
                username = "admin";
                password = "123456";
                break;
            case STAFF:
                username = "Olajuwon";
                password = "123456";
                break;
            case MANAGER:
                username = "James";
                password = "123456";
                break;
            case BOSS:
                username = "Jordan";
                password = "123456";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        // Set Authentication for SecurityContext
        ctx.setAuthentication(authentication);

        // Set UUID for the Current User
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        String uuid = IdUtils.fastUUID();
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, uuid);
        loginUser.setToken(uuid);

        // Set Other Attributes
        String ip = "127.0.0.1";
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser("Chrome 8");
        loginUser.setOs("Windows 10");
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);

        // Cache loginUser by UUID
        if (userKey != null) {
            redisCache.deleteObject(userKey);
        }
        userKey = Constants.LOGIN_TOKEN_KEY + loginUser.getToken();
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);

        // Get Request Token
        token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();

        // Set Session
        mockHttpSession.setAttribute("token", token);
    }

    enum RoleType {
        ADMIN(0), STAFF(1), MANAGER(2), BOSS(3);

        private final Integer value;

        RoleType(int value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
