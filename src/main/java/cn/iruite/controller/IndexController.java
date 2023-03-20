package cn.iruite.controller;

import cn.iruite.service.IndexService;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: LiRuite
 * @Date: 2023/3/15 10:59
 */
@RestController
@RequestMapping("relaxJob")
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    @PostMapping("start")
    public void start(@RequestPart MultipartFile file, HttpServletResponse response) throws Exception{
        indexService.start(file, response);
    }

}
