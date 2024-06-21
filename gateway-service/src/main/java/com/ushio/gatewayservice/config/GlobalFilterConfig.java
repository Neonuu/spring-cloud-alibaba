package com.ushio.gatewayservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

@Component
public class GlobalFilterConfig implements GlobalFilter, Ordered {
    //application.yml配置文件中，设置token在redis中的过期时间
    @Value("${config.redisTimeout}")
    private Long redisTimeout;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String HEADER_NAME = "Acess-Token";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("============过滤器============");

        // 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求地址
        String url = request.getURI().getPath();
        // 获取token信息
        String token = request.getHeaders().getFirst(HEADER_NAME);

        // 判断是否为白名单请求，以及一些内置不需要验证的请求。(登录请求也包含其中)。
        // 如果当前请求中包含token令牌不为空的时候，也会继续验证Token的合法性，这样就能保证
        // Token中的用户信息被业务接口正常访问到了。而如果当token为空的时候，白名单的接口可以
        // 被网关直接转发，无需登录验证。当然被转发的接口，也无法获取到用户的token身份数据了。
        if (this.shouldNotFilter(url)) {
            return chain.filter(exchange);
        }
        if (StringUtils.isEmpty(token)) {
            return unAuthorize(exchange);
        }
        //验证redis中是否存在token
        if(!redisTemplate.hasKey(token)){
            return unAuthorize(exchange);
        }

        //验证通过，刷新token过期时间
        redisTemplate.expire(token,redisTimeout,TimeUnit.SECONDS);
        String userId = String.valueOf(redisTemplate.opsForValue().get(token));
        System.out.println("============登录用户id："+userId+"============");
        //把新的 exchange放回到过滤链
        ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, token).build();
        ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
        return chain.filter(newExchange);

    }

    @Override
    public int getOrder() {
        return 0;
    }

    // 返回未登录的自定义错误
    private Mono<Void> unAuthorize(ServerWebExchange exchange) {
        // 设置错误状态码为401
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 设置返回的信息为JSON类型
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // 自定义错误信息
        String errorMsg = "{\"error\": \"" + "用户未登录或登录超时,请重新登录" + "\"}";
        // 将自定义错误响应写入响应体
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(errorMsg.getBytes())));
    }


    /**
     * 判断当前请求URL是否为白名单地址，以及一些内置的不用登录的接口，
     *
     * @param url 请求的url。
     * @return 是返回true，否返回false。
     */
    private boolean shouldNotFilter(String url) {
//        if (url.startsWith("/user/login")) {
//            return true;
//        }
//        return false;
        return true;
    }

}
