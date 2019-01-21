#1.1 REST课程

springboot版本：2.0.8

rest是一种风格、规范，不是技术

https://github.com/fankun1245067282/spring-cloud-rest-demo.git

##幂等

PUT

初始状态：0

修改状态：1*N（N次操作之后）

最终状态：1

DELETE

初始状态：1

修改状态：0*N（N次操作之后）

最终状态：0



## 非幂等

POST

初始状态：1

修改状态：1+1=2

N次修改：1+N=N+1

最终状态：N+1



幂等/非幂等依赖于服务端实现，这种方式是一种契约

post方法也可以实现幂等性，这需要服务端加以控制



##maven导入

```xml		&lt;dependency&gt; 			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt; 			&lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt; 		&lt;/dependency&gt; 		&lt;dependency&gt; 			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt; 			&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt; 		&lt;/dependency&gt;
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!--mvc包-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

若想搜索使用的类，需要导入源码

springmvc有一个@EnableWebmvc

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
```

点击：DelegatingWebMvcConfiguration 进去查看

```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
```

点进去：WebMvcConfigurationSupport

里面可以看到：

```java
public class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {

	private static final boolean romePresent =
			ClassUtils.isPresent("com.rometools.rome.feed.WireFeed",
					WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean jaxb2Present =
			ClassUtils.isPresent("javax.xml.bind.Binder",
					WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean jackson2Present =
			ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper",
					WebMvcConfigurationSupport.class.getClassLoader()) &&
			ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator",
					WebMvcConfigurationSupport.class.getClassLoader());

	private static final boolean jackson2XmlPresent =
			ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper",
					WebMvcConfigurationSupport.class.getClassLoader());
......略，还有其他类型
```

选择 `jackson2Present` 里面的字符串，`com.fasterxml.jackson.databind.ObjectMapper` 点击shift,shift搜索类是存在，搜索`jackson2XmlPresent`对应的字符串的类是不存在

它们都是`boolean`类型的，表示这些字符串对应的类是否存在，如果存在才会支持对应的`mediaType`,即是才会对应的`contentType`或者`accept`

WebMvcConfigurationSupport

```java
	protected Map<String, MediaType> getDefaultMediaTypes() {
		Map<String, MediaType> map = new HashMap<>(4);
		if (romePresent) {
			map.put("atom", MediaType.APPLICATION_ATOM_XML);
			map.put("rss", MediaType.APPLICATION_RSS_XML);
		}
		if (jaxb2Present || jackson2XmlPresent) {
			map.put("xml", MediaType.APPLICATION_XML);
		}
		if (jackson2Present || gsonPresent || jsonbPresent) {
			map.put("json", MediaType.APPLICATION_JSON);
		}
		if (jackson2SmilePresent) {
			map.put("smile", MediaType.valueOf("application/x-jackson-smile"));
		}
		if (jackson2CborPresent) {
			map.put("cbor", MediaType.valueOf("application/cbor"));
		}
		return map;
	}

```

可以获取默认支持的`mediaType`,就是对应的`contentType`,

`contentType`表示请求或者响应的数据类型；

`accept`表示请求时可以接受的返回数据类型；

示例：

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8

q=0.8是权重的意思，权重高的优先返回



java代码示例:

```java
public class Person {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
```

```java
@RestController
public class PersonRestController {

    //测试URL:http://localhost:8080/person/1?name=%E6%A8%8A%E5%9D%A4
    @GetMapping("/person/{id}")
    public Person person(@PathVariable Long id, @RequestParam(required = false) String name){
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        return person;
    }
}
```

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestOnSpringWebmvc {
    public static void main(String[] args) {
        SpringApplication.run(RestOnSpringWebmvc.class,args);
    }
}
```

使用url:http://localhost:8080/person/3?name=樊坤   进行测试，返回的是：

```json
{
    "id": 3,
    "name": "樊坤"
}
```

为什么返回的是json格式，不是其他类型的格式，例如：xml格式？？？？

因为：`jackson2Present` 是true,对应的类`com.fasterxml.jackson.databind.ObjectMapper`存在

`jackson2XmlPresent`是false,对应的类`com.fasterxml.jackson.dataformat.xml.XmlMapper`不存在

现在我们导入`com.fasterxml.jackson.dataformat.xml.XmlMapper`的包，在maven仓库中搜索

```xml
<!-- https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

再进行测试：http://localhost:8080/person/3?name=樊坤 

```xml
<Person>
    <id>3</id>
    <name>樊坤</name>
</Person>
```

为什么这次返回xml格式的了？？？



##自描述信息

Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8

q=0.8是权重的意思，权重高的优先返回

这是客户端要求返回的吧？？

第一优先顺序： text/html-->application/xhtml+xml-->application/xml

第二优先顺序：image/webp-->image/apng



使用postman测试，返回结果还是json格式；

可以在postman中指定请求头，headers[Accept:application/xml]

这样就可以了



##查看源代码

@EnableWebmvc-->

​	DelegatingWebMvcConfiguration-->

​		WebMvcConfigurationSupport.addDefaultHttpMessageConverters

```java
protected final void addDefaultHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316

		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringHttpMessageConverter);
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new ResourceRegionHttpMessageConverter());
		messageConverters.add(new SourceHttpMessageConverter<>());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());

		if (romePresent) {
			messageConverters.add(new AtomFeedHttpMessageConverter());
			messageConverters.add(new RssChannelHttpMessageConverter());
		}

		if (jackson2XmlPresent) {
			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.xml();
			if (this.applicationContext != null) {
				builder.applicationContext(this.applicationContext);
			}
			messageConverters.add(new MappingJackson2XmlHttpMessageConverter(builder.build()));
		}
		else if (jaxb2Present) {
			messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		}

		if (jackson2Present) {
			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
			if (this.applicationContext != null) {
				builder.applicationContext(this.applicationContext);
			}
			messageConverters.add(new MappingJackson2HttpMessageConverter(builder.build()));
		}
		else if (gsonPresent) {
			messageConverters.add(new GsonHttpMessageConverter());
		}
		else if (jsonbPresent) {
			messageConverters.add(new JsonbHttpMessageConverter());
		}

		if (jackson2SmilePresent) {
			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.smile();
			if (this.applicationContext != null) {
				builder.applicationContext(this.applicationContext);
			}
			messageConverters.add(new MappingJackson2SmileHttpMessageConverter(builder.build()));
		}
		if (jackson2CborPresent) {
			Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.cbor();
			if (this.applicationContext != null) {
				builder.applicationContext(this.applicationContext);
			}
			messageConverters.add(new MappingJackson2CborHttpMessageConverter(builder.build()));
		}
	}
```

返回的是一个List，是按顺序服务的

`jackson2XmlPresent`在j`ackson2Present`之前

查看所有方法的快捷键：ctrl+F12

为什么什么都不写，返回的是json，客户端accept中没有json类型呀？？

WebMvcConfigurationSupport.getMessageConverters，是addDefaultHttpMessageConverters使用的地方

所有的Http自描述信息都在messageConverters中，这个集合会传递到RequestMappingHandlerAdapter，最终控制写出。

以application/json为例，Spring Boot中默认使用jackson2序列化方式

媒体类型，application/json，它的处理类是：

```
org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
提供两种方法：
1、read*,通过http请求内容转化成对应的Bean
2、write*,通过序列化成对应内容作为响应内容
```

```java
protected final List<HttpMessageConverter<?>> getMessageConverters() {
    if (this.messageConverters == null) {
        this.messageConverters = new ArrayList<>();
        configureMessageConverters(this.messageConverters);//这个是配置的
        if (this.messageConverters.isEmpty()) {
            addDefaultHttpMessageConverters(this.messageConverters);
        }
        extendMessageConverters(this.messageConverters);//这个是扩展的，自己写的，本类中的方法
    }
    return this.messageConverters;
}
```

WebMvcConfigurationSupport.requestMappingHandlerAdapter,调用

```java
@Bean
public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
    RequestMappingHandlerAdapter adapter = createRequestMappingHandlerAdapter();
    adapter.setContentNegotiationManager(mvcContentNegotiationManager());/////搜索ContentNegotiationManager这个类
    adapter.setMessageConverters(getMessageConverters());
    adapter.setWebBindingInitializer(getConfigurableWebBindingInitializer());
    adapter.setCustomArgumentResolvers(getArgumentResolvers());
    adapter.setCustomReturnValueHandlers(getReturnValueHandlers());

    if (jackson2Present) {
        adapter.setRequestBodyAdvice(Collections.singletonList(new JsonViewRequestBodyAdvice()));
        adapter.setResponseBodyAdvice(Collections.singletonList(new JsonViewResponseBodyAdvice()));
    }

    AsyncSupportConfigurer configurer = new AsyncSupportConfigurer();
    configureAsyncSupport(configurer);
    if (configurer.getTaskExecutor() != null) {
        adapter.setTaskExecutor(configurer.getTaskExecutor());
    }
    if (configurer.getTimeout() != null) {
        adapter.setAsyncRequestTimeout(configurer.getTimeout());
    }
    adapter.setCallableInterceptors(configurer.getCallableInterceptors());
    adapter.setDeferredResultInterceptors(configurer.getDeferredResultInterceptors());

    return adapter;
}
```

打开这个类：

org.springframework.web.accept.ContentNegotiationManager

```java
public class ContentNegotiationManager implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver {

	private final List<ContentNegotiationStrategy> strategies = new ArrayList<>();

	private final Set<MediaTypeFileExtensionResolver> resolvers = new LinkedHashSet<>();
......略。。。
```

```java
@FunctionalInterface
public interface ContentNegotiationStrategy {//支持所有类型，MediaType.ALL

	/**
	 * A singleton list with {@link MediaType#ALL} that is returned from
	 * {@link #resolveMediaTypes} when no specific media types are requested.
	 * @since 5.0.5
	 */
	List<MediaType> MEDIA_TYPE_ALL_LIST = Collections.singletonList(MediaType.ALL);
```

```java
@Override
public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
    for (ContentNegotiationStrategy strategy : this.strategies) {
        List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);
        if (mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
            continue;
        }
        return mediaTypes;
    }
    return MEDIA_TYPE_ALL_LIST;
}
```

ContentNegotiationManager.resolveMediaTypes 解析请求类型

问题：为什么第一次是json，增加了xml 依赖之后，就变成了xml了

答：spring boot默认没有增加xml处理实现，所以最后采用轮询的方式逐一尝试是否canWrite(Pojo),

如果返回ture,说明可以序列化该Pojo对象，那么Jackson2恰好能处理，那么Jackson输出了。



问题：当accept请求头未必指定时，为什么还是JSON来处理？

答：这依赖于messageConverters的插入顺序。



问题：messageConverters里面的顺序可以修改吗？

答：可以修改，怎么改：

```java
@Configuration
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {

	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();


	@Autowired(required = false)//这个是自己配置的mediaType,不是必须的 。。。实现WebMvcConfigurer
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addWebMvcConfigurers(configurers);
		}
	}
 ......略。。。
```

```java
protected final List<HttpMessageConverter<?>> getMessageConverters() {
    if (this.messageConverters == null) {
        this.messageConverters = new ArrayList<>();
        configureMessageConverters(this.messageConverters);//添加自己想要的mediaType,如果不为空，就不添加默认的了。。。。。。
        if (this.messageConverters.isEmpty()) {
            addDefaultHttpMessageConverters(this.messageConverters);
        }
        extendMessageConverters(this.messageConverters);
    }
    return this.messageConverters;
}
```

WebMvcConfigurationSupport.getMessageConverters

## 自己添加mediaType

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {



    /**
     * 添加自己的mediaType
     * @param converters
     */
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        System.out.println("converters不为空，所有的默认的都已经加载进来了:"+converters);
//        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
//        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        System.out.println("");
//    }

    /**
     * 添加自己的mediaType,把xml默认添加到第一个
     * @param converters
     */
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        System.out.println("converters不为空，所有的默认的都已经加载进来了:"+converters);
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        converters.add(0,new MappingJackson2XmlHttpMessageConverter());
        converters.add(0,new MappingJackson2HttpMessageConverter(builder.build()));
        System.out.println("");
    }
}

```

这样可以修改默认的顺序，自己把默认的重新清空，按照自己的顺序添加

##自定义mediaType

自己添加扩展一个Properties格式，待扩展，之前的返回结果：

XML格式：（Accept ：application/xml ）

```xml
<Person>
    <id>3</id>
    <name>樊坤</name>
</Person>
```

JSON格式：（Accept ：application/json）

```json
{
    "id": 3,
    "name": "樊坤"
}
```

Properties格式：（Accept ：application/properties+person）（需要扩展）

```properties
person.id=3
person.name=樊坤
```

实现参考：MappingJackson2XmlHttpMessageConverter

继承：org.springframework.http.converter.AbstractHttpMessageConverter



```java
import Person;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Person Properties自描述消息处理
 */
public class PerpertiesPersonHttpMessageConverter extends AbstractHttpMessageConverter <Person> {

    /**
     * 添加所支持的类型的参数。。。
     */
    public PerpertiesPersonHttpMessageConverter() {
        super(MediaType.valueOf("application/properties+person"));
        setDefaultCharset(Charset.forName("UTF-8"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        //必须是Person的子类才支持
        return clazz.isAssignableFrom(Person.class);
    }

    /**
     * 读，把properties类型的请求内容转化为Bean（Person）类型
     * @param clazz
     * @param inputMessage
     * @return
     * @throws IOException
     * @throws HttpMessageNotReadableException
     */
    @Override
    protected Person readInternal(Class<? extends Person> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream inputStream = inputMessage.getBody();
        /**
         * person.id=3
         * person.name=樊坤
         */
        Properties properties = new Properties();
        //将请求中的内容转换为Properties
        properties.load(new InputStreamReader(inputStream,getDefaultCharset()));
        //将Properties内容转为Person对象中
        Person person = new Person();
        person.setId(Long.valueOf(properties.getProperty("person.id")));
        person.setName(properties.getProperty("person.name"));
        return person;
    }
    /**
     * ，把properties类型的请求内容转化为Bean（Person）类型
     * @param clazz
     * @param inputMessage
     * @return
     * @throws IOException
     * @throws HttpMessageNotReadableException
     */
    /**
     * 写，把Bean（Person）类型返回内容转化为properties类型的
     * @param person
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException
     */
    @Override
    protected void writeInternal(Person person, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream outputStream = outputMessage.getBody();
        Properties properties = new Properties();
        properties.setProperty("person.id",String.valueOf(person.getId()));
        properties.setProperty("person.name",person.getName());

        properties.store(new OutputStreamWriter(outputStream,getDefaultCharset()),"written by web server");
    }
}
```

supports方法，是否支持pojo类型

readInternal方法，读取http请求，转换为pojo对象

writeInternal方法，将pojo对象序列化为文本内容（properties内容）



添加到配置中：

```java
import PerpertiesPersonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加自己的mediaType
     * @param converters
     */
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        System.out.println("converters不为空，所有的默认的都已经加载进来了:"+converters);
        converters.add(new PerpertiesPersonHttpMessageConverter());
        System.out.println("");
    }
}

```

```java

```





使用postman测试

###测试1

url:localhost:8080//person/json/to/properties 

headers[{"key":"Content-Type","value":"application/json;charset=UTF-8"}]

headers[{"key":"Accept","value":"application/properties+person","description":"","enabled":true}]

type:post

params:

```json

{

    "id": 3,

    "name": "樊坤"

}result:
```

```properties
#written by web server
#Thu Jan 17 15:53:17 CST 2019
person.name=樊坤
person.id=3
```

###测试2

url:localhost:8080/person/properties/to/json 

headers[{"key":"Content-Type","value":"application/properties+person "}]

headers[{"key":"Accept","value":"application/json;charset=UTF-8","description":"","enabled":true}]

type:post
params:

```properties
person.name=樊坤
person.id=3
```

result:

```json
{
    "id": 3,
    "name": "樊坤"
}
```

RequestMapping中:

consumers-->content-type

producers-->accept



HttpMessageConverter执行逻辑

*读操作，尝试是否能读取，canRead方法去尝试，如果返回true,下一步执行read

*写操作，尝试是否能写取，canWrite方法去尝试，如果返回true,下一步执行write



# 1.2 WEBMVC课程(含flux)

j2ee 核心模式

mvc

m : model

v : view

c : contoller-->dispatchServlet

Font Controller  --> DispatchServlet

Application Controller --> @Controller



ServletContextListener --> ContextLoaderListener--> Root WebApplicaitonContext(根)

DispatcherServlet--> Servlet WebApplicationContext（上层）

Services=>@Services

Repositories=>@Repositories



##映射处理

###Servlet请求映射

--Servlet URL Pattern

--Filter URL Pattern



### Spring Web MVC

--DispatcherServlet

--HandlerMapping



用eclipse建立dynamic web project

新建一个servlet3.0的servlet，只是简单测试



## 代码模块，在spring-cloud-rest-demo中

###文件夹：com.fankun.webmvc

DispatcherServlet <-- FrameworkServlet <-- HttpServletBean <-- HttpServlet

映射关系：

org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration

ServletContext Path = "" 或者 ”/"

Request URI = ServletContext Path + RequestMapping("")/@getMapping("")/@PostMapping("")



Spring  Web MVC的配置Bean : WebMvcProperties

Spring Boot 允许通过 application.properties去定义配置，配置外部化

```java
@ConfigurationProperties(prefix = "spring.mvc") //配置前缀
public class WebMvcProperties {
```

WebMvcProperties的前缀：spring.mvc

RestDemoController#index()

HandlerMapping,寻找Request URI匹配的Handler

Handler是处理的方法，当然这是一种实例

Request-->Handler-->执行结果-->返回（REST)-->普通的文本  (和Spring MVC源码讲解的差不多)

HandlerMapping-->RequestMappingHandlerMapping(@RequestMapping Handler Mapping)

@RequestMapping默认处理@GetMapping请求



@PostMapping==@RequestMapping(method=RequestMethod.POST)  Create(C)

@GetMapping==@RequestMapping(method=RequestMethod.GET)     Read(R)

@PutMapping==@RequestMapping(method=RequestMethod.PUT)    Update(U)

@DeleteMapping==@RequestMapping(method=RequestMethod.DELETE)  Delete(D)

CRUD

windows通过postman测试

linux:  curl -X POST

拦截器：HandlerInterceptor

```java
package com.fankun.webmvc.controller;

import org.springframework.web.bind.annotation.*;

@RestController//==@Controller+@ResponseBody
public class RestDemoController {
    //测试URL:http://localhost:8080/hello
    @GetMapping("/hello")
    public String index(){
        return "hello,world";
    }
}
```

```java
package com.fankun.webmvc.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultHandlerInterceptor implements HandlerInterceptor{

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println(handler.toString());
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) throws Exception {
    }

}
```

上面为自定义handlerInterceptor

## 添加自定义Handler

```java
@SpringBootApplication
public class RestOnSpringWebmvc extends WebMvcConfigurerAdapter{

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefaultHandlerInterceptor());
    }

    public static void main(String[] args) {
        SpringApplication.run(RestOnSpringWebmvc.class,args);
    }
}
```

### HandlerInteceptor处理逻辑

处理顺序：preHandle(true)-->Handler:[HandlerMethod执行(Method#invoke)]-->postHandle(true)-->afterCompletion

Handler不一定是HandlerMethod，所以在拦截器中是Object类型



## 异常处理

### Servlet标准

####返回参数

Request Attributes                                       Type

javax.servlet.error.status_code                 java.lang.Integer

javax.servlet.error.exception_type           java.lang.Class

javax.servlet.error.message                      java.lang.String

javax.servlet.error.exception                    java.lang.Throwable

javax.servlet.error.request_uri                 java.lang.String

javax.servlet.error.servlet_name              java.lang.String



####理解web.xml错误页面

<error-page>处理逻辑:

处理状态码：<error-code>

处理异常类型：<exception-type>

处理服务：<location>

优点：业界的标准，统一处理

缺点：灵活度不够，只能定义在web.xml文件里面

```xml
<error-page>
    <error-code>404</error-code>
    <location>/404.html</location>
</error-page>
<servlet>
    <description></description>
    <display-name>PageNotFoundServlet</display-name>
    <servlet-name>PageNotFoundServlet</servlet-name>
    <servlet-class>com.fankun.PageNotFoundServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>PageNotFoundServlet</servlet-name>
    <url-pattern>/404.html</url-pattern>
</servlet-mapping>
```

找不到页面会跳转到url为：/404.html

然后找到PageNotFoundServlet进行处理

```java

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageNotFoundServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public PageNotFoundServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter writer =   response.getWriter();
		writer.write("页面没有找到");
		writer.close();
		System.out.println("=============================");
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
```



### Spring Web MVC

第一， 需要注意Spring MVC 和 Spring Rest两种情况下的区别。

Spring MVC是可以通过增加/error的handler来处理异常的，

而REST却不行，因为在spring Rest中， 当用户访问了一个不存在的链接时, Spring 默认会将页面重定向到 **/error** 上, 而不会抛出异常。

处理方法是，在application.properties文件中， 增加下面两项设置

```
spring.mvc.throw-exception-if-no-handler-found=true
spring.resources.add-mappings=false
```

添加处理器： @RestControllerAdvice==@ControllerAdvice+@ResponseBody

basePackages属性指定扫描的路径，否则是指当前路径下

```
@ControllerAdvice(basePackages = "com.fankun.webmvc.controller")
```

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestControllerAdvicer {

    @ExceptionHandler(NullPointerException.class)//要处理的异常
    @ResponseBody
    public Object npe(HttpServletRequest request, Throwable throwable){
        return throwable.getMessage();
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})//要处理的异常404
    @ResponseBody
    public Object pageNotPage(HttpServletRequest request, Throwable throwable){
        Map<String, Object> errors = new HashMap<>();
        //没有获取到信息，好像request的信息都被删除了
        errors.put("status_code",request.getAttribute("javax.servlet.error.status_code"));
        errors.put("request_uri",request.getAttribute("javax.servlet.error.request_uri"));
//        javax.servlet.error.status_code                 java.lang.Integer
//        javax.servlet.error.exception_type           java.lang.Class
//        javax.servlet.error.message                      java.lang.String
//        javax.servlet.error.exception                    java.lang.Throwable
//        javax.servlet.error.request_uri                 java.lang.String
//        javax.servlet.error.servlet_name              java.lang.String
        return errors;
    }
}
```

@ExceptionHandler(NullPointerException.class)//要处理的异常
	缺点：很难完全掌握掌握所有的异常类型
	优点：易于理解，尤其是全局异常处理



### Spring Boot 错误页面处理

实现：org.springframework.boot.web.server.ErrorPageRegistrar

​	缺点：页面处理的路径必须固定

​	优点：比较通用，不需要理解Spring MVC的异常体系

注册：Error Page对象

实现：ErrorPage对象中Path路径WEB服务

```java
@SpringBootApplication
public class RestOnSpringWebmvc extends WebMvcConfigurerAdapter implements
        ErrorPageRegistrar{

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefaultHandlerInterceptor());
    }

    public static void main(String[] args) {
        SpringApplication.run(RestOnSpringWebmvc.class,args);
    }
    /**
     * spring boot 异常处理器
     * @param registry
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"/404.html"));
    }
}
```

处理服务：

```java
import org.springframework.web.bind.annotation.*;

@RestController//==@Controller+@ResponseBody
public class RestDemoController {
    /**
     * 处理页面找不到的情况
     * @return
     */
    @GetMapping("/404.html")
    public Map<String, Object> handlerPageNotFond(HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();
        errors.put("status_code",request.getAttribute("javax.servlet.error.status_code"));
        errors.put("request_uri",request.getAttribute("javax.servlet.error.request_uri"));
        return errors;
    }
}
```



## 视图技术

### View

#### render方法

处理页面渲染的逻辑，Velocity,JSP,Thymeleaf



### ViewResover

View Resolver  = 页面 +解析器  -> resolveViewName 寻找合适/对应View对象

RequestURI -> RequestMappingHandlerMapping->HandleMethod -> return "viewName"->

完整的页面名称=prefix +"viewName"+suffix -> ViewResolver-> View -> render ->HTML



Spring Boot 解析完整的页面路径：

spring.view.prefix + HandlerMethod +return spring.view+suffix



org.springframework.web.servlet.view.ContentNegotiatingViewResolver

用于处理多个ViewResolver: JSP，Velocity,  thymeleaf

最佳匹配原则

当所有的ViewResover配置完成是时，他们的order默认值是一样的，所有先来先服务（list）

当他们定义自己的order,通过order来倒序排列





### Thymeleaf

#### 自动装配类：ThymeleafAutoConfiguration(jar没有引入，打开里面飘红)

maven导入

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

```java
@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
@ConditionalOnClass(TemplateMode.class)
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class })
public class ThymeleafAutoConfiguration {
    ......略。。。
```

打开ThymeleafProperties.class

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {
	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";

	/**
	 * Whether to check that the template exists before rendering it.
	 */
	private boolean checkTemplate = true;

	/**
	 * Whether to check that the templates location exists.
	 */
	private boolean checkTemplateLocation = true;

	/**
	 * Prefix that gets prepended to view names when building a URL.
	 */
	private String prefix = DEFAULT_PREFIX;

	/**
	 * Suffix that gets appended to view names when building a URL.
	 */
	private String suffix = DEFAULT_SUFFIX;
    ......略。。。
```

配置前缀：spring.thymeleaf

模板寻找前缀：spring.thymeleaf.prefix   [classpath:/templates/]

模板寻找后缀：spring.thymeleaf.suffix   [.html]

添加配置：

```properties
spring.thymeleaf.prefix=classpath:/thymeleaf/
spring.thymeleaf.suffix=.htm
```

Controller

```java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThymeleafController {

    @RequestMapping("/thymeleaf/index.do")
    public String index(){
        return "index";
    }
}

```

prefix:/thymeleaf/

suffix:.htm

return value:index

完整页面:  /thymeleaf/index.htm

测试url:     http://localhost:8080/thymeleaf/index.do

ViewResolver,查看它的实现类

org.thymeleaf.spring5.view.ThymeleafViewResolver

父类：org.springframework.web.servlet.view.AbstractCachingViewResolver

中的resolveViewName方法



## 国际化（i18n）

java.util.Locale

MessageSourceAutoConfiguration

```java
@Configuration
@ConditionalOnMissingBean(value = MessageSource.class, search = SearchStrategy.CURRENT)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Conditional(ResourceBundleCondition.class)
@EnableConfigurationProperties
public class MessageSourceAutoConfiguration {

	private static final Resource[] NO_RESOURCES = {};

	@Bean
	@ConfigurationProperties(prefix = "spring.messages")
	public MessageSourceProperties messageSourceProperties() {
		return new MessageSourceProperties();
	}
......略。。。
```

国际化配置：

```
spring.messages.basename=META-INF/locale/messages
```

创建文件夹：resources/META-INF/locale

在文件夹中新建:

messages_zh_CN.properties

messages.properties

两个都要建

在html中使用和替换文本：

```html
<!--使用国际化参数 home.welcome-->
<p th:text="#{home.welcome}">这是我的第一个thymeleaf测试</p>
```

在messages_zh_CN.properties中添加响应的配置：

```properties
home.welcome=Hello,World, ABC
```

好像默认使用messages_zh_CN.properties中的配置，不是messages.properties中的配置

%JAVA_HOME%/bin中 

中文转Unicode：native2ascii -encoding UTF-8 D:/abc.txt D:/abd.txt 

//GB2312、 GBK 也可以

Unicode转中文：native2ascii -reverse -encoding  UTF-8 D:/abd.txt D:/abc.txt



LocaleContext

LocaleContextHolder

LocaleResolver/LocaleContextResolver

```
AcceptHeaderLocaleResolver 实现 LocaleResolver
```

使用postman测试，headers:[{"key":"Accept-Language","value":"en","description":"","enabled":true}]

结果还是英文？？？



# 1.3 Spring Boot Jdbc（含webflux）

## 数据源（DataSource）

maven 导入

```xml
<!--jdbc-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<!--reactive web-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<!--mysql driver-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

spring-boot-starter-jdbc 如果没有配置数据源，启动会报错，如果没有配置数据源，就只能把这个maven导入注释掉，再启动

###类型

​	通用型数据源

​	javax.sql.DataSource

​	分布式数据源

​	javax.sql.XADataSource

​	嵌入式数据源

​	org.springframework.datasource.embedded.EmbeddedDatabase

### Spring Boot JDBC 场景演示

​	单数据源场景

javax.sql.DataSource

### 数据库连接池技术

####Apache  Commons DBCP

commons-dbcp 依赖 commons-pool(老版本)

commons-dbcp2 依赖 commons-pool2

​	多数据源场景

####[Tomcat DBCP](http://tomcat.apache.org/tomcat-8.5-doc/jndi-datasource-examples-howto.html)



```sql
CREATE TABLE myuser (
	id BIGINT NOT NULL PRIMARY KEY auto_increment,
	NAME VARCHAR (32) NOT NULL
);
```

```properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=123456
```





## 事务(Transaction)

### 重要概念

#### 自动提交模式

#### 事务隔离级别（Transaction isolation levels）

javax.sql.Connection

```java
int TRANSACTION_READ_UNCOMMITTED = 1;
int TRANSACTION_READ_COMMITTED   = 2;
int TRANSACTION_REPEATABLE_READ  = 4;
int TRANSACTION_SERIALIZABLE     = 8;
```

事务的隔离级别

从上往下，级别越高，性能越差

Spring Transaction实现重用了jdbc api

org.springframework.transaction.annotation.Isolation-->TransactionDefinition

```java
int ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
int ISOLATION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;
int ISOLATION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;
int ISOLATION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;
```



#### 代理执行 TransactionInterceptor

@Transactional注解的方法，被代理了，要是想查看代理之后的样子，需要在方法调用的地方进行Dubugger跟踪！

TransactionAspectSupport

@Transactional

*可以控制rollback的异常粒度：rollbackFor()以及noRollbackFor()*

*可以执行事务管理器：transactionManager()*

#### API实现方式

org.springframework.transaction.PlatformTransactionManager



@Transactional在事务传播

@Transactional

save(){

​	//insert into DS1

​	save2()//insert into DS2;save2()没有@Transactional

}

单独调用save2()是没有事务的，但是save()是有事务的，一起的事务



#### 保护点（Savepoints）

save(){

​	//新建一个安全点SP1

​	SP1

​	SP2{

​		//操作

​	}catch(){

​		rollback(SP2)

​	}

​	commit();

​	release(SP1)

}



小马哥的github:

https://github.com/mercyblitz/jsr





##Spring Boot 实际使用场景

Spring Boot 2.0容器使用

spring webflux认启动容器是netty（嵌入式）

Spring webmvc的默认启动容器是tomcat（嵌入式）



Spring Boot 1.4 开始，错误分析接口

org.springframework.boot.diagnostics.FailureAnalysisReporter

数据源初始化

org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration



WebFlux

Mono:0-1 个 Publisher  (类似于java8中的Optional)

Flux:0-N 个 Publisher  (类似于java中的List)



传统的Servlet采用的是HttpServletRequest,HttpServletResponse

WebFlux采用ServerRequest,ServerResponse(不再限制于Servlet容器，可以选择自定义实现，比如Netty Web Server)







## 查找异常调用链

1、找到异常输出的字符串

2、把字符串在project and libraries中查找（ctrl+shift+f)

3、找到异常方法的方法，字段；然后查看在那里使用，右击，Find Usages,看看在哪个方法中调用

4、对找到的方法进行断点，执行到断点处，查看方法栈，看看是否在Configuration中就报错了或者其他情况



##问题集合

问题：用reactive web,原来mvc的好多东西都不能用了？

答：不是，Reactive Web还是能兼容Spring mvc的。



问题：开个线程池事务控制用api方式？

答：TransactionSynchronizationManager，使用大量的ThreadLocal来实现的。



问题：spring boot中分布式事务有几种方式？

https://docs.spring.io/spring-boot/docs/2.0.8.RELEASE/reference/htmlsingle/#boot-features-jta



# 1.4 Spring Boot 初体验

## 基本概念

应用分为两个方面：功能性、非功能性

功能性：系统所设计的业务范畴

非功能性：安全、性能、监控、数据指标（CPU使用率、网卡使用率）

Spring Boot 规约大于配置，大多数组件，不需要自行配置，而是自动组装！简化开发，大多数情况，使用默认即可！

production-ready就是非功能性范畴

opinionated 固化的



独立Spring应用，不需要依赖，依赖容器（tomcat）

嵌入式Tomcat Jetty

外部配置：启动参数、配置文件、环境变量

Profiles

Logging

外部应用：Servlet应用、Spring Web MVC、Spring Web Flux、WebSockets、Web Service

SQL: JDBC、JPA、ORM

NoSql(Not Only SQL): Redis、ElasticSearch、Hive、Hbase



## Spring Boot 创建方式

图形化方式：http://start.spring.io

命令行方式：maven

mvn archetype:generate  -DgroupId=com.fankun -DartifactId=first-sping-boot-app -Dversion=1.0.0-SNAPSHOT -DinteractiveMode=false

创建之后，idea可以import project导入！

interactiveMode 交互模式



#### spring-webflux 目前不能和 spring-webmvc同时使用



