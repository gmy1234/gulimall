package com.gmy.gulimall.search.service;

import com.gmy.gulimall.search.vo.SearchParam;
import com.gmy.gulimall.search.vo.SearchRes;
import org.springframework.stereotype.Service;

@Service
public interface MallSearchService {

    /**
     *
     * @param searchKey 检索的信息
     * @return 返回的检索结果
     */
    SearchRes search(SearchParam searchKey);
}
