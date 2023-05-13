package com.alibaba.csp.sentinel.demo.flow.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

/**
 * <p>把需要控制流量的代码用 Sentinel API SphU.entry("HelloWorld") 和 entry.exit() 包围起来即可。
 * <p>在下面的例子中，我们将 System.out.println("hello world"); 这段代码作为资源，用 API 包围起来。
 *
 * <p>参考：<a href="https://sentinelguard.io/zh-cn/docs/quick-start.html">快速开始</a>
 *
 * @author yuwenbo@kkworld.com
 * @date 2023/4/12
 */
public class SimpleDemo1 {

    private static final String RESOURCE = "HelloWorld";

    public static void main(String[] args) {
        // 配置规则.
        initFlowRules();

        while (true) {
            // 1.5.0 版本开始可以直接利用 try-with-resources 特性
            try (Entry entry = SphU.entry(RESOURCE)) {
                // 被保护的逻辑
                System.out.println("hello world");
            } catch (BlockException ex) {
                // 处理被流控的逻辑
                System.out.println("blocked!");
            }
        }
    }

    /**
     * <p>定义资源 HelloWorld 每秒最多只能通过 20 个请求。
     * <p>查看日志：~/logs/csp/${appName}-metrics.log.xxx
     * <p>参考：<a href="https://github.com/alibaba/Sentinel/wiki/%E6%97%A5%E5%BF%97">日志</a>
     * |--timestamp-|------date time----|--resource-|p |block|s |e|rt
     * <p>p stands for incoming request, block for blocked by rules, s for success handled by Sentinel, e for exception count, rt for average response time (ms), occupied stands for occupiedPassQps since 1.5.0 which enable us booking more than 1 shot when entering.
     *
     */
    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource(RESOURCE);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(20);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
