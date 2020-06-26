package com.concurrent.bloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.stream.IntStream;

/**
 * created by lanxinghua@2dfire.com on 2020/6/26
 * 布隆过滤器：空间效率高，概率
 */
public class BloomTest {
    public static void main(String[] args) {
        // 要传入期望处理的元素数量
        // fpp期望误判率，比如1E-7（千万分之一）
        BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 1000, 0.000001);
        filter.put("11");
        filter.put("22");
        filter.put("33");
        System.out.println(filter.mightContain("66"));
        System.out.println(filter.mightContain("11"));
        IntStream.range(0, 100_000).forEach(r->{
            if(filter.mightContain(String.valueOf(r))){
                System.out.println(r);
            }
        });
    }
}
