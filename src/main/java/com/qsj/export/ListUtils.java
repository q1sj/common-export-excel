package com.qsj.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Q1sj
 * @date 2020.12.23 15:33
 */
public class ListUtils {

    /**
     * 拆分list
     * @param list
     * @param maxSize
     * @param <T>
     * @return
     */
    public static  <T> List<List<T>> subList(List<T> list, int maxSize){
        // size小于最大值直接返回
        if (list.size()<=maxSize) {
            return Collections.singletonList(list);
        }

        List<List<T>> result = new ArrayList<List<T>>();
        for (int i = 0; i < list.size(); i+=maxSize) {
            // 检查是否下标越界
            if (i+maxSize<=list.size()){
                result.add(list.subList(i,i+maxSize));
            }else {
                result.add(list.subList(i,list.size()));
            }
        }
        return result;
    }
}
