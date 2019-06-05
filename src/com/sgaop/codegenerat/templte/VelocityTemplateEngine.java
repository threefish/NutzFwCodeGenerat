package com.sgaop.codegenerat.templte;

import com.sgaop.codegenerat.util.FileUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.beetl.core.exception.BeetlException;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2017/12/30 0030
 */
public class VelocityTemplateEngine implements ITemplteEngine {

    VelocityEngine ve = new VelocityEngine();

    public VelocityTemplateEngine() {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, VelocityTemplateResourceLoader.class.getName());
//        ve.setProperty("classpath.resource.loader.class", VelocityTemplateResourceLoader.class.getName());
        ve.setProperty(RuntimeConstants.RUNTIME_LOG, "startup_wizard_vel.log");
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,"org.apache.velocity.runtime.log.CommonsLogLogChute");
        ve.setProperty(CommonsLogLogChute.LOGCHUTE_COMMONS_LOG_NAME, "initial_wizard_velocity");
        ve.init();
    }

    /**
     * 解析模版
     *
     * @param templeText 模版内容
     * @param bindData   绑定参数
     * @return
     */
    @Override
    public String render(String templeText, Map<String, Object> bindData) throws BeetlException {
        Template t = ve.getTemplate(templeText);
        VelocityContext ctx = new VelocityContext();
        bindData.forEach((key, val) -> ctx.put(key, val));
        StringWriter sw = new StringWriter();
        t.merge(ctx, sw);
        return sw.toString();
    }


    /**
     * 解析模版
     *
     * @param templeText 模版内容
     * @param bindData   绑定参数
     * @param path       写入的文件
     * @return
     */
    @Override
    public Path renderToFile(String templeText, Map bindData, Path path) throws BeetlException {
        Path floderPath = path.getParent();
        if (!floderPath.toFile().exists()) {
            floderPath.toFile().mkdirs();
        }
        FileUtil.strToFile(render(templeText, bindData), path.toFile());
        return floderPath;
    }
}
