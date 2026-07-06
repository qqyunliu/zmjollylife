package com.easyjava.admin.controller;

import com.easyjava.entity.po.CategoryInfo;
import com.easyjava.entity.query.CategoryInfoquery;
import com.easyjava.entity.vo.ResponseVO;
import com.easyjava.exception.BusinessException;
import com.easyjava.service.CategoryInfoService;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/category")
public class categoryController extends ABaseController{
    @Resource
    private CategoryInfoService categoryInfoService;

    @RequestMapping("/loadCategory")
    public ResponseVO loadCategory(CategoryInfoquery query) {
        System.out.println("=== Controller被调用了 ===");
        query.setOrderBy("sort asc");
        query.setConvert2Tree(true);
        List<CategoryInfo> categoryInfoList = categoryInfoService.findListByParam(query);
        System.out.println("查询到数据数量: " + categoryInfoList.size());

        // 打印第一条数据的详细信息
        if (!categoryInfoList.isEmpty()) {
            CategoryInfo first = categoryInfoList.get(0);
            System.out.println("第一条数据: " + first);
            System.out.println("categoryId: " + first.getCategoryId());
            System.out.println("categoryName: " + first.getCategoryName());
        }

        ResponseVO result = getSuccessResponseVO(categoryInfoList);
        System.out.println("返回的ResponseVO: " + result);
        return result;
    }

    @RequestMapping("/saveCategory")
    public ResponseVO saveCategory(@NotNull Integer pCategoryId,
                                   Integer categoryId,
                                   @NotEmpty String categoryCode,
                                   @NotEmpty String categoryName,
                                   String icon,
                                   String background) throws BusinessException {
        CategoryInfo categoryInfo=new CategoryInfo();
        categoryInfo.setCategoryId(categoryId);
        categoryInfo.setPCategoryId(pCategoryId);
        categoryInfo.setCategoryCode(categoryCode);
        categoryInfo.setCategoryName(categoryName);
        categoryInfo.setIcon(icon);
        categoryInfo.setBackground(background);

        categoryInfoService.saveCategoryInfo(categoryInfo);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/deleteCategory")
    public ResponseVO deleteCategory(@NotNull Integer categoryId){
        categoryInfoService.delCategoryInfo(categoryId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/changeSort")
    public ResponseVO changeSort(@NotNull Integer pCategoryId,@NotEmpty String categoryIds){
        categoryInfoService.changeSort(pCategoryId,categoryIds);
        return getSuccessResponseVO(null);
    }

}
