package com.fankun.http.message;

import com.fankun.domain.Person;
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
