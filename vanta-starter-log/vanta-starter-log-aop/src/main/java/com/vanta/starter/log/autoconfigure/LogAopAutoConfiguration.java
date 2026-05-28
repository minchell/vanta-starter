package com.vanta.starter.log.autoconfigure;

import com.vanta.starter.core.constant.OrderedConstants;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.log.annotation.ConditionalOnEnabledLog;
import com.vanta.starter.log.aspect.AccessLogAspect;
import com.vanta.starter.log.aspect.LogAspect;
import com.vanta.starter.log.dao.LogDao;
import com.vanta.starter.log.dao.impl.DefaultLogDaoImpl;
import com.vanta.starter.log.filter.LogFilter;
import com.vanta.starter.log.handler.AopLogHandler;
import com.vanta.starter.log.handler.LogHandler;
import com.vanta.starter.log.model.LogProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


/**
 * AOP 妯″紡鏃ュ織鑷姩閰嶇疆銆?
 *
 * <p>璇ユā寮忛€氳繃 Servlet Filter 缂撳瓨璇锋眰/鍝嶅簲锛屽啀閫氳繃鍒囬潰璇诲彇 Controller 涓婄殑鏃ュ織鍏冩暟鎹苟浜ょ粰 {@link LogHandler} 澶勭悊銆?
 * 涓氬姟鏂瑰彲浠ヨ嚜瀹氫箟 {@link LogHandler} 鎴?{@link LogDao} 瑕嗙洊榛樿澶勭悊涓庢寔涔呭寲琛屼负銆?/p>
 */
@AutoConfiguration
@ConditionalOnEnabledLog
@EnableConfigurationProperties(LogProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class LogAopAutoConfiguration {

    /**
     * AOP 鏃ュ織鑷姩閰嶇疆鏃ュ織銆?
     */
    private static final Logger log = LoggerFactory.getLogger(LogAopAutoConfiguration.class);

    /**
     * 鏃ュ織閲囬泦閰嶇疆銆?
     */
    private final LogProperties logProperties;

    /**
     * 鏃ュ織澶勭悊鍣ㄣ€?
     */

    /**
     * 鍒涘缓 AOP 鏃ュ織鑷姩閰嶇疆銆?
     *
     * @param logProperties 鏃ュ織閲囬泦閰嶇疆
     * @param logHandler    鏃ュ織澶勭悊鍣?
     */
    public LogAopAutoConfiguration(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    /**
     * 娉ㄥ唽鏃ュ織杩囨护鍣ㄣ€?
     *
     * <p>杩囨护鍣ㄨ礋璐ｅ寘瑁呰姹傚拰鍝嶅簲锛屼繚璇佸悗缁垏闈㈠彲浠ュ畨鍏ㄨ鍙栬姹備綋銆佸搷搴斾綋鍜屽弬鏁般€?/p>
     *
     * @return 鏃ュ織杩囨护鍣ㄦ敞鍐屽璞?
     */
    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter(logProperties));
        registrationBean.setOrder(OrderedConstants.Filter.LOG_FILTER);
        registrationBean.addUrlPatterns(StringConstants.PATH_PATTERN_CURRENT_DIR);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return registrationBean;
    }

    /**
     * 娉ㄥ唽鎿嶄綔鏃ュ織鍒囬潰銆?
     *
     * @param logDao 鏃ュ織鎸佷箙灞傛帴鍙?
     * @return 鎿嶄綔鏃ュ織鍒囬潰
     */
    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect(LogHandler logHandler, LogDao logDao) {
        return new LogAspect(logProperties, logHandler, logDao);
    }

    /**
     * 娉ㄥ唽璁块棶鏃ュ織鍒囬潰銆?
     *
     * @return 璁块棶鏃ュ織鍒囬潰
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessLogAspect accessLogAspect(LogHandler logHandler) {
        return new AccessLogAspect(logProperties, logHandler);
    }

    /**
     * 娉ㄥ唽榛樿 AOP 鏃ュ織澶勭悊鍣ㄣ€?
     *
     * @return 榛樿 AOP 鏃ュ織澶勭悊鍣?
     */
    @Bean
    @ConditionalOnMissingBean
    public LogHandler logHandler() {
        return new AopLogHandler();
    }

    /**
     * 娉ㄥ唽榛樿鏃ュ織鎸佷箙灞傘€?
     *
     * <p>榛樿瀹炵幇閫氬父鍙彁渚涘熀纭€琛屼负锛屼笟鍔￠」鐩簲閫氳繃鑷畾涔?{@link LogDao} 瀵规帴鏁版嵁搴撱€佹秷鎭槦鍒楁垨瀹¤绯荤粺銆?/p>
     *
     * @return 榛樿鏃ュ織鎸佷箙灞?
     */
    @Bean
    @ConditionalOnMissingBean
    public LogDao logDao() {
        return new DefaultLogDaoImpl();
    }

    /**
     * 杈撳嚭鑷姩閰嶇疆鍒濆鍖栧畬鎴愭棩蹇椼€?
     */
    @PostConstruct
    public void postConstruct() {
        log.debug("[Vanta Starter] - Auto Configuration 'Log-AOP' completed initialization.");
    }
}
