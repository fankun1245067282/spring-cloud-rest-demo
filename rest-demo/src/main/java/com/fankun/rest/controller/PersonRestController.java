package com.fankun.rest.controller;

import com.fankun.rest.domain.Person;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class PersonRestController {

    //测试URL:http://localhost:8080/person/1?name=%E6%A8%8A%E5%9D%A4
    @GetMapping("/person/{id}")
    public Person person(@PathVariable Long id, @RequestParam(required = false) String name){
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        System.out.println("person:"+person);
        return person;
    }


    @PostMapping(value="/person/json/to/properties",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,//请求类型 content-type
            produces = "application/properties+person")//响应类型 accept
    public Person personJsonToProperties(@RequestBody Person person){
        // @RequestBody的内容是JSON
        System.out.println("/person/json/to/properties[person]:"+person);
        //响应的内容是properties
        System.out.println("");
        return person;
    }

    @PostMapping(value="/person/properties/to/json",
            consumes = "application/properties+person", //请求类型 content-type
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE) //响应类型 accept
    public Person personPropertiesToJson(@RequestBody Person person){
        // @RequestBody的内容是Properties
        //响应的内容是JSON
        System.out.println("/person/properties/to/json[person]:"+person);
        //响应的内容是properties
        System.out.println("");
        return person;
    }

    @GetMapping(value="/person.html",produces = "application/properties+person") //响应类型
    public Person personHtml(){//.html默认是json格式，produces是返回格式
        Person person = new Person();
        person.setId((long)5);
        person.setName("樊坤");
        System.out.println("/person.html[person]:"+person);
        //响应的内容是properties
        System.out.println("");
        return person;
    }
}
