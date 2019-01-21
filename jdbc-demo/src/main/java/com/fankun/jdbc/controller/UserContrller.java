package com.fankun.jdbc.controller;

import com.fankun.jdbc.domain.MyUser;
import com.fankun.jdbc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class UserContrller {

    private final UserRepository userRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Autowired
    public UserContrller(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/web/mvc/user/save")
    public Boolean save(@RequestBody MyUser user) throws ExecutionException, InterruptedException {
        System.out.println("web mvc =============================");
//        //手动改成异步
//       Future<Boolean> future =  executorService.submit(()->{
//            return userRepository.save(user);
//        });
//       return future.get()
        System.out.printf("[Thread: %s] UserContrller starts saving user...\n",Thread.currentThread().getName());
        return userRepository.save(user);
    }

}
