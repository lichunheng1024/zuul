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

package com.netflix.zuul;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import com.netflix.zuul.monitoring.MonitoringHelper;
import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务启动时的入口类 实现了 ServletContextListener 接口。
 *
 *
 *
 *  ServletContextListener 继承 EventListener ,开发者能够在为客户端请求提供服务之前向ServletContext中添加任意的对象。
 *  这个对象在ServletContext启动的时候被初始化，然后在ServletContext 整个运行期间都是可见的。
 *
 *  该接口拥有两个方法：
 *          contextDestroyed(ServletContextEvent sce)
 *          contextInitialized(ServletContextEvent sce)
 *
 *  用户需要创建一个java类实现该接口，并提供上述方法的实现。
 *
 *
 *  // 当你需要在处理任何客户端请求之前创建一个数据库连接，
 *  // 并且希望在整个应用过程中该连接都是可用的，这个时候ServletContextListener接口就会十分有用了。
 *
 *  public class DatabaseContextListener implements ServletContextListener {
 *
 *          private ServletContext context = null;
 *          private Connection conn = null;
 *
 *          public DatabaseContextListener() {
 *
 *          }
 *
 *
 *          //该方法在ServletContext启动之后被调用，并准备好处理客户端请求
 *          public void contextInitialized(ServletContextEvent event)  {
 *                  this.context = event.getServletContext();
 *                  conn = DbConnection.getConnection;
 *                  // 这里DbConnection是一个定制好的类用以创建一个数据库连接
 *                  context = setAttribute(”dbConn”,conn);
 *          }
 *
 *          //这个方法在ServletContext 将要关闭的时候调用
 *          public void contextDestroyed(ServletContextEvent event){
 *                  this.context = null;
 *                  this.conn = null;
 *          }
 *  }
 * //并在 web.xml 中增加以下配置
 *      <listener>
 *          com.database.DatabaseContextListener
 *      </listener>
 *
 * @author  xxx
 */
public class StartServer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(StartServer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("starting server");

        // mocks monitoring infrastructure as we don't need it for this simple app
        MonitoringHelper.initMocks();

        // initializes groovy filesystem poller
        initGroovyFilterManager();

        // initializes a few java filter examples
        initJavaFilters();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("stopping server");
    }

    private void initGroovyFilterManager() {
        FilterLoader.getInstance().setCompiler(new GroovyCompiler());

        /**
            在 build 编译时指定了：System.setProperty("zuul.filter.root", "src/main/groovy/filters")
         */
        String scriptRoot = System.getProperty("zuul.filter.root", "");
        if (scriptRoot.length() > 0) scriptRoot = scriptRoot + File.separator;
        try {
            FilterFileManager.setFilenameFilter(new GroovyFileFilter());
            FilterFileManager.init(5, scriptRoot + "pre", scriptRoot + "route", scriptRoot + "post");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initJavaFilters() {
        final FilterRegistry r = FilterRegistry.instance();

        r.put("javaPreFilter", new ZuulFilter() {
            @Override
            public int filterOrder() {
                return 50000;
            }

            @Override
            public String filterType() {
                return "pre";
            }

            @Override
            public boolean shouldFilter() {
                return true;
            }

            @Override
            public Object run() {
                logger.debug("running javaPreFilter");
                RequestContext.getCurrentContext().set("javaPreFilter-ran", true);
                return null;
            }
        });

        r.put("javaPostFilter", new ZuulFilter() {
            @Override
            public int filterOrder() {
                return 50000;
            }

            @Override
            public String filterType() {
                return "post";
            }

            @Override
            public boolean shouldFilter() {
                return true;
            }

            @Override
            public Object run() {
                logger.debug("running javaPostFilter");
                RequestContext.getCurrentContext().set("javaPostFilter-ran", true);
                return null;
            }
        });
    }

}
