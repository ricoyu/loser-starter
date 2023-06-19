# 接入说明

1. 接入方要提供Oauth2相关表, 配置好DataSource

   如果要基于模块粒度做权限控制, sys_permission表的Uri可以配置成 /account/** 这种形式, 具体API设计的时候, 同一模块的API收敛到同一前缀下, 这样类似/user/getUserInfo/1, /user/getUserInfo/2这种API就不需要每个ID都配一个权限, 会疯掉

   ```mysql
   drop database if exists oauth;
   create database oauth default character set utf8mb4 default collate utf8mb4_general_ci;
   grant all on oauth.* to 'oauth'@'%' identified by '123456';
   flush privileges;
   use oauth;
   
   create table oauth_client_details
   (
       client_id               VARCHAR(256) PRIMARY KEY,
       client_secret           VARCHAR(256),
       resource_ids            VARCHAR(256),
       scope                   VARCHAR(256),
       authorized_grant_types  VARCHAR(256),
       web_server_redirect_uri VARCHAR(256),
       authorities             VARCHAR(256),
       access_token_validity   INTEGER,
       refresh_token_validity  INTEGER,
       additional_information  VARCHAR(4096),
       autoapprove             VARCHAR(256)
   );
   
   create table oauth_client_token
   (
       token_id          VARCHAR(256),
       token             varchar(256),
       authentication_id VARCHAR(256) PRIMARY KEY,
       user_name         VARCHAR(256),
       client_id         VARCHAR(256)
   );
   
   create table oauth_access_token
   (
       token_id          VARCHAR(256),
       token             VARCHAR(256),
       authentication_id VARCHAR(256) PRIMARY KEY,
       user_name         VARCHAR(256),
       client_id         VARCHAR(256),
       authentication    VARCHAR(256),
       refresh_token     VARCHAR(256)
   );
   
   create table oauth_refresh_token
   (
       token_id       VARCHAR(256),
       token          VARCHAR(256),
       authentication VARCHAR(256)
   );
   
   create table oauth_code
   (
       code           VARCHAR(256),
       authentication VARCHAR(256)
   );
   
   create table oauth_approvals
   (
       userId         VARCHAR(256),
       clientId       VARCHAR(256),
       scope          VARCHAR(256),
       status         VARCHAR(10),
       expiresAt      TIMESTAMP,
       lastModifiedAt TIMESTAMP
   );
   
   
   -- customized oauth_client_details table
   create table ClientDetails
   (
       appId                  VARCHAR(256) PRIMARY KEY,
       resourceIds            VARCHAR(256),
       appSecret              VARCHAR(256),
       scope                  VARCHAR(256),
       grantTypes             VARCHAR(256),
       redirectUrl            VARCHAR(256),
       authorities            VARCHAR(256),
       access_token_validity  INTEGER,
       refresh_token_validity INTEGER,
       additionalInformation  VARCHAR(4096),
       autoApproveScopes      VARCHAR(256)
   );
   
   
   create table sys_user(
       id int primary key auto_increment,
       username VARCHAR(50) not null,
       password VARCHAR(100) not null,
       nickname VARCHAR(50) not null,
       email VARCHAR(50) not null,
       status TINYINT default 0,
       create_user VARCHAR(50),
       create_time datetime,
       update_user VARCHAR(50),
       update_time datetime);
   
   INSERT INTO `oauth`.`sys_user` (`id`, `username`, `password`, `nickname`, `email`, `status`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES (1, 'yuxh', '$2a$10$YIm4d0W9oHzIG6DLWsFTNOJscI3peSz6gRzcTeW/58TQAbwkJuniC', '三少爷', 'ricoyu520@gmail.com', 0, 'system', '2022-10-12 17:01:45', 'system', '2022-10-12 17:01:51');
   
   
   CREATE TABLE sys_user_role (
      id INT PRIMARY KEY auto_increment,
      user_id INT,
      role_id INT);
   INSERT INTO `oauth`.`sys_user_role` (`id`, `user_id`, `role_id`) VALUES (1, 1, 1);
   
   CREATE TABLE sys_role (
     id INT PRIMARY KEY auto_increment,
     role_name VARCHAR ( 20 ) NOT NULL,
     role_code VARCHAR ( 20 ) NOT NULL,
     role_description VARCHAR ( 50 ),
     create_user VARCHAR ( 50 ),
     create_time datetime,
     update_user VARCHAR ( 50 ),
     update_time datetime);
   
   INSERT INTO `oauth`.`sys_role` (`id`, `role_name`, `role_code`, `role_description`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES (1, 'Admin', 'admin', '系统管理员', 'system', '2022-10-12 17:04:41', 'system', '2022-10-12 17:04:46');
   
   
   CREATE TABLE sys_role_permission (
    id INT PRIMARY KEY auto_increment,
    role_id INT NOT NULL,
    permission_id INT NOT NULL);
   
   INSERT INTO `oauth`.`sys_role_permission` (`id`, `role_id`, `permission_id`) VALUES (1, 1, 1);
   
   
   CREATE TABLE sys_permission (
       id INT PRIMARY KEY auto_increment,
       pid INT,
       type INT,
       `NAME` VARCHAR( 50 ),
       `CODE` VARCHAR( 20 ),
       uri VARCHAR( 50 ),
       seq INT,
       create_user VARCHAR (50),
       create_time datetime,
       update_user VARCHAR(50),
       update_time datetime
   );
   
   INSERT INTO `oauth`.`sys_permission` (`id`, `pid`, `type`, `NAME`, `CODE`, `uri`, `seq`, `create_user`, `create_time`, `update_user`, `update_time`) VALUES (1, 1, 1, 'account', 'account', '/account/**', 1, 'system', '2022-10-12 17:06:33', 'system', '2022-10-12 17:06:40');
   
   
   
   INSERT INTO `oauth_client_details` (`client_id`, `client_secret`, `resource_ids`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('order_app', '$2a$10$LFQWUkRE4RIWNoy7Gw3GsOKgjCis/x.sKvmvO1KJ.6WRoBDgnXcnK', 'order-service', 'read', 'password', NULL, NULL, 1800, NULL, NULL, NULL);
   INSERT INTO `oauth`.`oauth_client_details` (`client_id`, `client_secret`, `resource_ids`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('portal_app', '$2a$10$LFQWUkRE4RIWNoy7Gw3GsOKgjCis/x.sKvmvO1KJ.6WRoBDgnXcnK', 'order-service,account-service', 'read', 'password,authorization_code,refresh_token', 'http://www.baidu.com', NULL, 120, 3600, NULL, '');
   INSERT INTO `oauth_client_details` (`client_id`, `client_secret`, `resource_ids`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('storage_app', '$2a$10$LFQWUkRE4RIWNoy7Gw3GsOKgjCis/x.sKvmvO1KJ.6WRoBDgnXcnK', 'storage-service', 'read', 'password', NULL, NULL, 1800, NULL, NULL, NULL);
   INSERT INTO `oauth_client_details` (`client_id`, `client_secret`, `resource_ids`, `scope`, `authorized_grant_types`, `web_server_redirect_uri`, `authorities`, `access_token_validity`, `refresh_token_validity`, `additional_information`, `autoapprove`) VALUES ('gateway_app', '$2a$10$LFQWUkRE4RIWNoy7Gw3GsOKgjCis/x.sKvmvO1KJ.6WRoBDgnXcnK', 'storage-service', 'read', 'password', NULL, NULL, 1800, NULL, NULL, NULL);
   ```

2. 接入方要自己配置一个Bean: UserDetailsService, 参考示例

   ```java
   @Slf4j
   @Service("userDetailService")
   public class JDBCUserDetailService implements UserDetailsService {
   	
   	@Autowired
   	private SysPermissionService sysPermissionService;
   	
   	@Autowired
   	private CriteriaOperations criteriaOperations;
   	
   	@Override
   	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
   		SysUser sysUser = criteriaOperations.findUniqueByProperty(SysUser.class, "username", username);
   		if (null == sysUser) {
   			log.warn("根据用户名:{}查询用户信息为空", username);
   			throw new UsernameNotFoundException(username);
   		}
   		
   		List<SysPermission> sysPermissions = sysPermissionService.findByUserId(sysUser.getId());
   		List<SimpleGrantedAuthority> authorityList = Collections.emptyList();
   		if (sysPermissions != null) {
   			authorityList = sysPermissions.stream().map((sysPermission) -> {
   				return new SimpleGrantedAuthority(sysPermission.getUri());
   			}).collect(Collectors.toList());
   		}
   		
   		OAuthUser oAuthUser = new OAuthUser(sysUser.getUsername(), sysUser.getPassword(), authorityList);
   		oAuthUser.setUserId(sysUser.getId());
   		oAuthUser.setNickname(sysUser.getNickname());
   		oAuthUser.setEmail(sysUser.getEmail());
   		log.info("用户登陆成功:{}", JacksonUtils.toJson(oAuthUser));
   		return oAuthUser;
   	}
   }
   ```

3. 用keytool生成key放到classpath root下

```shell
keytool -genkeypair -alias jwt -keyalg RSA -keysize 2048 -keystore D:/jwt.jks
```

然后把生成的jwt.jks拷贝到src/main/resources下



## Redis

如果不使用JWT Token的话, 默认使用Redis用来存储token

接入后会自动引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.3.12.RELEASE</version>
</dependency>
```

需要在application.yml中对Redis做配置, 如:

```yaml
spring:
  redis:
    host: localhost
    password: deepdata$
```



# Starter说明

## 登录失败异常处理

定义了Oauth2AuthenticationentryPoint, 在LoserOAuth2WebSecurityAutoConfig#configure(HttpSecurity http)  方法中奖这个entryPoint配置进去

```java
http.httpBasic().authenticationEntryPoint(oauth2AuthenticationentryPoint());
```

