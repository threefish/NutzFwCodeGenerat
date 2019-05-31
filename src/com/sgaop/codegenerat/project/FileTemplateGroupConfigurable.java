package com.sgaop.codegenerat.project;

import com.google.common.base.Objects;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.ProjectManager;
import com.sgaop.codegenerat.util.Strings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/28
 */
public class FileTemplateGroupConfigurable implements Configurable {

    FileTemplateGroupConfigurableUI ui;

    private ToolCfigurationData configuration = ToolCfigurationData.getInstance();

    public FileTemplateGroupConfigurable() {
        this.ui = new FileTemplateGroupConfigurableUI(ProjectManager.getInstance().getDefaultProject(), configuration);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "NutzFw Code Generat";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return ui.getRoot();
    }

    @Override
    public boolean isModified() {
        return !Objects.equal(configuration.getTemplatePath(), ui.getTemplatePath().getText())
                || !Objects.equal(configuration.getUserMail(), ui.getUserMail().getText())
                || !Objects.equal(configuration.getUserName(), ui.getUserName().getText());
    }

    @Override
    public void apply() throws ConfigurationException {
        if (Strings.isNullOrEmpty(ui.getTemplatePath().getText())) {
            throw new ConfigurationException("请选择模板目录");
        }
        if (Strings.isNullOrEmpty(ui.getUserName().getText())) {
            throw new ConfigurationException("请输入姓名");
        }
        if (Strings.isNullOrEmpty(ui.getUserMail().getText())) {
            throw new ConfigurationException("请输入邮箱");
        }
        configuration.setTemplatePath(ui.getTemplatePath().getText());
        configuration.setUserMail(ui.getUserMail().getText());
        configuration.setUserName(ui.getUserName().getText());
    }

    @Override
    public void reset() {
        ui.getUserName().setText(configuration.getUserName());
        ui.getUserMail().setText(configuration.getUserMail());
        ui.getTemplatePath().setText(configuration.getTemplatePath());
    }
}
