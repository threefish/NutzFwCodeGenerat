package com.sgaop.codegenerat.idea.actions;

import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.sgaop.codegenerat.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/16
 */
public class ImportCodeTemplate implements ActionListener {

    private static final String EXTENSION = ".zip";

    static HashMap<String, String> templates = new HashMap<>();

    static {
        templates.put("Action.java.ft", "Action");
        templates.put("Edit.html.ft", "Edit");
        templates.put("Index.html.ft", "Index");
        templates.put("Service.java.ft", "Service");
        templates.put("ServiceImpl.java.ft", "ServiceImpl");
    }

    FileTemplateManager fileTemplateManager;
    private Project project;

    public ImportCodeTemplate(Project project) {
        this.project = project;
        this.fileTemplateManager = FileTemplateManager.getInstance(project);
    }

    private int setText(String fileName, String text) {
        String val = templates.getOrDefault(fileName, "");
        if (!"".equals(val)) {
            fileTemplateManager.getJ2eeTemplate(val).setText(text);
            return 1;
        }
        return 0;
    }

    @Override
    public void actionPerformed(ActionEvent anActionEvent) {
        Path path = null;
        try {
            path = FileUtil.createTempFolderPath("NutzFwCodeGeneratImport");
            Path finalPath = path;
            JFileChooser fd = new JFileChooser();
            fd.setFileHidingEnabled(false);
            fd.setCurrentDirectory(new File(project.getBasePath()));
            fd.setDialogTitle("请选择需要导入的模版文件(zip)");
            fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fd.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory() || f.getName().toLowerCase().endsWith(EXTENSION)) {
                        return true;
                    }
                    return false;
                }
                @Override
                public String getDescription() {
                    return "请选择需要导入的模版文件(zip)";
                }
            });
            fd.showOpenDialog(null);
            File selectedFile = fd.getSelectedFile();
            FileUtil.extractZipFile(selectedFile, finalPath.toFile());
            File[] list = finalPath.toFile().listFiles();
            AtomicInteger count = new AtomicInteger();
            Arrays.stream(list).filter(file -> file.isFile()).forEach(file -> {
                String text = FileUtil.readStringByFile(file);
                count.set(count.get() + setText(file.getName(), text));
            });
            Messages.showInfoMessage(MessageFormat.format("成功导入{0}个模版文件！", count), "提示");
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


}
