import org.springframework.aop.scope.ScopedProxyFactoryBean

beans = {
    googleServiceProxy(ScopedProxyFactoryBean) {
        targetBeanName = 'googleService'
        proxyTargetClass = true
    }
    wikipediaServiceProxy(ScopedProxyFactoryBean) {
        targetBeanName = 'wikipediaService'
        proxyTargetClass = true
    }
}