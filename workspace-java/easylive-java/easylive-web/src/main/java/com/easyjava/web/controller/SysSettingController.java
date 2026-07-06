package com.easyjava.web.controller;

import com.easyjava.component.RedisComponent;
import com.easyjava.entity.config.AppConfig;
import com.easyjava.entity.constants.Constans;
import com.easyjava.entity.dto.SysSettingDto;
import com.easyjava.entity.dto.TokenUserInfoDto;
import com.easyjava.entity.dto.UploadingFileDto;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.enums.ResponseCodeEnum;
import com.easyjava.exception.BusinessException;
import com.easyjava.utlis.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@RequestMapping("/sysSetting")
@RestController
@Slf4j
@Validated
public class SysSettingController extends ABaseController {
    @Resource
    private RedisComponent redisComponent;
/*获取系统全局配置信息


* 1️⃣ 从Redis缓存中读取系统设置
2️⃣ 直接返回配置对象
```

**实现特点：**
- ✅ **零数据库查询**：直接从内存缓存读取
- ✅ **高性能**：Redis读取速度极快
- ✅ **简洁明了**：一行代码完成功能

*
* 为什么用Redis？

高频访问：系统设置几乎每个页面都需要
读多写少：配置很少变动，适合缓存
性能优化：避免重复查询数据库

从Redis读取并返回系统配置，为前端和其他模块提供全局配置信息，保证高性能和实时性。
虽然代码很简洁，但这是典型的配置中心模式，在分布式系统中非常常见！*/
    @RequestMapping("/getSetting")
    public ResponseVO getSetting(){
        return getSuccessResponseVO(redisComponent.getSysSettingDto());
    }

}
