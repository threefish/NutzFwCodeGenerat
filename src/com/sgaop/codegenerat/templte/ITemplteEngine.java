package com.sgaop.codegenerat.templte;

import java.nio.file.Path;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2017/12/30 0030
 */
public interface ITemplteEngine {
    /**
     * 解析模版
     *
     * @param templePath 模版地址
     * @param bindData   绑定参数
     * @return
     */
    String render(String templePath, Map<String, Object> bindData) throws Exception;

    Path renderToFile(String templePath, Map<String, Object> bindData, Path path);
}
