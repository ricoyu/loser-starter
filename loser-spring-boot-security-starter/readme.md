# 一 接入说明

1. 实现了验证码功能, 只需要开启该功能选项即可, 不需要自己编码

   ```properties
   loser.security.pic-code.enabled=true
   ```

   默认false

1. 开启认证中心能力

   ```properties
   loser.security.auth-center-enabled=true
   ```

   自己需要提供一个org.springframework.security.core.userdetails.UserDetailsService的实现, 并配置为Spring Bean

2. SpringSecurity核心逻辑的配置类是: LoserWebSecurityAutoConfig

3. TokenDecryptProcessingFilter 

   * `loser.security.token-encrypted`配置是否需要开启token的加密, 是对token做了一层保护, 防止token被复制后调用用于任意接口, 实现一个token只能用在一个接口

   * 如果开启, 前端传token前需要将请求的URI, access_toen, 客户端当前的timestamp先组合成这样一个格式的字符串 uri=/saleOrder/search&access_token=dHDG13ms4868gFNfuk&timestamp=123128371823

   * 再用公钥加密, 将加密后的字符串作为access_token传递过来

     比如先组合成这样一个字符串 uri=/saleOrder/search&access_token=dHDG13ms4868gFNfuk&timestamp=123128371823

     再对这个字符串做RSA公钥加密, 加密后作为Authorization请求头传入, 同时请求的URL后面要加上timestamp=123128371823参数

   * 如果开启token加密, 这个filter会用私钥对token解密, 然后判断解密后得到的uri和实际请求的uri是否一致, 时间戳是否匹配(时间戳相等)

   * 这个filter会从Authorization请求头中拿token, token值是Bearer 开头的
   * 然后回去掉Bearer 前缀拿到真正的token值
   * 构造一个AuthRequest对象, 将token塞进去, 再将这个AuthRequest对象放到ThreadLocal里面供后面的filter使用

4. PreAuthenticationFilter

   从ThreadContext中拿上一个filter塞的AuthRequest对象, 然后到Redis中取验证这个token, 找到token对应的用户名塞到threadLocal里面

   至此, ThreadLocal中一共塞了

   * AUTH_REQUEST  "authRequest" AuthRequest
   * ACCESS_TOKEN  "accessToken" String
   * USERNAME      "username"    String
   * USER_ID       "userId"      Long
   * LOGIN_INFO    "loginInfo"   Map<String, Object>

5. UsernamePasswordAuthenticationFilter

   这是我自定义的, 不是SpringSecurity原生的那个, 全类名为com.loserico.security.filter.UsernamePasswordAuthenticationFilter

   登录成功后交给LoginSuccessHandler生成一个token

6. LoginSuccessHandler

   * 可以配置一个FirstLoginProcessor类型的bean来处理首次登录的特殊需求, 比如首次登录要求修改密码

   * 还可以配置一个loginPolicyService类型的bean来决定是否要单点登录, 单点登录的话在别处已经登录的会先被退出登录

   * 可以通过配置一个SingleLoginMessageProcessor类型的bean来实现当下面这种情况发生时自定义的提示消息:

     当用户不允许在多处登录, 且当前已在别处登录时应该提示给用户的消息

7. LoginFailureHandler

   登录失败时的处理器

   * 如果定义了LoginFailPertimeProcessor类型的bean, 登录失败会再转交给这个bean处理

   * 可以定义不同的AuthenticationFailMessageProcessor类型的bean来基于不同的认证异常产生不同的消息

   * 这个LoginFailureHandler还做了密码错误重试次数的限制, 默认五次, 也可以在业务层面对不同用户有不同的重试次数

     通过定义RetryCountService类型的bean来返回某个用户的重试次数限制

   * 超过重试次数后账户默认锁定5秒, 也可以通过配置一个AccountLockDurationService类型的bean来返回需要锁定的时间

   * 超过重试次数同时会在Redis的retryCount:channel上发布一条消息, 消息的内容是超过重试次数的username

8. 默认的登录URL是: /login

   不需要自己写controller, 可以通过loser.security.login-url配置

9. 配置loser.security.pic-code=true开启图片验证码功能

   * 会自动注册一个VerifyCodeController来生成图片验证码. URL是 GET /pic-code

     返回一个对象类型的JSON, 包含codeId(uuid), code(base64编码的验证码图片)

   * 会自动注册一个VerifyCodeFilter

     从request拿到前端传入的codeId和code, 跟Redis中的比对