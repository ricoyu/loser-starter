1. 注册了一个 TimeBetween route, 用法示例:

   ```yaml
   spring:
     cloud:
       gateway:
         routes:
           - id: between-route
             uri: lb://order-service
             predicates:
               - TimeBetween=7:00, 17:38
   ```

2. 注册了GatewayBlockRequestHandler统一处理限流异常

3. 注册了LoserErrorWebExceptionHandler**统一处理网关层异常**, 这个Handler又是委托给GatewayExceptionHandlerAdvice去真正处理异常并返回REST结果

4. 网关层application.yml要配置clientId, clientSecret, authServerName

5. 注册了SentinelGatewayFilter, 干嘛用的忘记了

6. 如果走网关层认证以及采用了JWT Token, 需要做如下配置

   ```yaml
   loser:
     gateway:
       auth:
         enabled: true
         is-jwt-token: true
   ```

   loser.gateway.auth.jwt-token=true会注册JwtAuthenticationFilter做认证, 否则注册AuthenticationFilter和AuthorizationFilter, 这三个filter是写在loser-cloud-gateway里面的

   **配置示例:**

   ```yaml
   loser:
     gateway:
       auth:
         client-id: gateway_app
         client-secret: MTIzNDU2
         auth-server-name: oauth-service
         enabled: true
         jwt-token: true
         should-skip-urls: /user/info
   ```
   
   
   
   
   
   
   
   
   
   



