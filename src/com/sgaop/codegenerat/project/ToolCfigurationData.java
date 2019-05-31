package com.sgaop.codegenerat.project;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/31
 */
@State(name = "NutzFwCodeGenerat", storages = {@Storage("NutzFwCodeGenerat.xml")})
public class ToolCfigurationData implements PersistentStateComponent<ToolCfigurationData> {
    /**
     * 模板目录
     */
    private String templatePath;
    /**
     * 邮箱
     */
    private String userMail;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 模板列表
     */
    private List<String> templateNames = new ArrayList<>();

    @Nullable
    public static ToolCfigurationData getInstance() {
        return ServiceManager.getService(ToolCfigurationData.class);
    }

    @Override
    @Nullable
    public ToolCfigurationData getState() {
        return this;
    }


    @Override
    public void loadState(@NotNull ToolCfigurationData toolCfigurationData) {
        XmlSerializerUtil.copyBean(toolCfigurationData, this);
    }


    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public List<String> getTemplateNames() {
        return templateNames;
    }

    public void setTemplateNames(List<String> templateNames) {
        this.templateNames = templateNames;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
