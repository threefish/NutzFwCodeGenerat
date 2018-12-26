package com.sgaop.codegenerat.group;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.sgaop.codegenerat.util.IconsUtil;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/26
 */
public class NutzFwFileTemplateGroupFactory implements FileTemplateGroupDescriptorFactory {
    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("NutzFw", IconsUtil.NUTZ);
        group.addTemplate(new FileTemplateDescriptor("Edit.html"));
        group.addTemplate(new FileTemplateDescriptor("Index.html"));
        group.addTemplate(new FileTemplateDescriptor("Action.java"));
        group.addTemplate(new FileTemplateDescriptor("Service.java"));
        group.addTemplate(new FileTemplateDescriptor("ServiceImpl.java"));
        return group;
    }
}
