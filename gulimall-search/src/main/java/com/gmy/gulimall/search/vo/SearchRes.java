package com.gmy.gulimall.search.vo;

import com.gmy.common.to.es.SkuESModule;
import lombok.Data;

import java.util.List;

@Data
public class SearchRes {

    // 查询到所有的商品信息
    private List<SkuESModule> products;

    /**
     * 以下是分页信息：
     */

    // 当前页码
    private Integer pageNum;
    // 总记录数
    private Long total;
    // 总页码
    private Integer totalPages;

    private List<Integer> pageNavs;

    // 查询结果设计到的品牌
    private List<BrandVo> bands;
    // 查询结果涉及到的属性
    private List<AttrVo> attrs;
    // 查询涉及到的所有分类
    private List<CatalogVo> catalogs;



    //===========================以上是返回给页面的所有信息============================//


    /* 面包屑导航数据 */
    private List<NavVo> navs;

    @Data
    public static class BrandVo{

        private Long brandId;

        private String brandName;

        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;

        private String attrName;

        private List<String> attrValue;
    }

    @Data
    public static class CatalogVo{

        private Long catalogId;

        private String catalogName;
    }

    @Data
    public static class NavVo{
        private String  navName;
        private String navValue;
        private String link;
    }

}
