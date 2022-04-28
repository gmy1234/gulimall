package com.gmy.gulimall.ware.exception;

import lombok.Data;

@Data
public class NoStockException extends RuntimeException{

    private Long skuId;

    public NoStockException(){
        super();
    }

    public NoStockException(Long skuId){
        super("没有足够的库存");
    }
}
