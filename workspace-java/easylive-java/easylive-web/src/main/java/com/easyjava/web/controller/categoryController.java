package com.easyjava.web.controller;

import com.easyjava.entity.po.CategoryInfo;
import com.easyjava.entity.query.CategoryInfoquery;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.CategoryInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
/*分类信息控制器，用于处理分类相关的HTTP请求
* 客户端请求 → GET /category/loadCategory

控制器接收 → loadCategory() 方法处理

业务逻辑 → 调用 categoryInfoService.getCategoryList()

数据访问 → Service层通过Mapper访问数据库

响应封装 → 使用 getSuccessResponseVO() 包装结果

返回客户端 → JSON格式的分类列表*/
@RestController
@RequestMapping("/category")
public class categoryController extends ABaseController {
    @Resource
    private CategoryInfoService categoryInfoService;

    @RequestMapping("/loadCategory")
    public ResponseVO loadCategory() {
        return getSuccessResponseVO(categoryInfoService.getCategoryList());
    }
}