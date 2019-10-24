/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */
package filters.pre

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

/**
 * @author mhawthorne
 */
class PreDecorationFilter extends ZuulFilter {

    @Override
    int filterOrder() {
        return 5
    }

    @Override
    String filterType() {
        return "pre"
    }

    @Override
    boolean shouldFilter() {
        return true;
    }

    @Override
    Object run() {
        RequestContext ctx = RequestContext.getCurrentContext()
        /**
         *  此处就是做的事情是，找域名，
         *  将当前的请求转发到哪个域名，
         *  将外部的请求转发到另外一个域名
         *
         *
         *  对于使用dubbo 或者 http的后端团队，此处就需要做一些扩展了。
         *
         *  根据API需要转换的后端协议来选择，
         *      1. 如果使用dubbo,则需要通过dubbo的Generic Invoke 来实现无jar依赖调用
         *         参考文档说明： http://dubbo.apache.org/zh-cn/blog/dubbo-generic-invoke.html
         *
         *      2. 如果使用的http,则通过如zuul中使用的Apache HttpClient 或者 OKHttp来发起http调用
         *
         *      假如前端用户发起的请求是：http://api.lch.com/userService/findUserInfo?userId=1024&timestamp=13933822945754
         *
         *      说明： " api.lch.com " 是网关对外暴露的域名。
         *            " /userService/findUserInfo " 是资源uri ,可以理解为 前端应用通过这个uri 调用到具体的某个后端服务，该服务根据入参的不同，执行具体的findUserInfo逻辑。
         *
         *             网关服务端维护一个映射关系 Map<String,ApiMetadata> routeMap=new HashMap<String,ApiMetadata>
         *                 其中
         *                      key: 资源uri
         *                      value: api元数据对象 (appName、uri、protocol、http相关metadata、rpc相关metadata )
         *
         *                  通过一堆zuul常见Filter过滤规则，或者根据实际情况自定义规则。
         *                  routeMap通过 推(主) + 拉(辅) 确保配置及时更新生效。
         *
         *
         *
         *
         *
         *
         *                                                  -- http -->  【后端http服务】
         *              ----------        -----------  /
         *              | 用户请求 |  ->   | 网关服务  |/\
         *              ----------        -----------   \
         *                                                  -- rpc --->  【后端rpc服务】
         *
         *
         *
         *
         *
         */
        // sets origin
        ctx.setRouteHost(new URL("http://ferry.jd.com"));

        // sets custom header to send to the origin
        ctx.addOriginResponseHeader("cache-control", "max-age=3600");
    }

}
