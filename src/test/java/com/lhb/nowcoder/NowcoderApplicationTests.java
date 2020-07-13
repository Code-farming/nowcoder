package com.lhb.nowcoder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootTest
class NowcoderApplicationTests {


    @Resource
    DataSource dataSource;

    @Test
    void contextLoads() {
        System.out.println(dataSource.getClass());
    }

    @Test
    void compareInt(){
        int a = -128;
        int i = -128;
        int y = 128;
        int z = 128;
        Integer aa =-129;
        Integer ii =-129;
        Integer yy =128;
        Integer zz =128;
        System.out.println(a==i);
        System.out.println(y==z);

        System.out.println(aa == ii);
        System.out.println(yy == zz);

    }


}
