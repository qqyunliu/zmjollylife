package com.easyjava.utlis;

import com.easyjava.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;


import java.lang.reflect.Field;

import java.lang.reflect.Method;
/*这是一个字符串工具类 StringTools，它提供了各种字符串处理和校验功能*/
public class StringTools {


    public static void checkParam(Object param) throws BusinessException {
        //校验对象参数是否至少有一个非空字段,防止全空条件更新或删除数据
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object != null && object instanceof String && !StringTools.isEmpty(object.toString())
                        || object != null && !(object instanceof String)) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("多参数更新，册除，必须有非空条件");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("校验参数是否为空失败");
        }
    }


    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static boolean isEmpty(String str) {
        //比常规的空值检查更严格，包含了特殊情况的处理。
        if (null == str || "".equals(str) || "null".equals(str) || "\u1000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static final String getRandomString(Integer count){
//随机字符串生成
        return RandomStringUtils.random(count,true,true);
    }

    public static final String getRandomNumber(Integer count){
        return RandomStringUtils.random(count,true,true);
    }
    public static final String encodeByMd5(String originString){
        //对字符串进行MD5加密
        //特点：空值安全，输入为空时返回null
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return false;
        }
        // 检查路径遍历攻击
        if (path.contains("../") || path.contains("..\\")) {
            return false;
        }
        // 检查其他危险模式
        if (path.contains("://") || path.contains("javascript:")) {
            return false;
        }
        return true;
    }

    public static String getFileSuffix (String fileName){
        //文件后缀提取
        if(StringTools.isEmpty(fileName) || !fileName.contains(".")){
            return  null;
        }
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        return suffix;
    }
}
