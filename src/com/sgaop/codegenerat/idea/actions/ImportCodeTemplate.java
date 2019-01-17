package com.sgaop.codegenerat.idea.actions;

import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.sgaop.codegenerat.util.FileUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/16
 */
public class ImportCodeTemplate implements ActionListener {

    private static final String EXTENSION = "zip";

    Project project;

    FileTemplateManager fileTemplateManager;

    public ImportCodeTemplate(Project project) {
        this.project = project;
        this.fileTemplateManager = FileTemplateManager.getInstance(project);
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        OpenProjectFileChooserDescriptor descriptor = new OpenProjectFileChooserDescriptor(true);
        descriptor.setForcedToUseIdeaFileChooser(true);
        descriptor.setHideIgnored(true);
        descriptor.setTitle("请选择需要导入的模版文件(zip)");
        Path path = null;
        try {
            path = FileUtil.createTempFolderPath("NutzFwCodeGeneratImport");
            Path finalPath = path;
            FileChooser.chooseFiles(descriptor, project, null, virtualFiles -> {
                VirtualFile virtualFile = virtualFiles.get(0);
                if (virtualFile != null && EXTENSION.equals(virtualFile.getExtension())) {
                    FileUtil.extractZipFile(Paths.get(virtualFile.getPath()).toFile(), finalPath.toFile());
                    File[] list = finalPath.toFile().listFiles();
                    AtomicInteger count = new AtomicInteger();
                    Arrays.stream(list).filter(file -> file.isFile()).forEach(file -> {
                        String text = FileUtil.readStringByFile(file);
                        count.set(count.get() + setText(file.getName(), text));
                    });
                    Messages.showInfoMessage(MessageFormat.format("成功导入{0}个模版文件！", count), "提示");
                } else {
                    Messages.showErrorDialog("请选择模版压缩包！", "提示");
                }
            });
        } catch (IOException e) {
            Messages.showErrorDialog(e.getMessage(), "导入错误！");
        } finally {
            if (path != null) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                }
            }
        }
    }


    private int setText(String fileName, String text) {
        int count = 0;
        switch (fileName) {
            case "Action.java.ft":
                fileTemplateManager.getJ2eeTemplate("Action").setText(text);
                count++;
                break;
            case "Edit.html.ft":
                fileTemplateManager.getJ2eeTemplate("Edit").setText(text);
                count++;
                break;
            case "Index.html.ft":
                fileTemplateManager.getJ2eeTemplate("Index").setText(text);
                count++;
                break;
            case "Service.java.ft":
                fileTemplateManager.getJ2eeTemplate("Service").setText(text);
                count++;
                break;
            case "ServiceImpl.java.ft":
                fileTemplateManager.getJ2eeTemplate("ServiceImpl").setText(text);
                count++;
                break;
            default:
                break;
        }
        return count;
    }

}
