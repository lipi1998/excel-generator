package cn.iruite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description:
 * @Author: LiRuite
 * @Date: 2023/3/15 10:55
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ExcelGeneratorApplication {


    public static void main(String[] args) {
        SpringApplication.run(ExcelGeneratorApplication.class, args);
    }
}
