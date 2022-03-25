package com.gmy.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {
    @Autowired
    OSS oss;

    @Test
    public void ossTest() throws FileNotFoundException {
        String filePath= "D:\\下载内容\\图片\\mv.jpg";
        InputStream inputStream = new FileInputStream(filePath);
        oss.putObject("gulimall-gmy", "data/mv.jpg", inputStream);

        oss.shutdown();
        System.out.println("上传完成");
    }

}
