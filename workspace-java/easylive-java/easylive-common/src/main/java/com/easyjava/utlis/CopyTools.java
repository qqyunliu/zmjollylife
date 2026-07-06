package com.easyjava.utlis;


import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
/*这是一个对象拷贝工具类 CopyTools，它基于Spring的 BeanUtils 提供了便捷的对象拷贝功能
* 这个工具类的主要目的是简化对象之间的属性拷贝操作，特别是在DTO、VO、PO之间的转换场景*/
public class CopyTools {
    public static <T, S> List<T> copyList(List<S> sList, Class<T> classz) {
        List<T> list = new ArrayList<T>();
        for (S s : sList) {
            T t = null;
            try {
                t = classz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(s, t);
            list.add(t);
        }
        return list;
    }

    public static <T, S> T copy(S s, Class<T> classz) {
        T t = null;
        try {
            t = classz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s, t);
        return t;

    }

    public static <T, S> void copyProperties(S s, T t) {
        BeanUtils.copyProperties(s, t);
    }

}
