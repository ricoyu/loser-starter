1. 自动注册 LoserRestTemplate, 支持在服务启动阶段从注册中心获取服务地址进行调用的版本, beanName是loserRestTemplate
2. 自动注册 RestTemplate, beanName是restTemplate

一些功能类还是不要写到starter里面为好, 因为引入这些依赖的时候, 有时候可能不想自动配置, 可以按需配置Bean, 更灵活一点