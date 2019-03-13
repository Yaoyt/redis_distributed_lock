package com.qdum.jhipsterdemo.web.rest;

import com.qdum.jhipsterdemo.distributed.lock.annotation.CacheLock;
import com.qdum.jhipsterdemo.distributed.lock.annotation.CacheParam;
import com.qdum.jhipsterdemo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yaoyt on 2019-03-11.
 *
 * @author yaoyt
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @CacheLock(prefix = "books")
    @GetMapping("/query")
    public String query(@CacheParam(name = "token") @RequestParam String token) {
        return "success - " + token;
    }


    @GetMapping("/count")
    public String count() {
        bookService.count("1111111");
        return "";
    }
}
