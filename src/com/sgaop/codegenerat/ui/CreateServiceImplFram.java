package com.sgaop.codegenerat.ui;

import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.roots.impl.JavaProjectModelModificationServiceImpl;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.sgaop.codegenerat.idea.ProjectPluginConfig;
import com.sgaop.codegenerat.project.ToolCfigurationData;
import com.sgaop.codegenerat.templte.BeetlTemplteEngine;
import com.sgaop.codegenerat.templte.ITemplteEngine;
import com.sgaop.codegenerat.util.FileUtil;
import com.sgaop.codegenerat.util.Strings;
import com.sgaop.codegenerat.vo.JavaBaseVO;
import com.sgaop.codegenerat.vo.JavaFieldVO;
import com.sgaop.codegenerat.vo.RenderDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author 黄川 306955302@qq.com
 * @date: 2018/5/30
 * 自动生成接口和实现类
 */
public class CreateServiceImplFram extends JDialog {
    private ProjectPluginConfig pluginrInfo;
    private String entityPackage;
    private String entityName;
    private String serviceFileName;
    private String serviceImplFileName;
    private String servicePackage;
    private String serviceImplPackage;
    private String actionPackage;
    private String actionFileName;
    private String htmlPaths;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField servicePackageText;
    private JTextField serviceImplPackageText;
    private JTextField actionPackageText;
    private JTextField htmlPathText;
    private JCheckBox actionCheckBox;
    private JCheckBox htmlPathCheckBox;
    private TextFieldWithBrowseButton basePathText;
    private JTextField funNameText;
    private JCheckBox implCheckBox;
    private JCheckBox serviceCheckBox;
    private JComboBox templateSelect;
    private JComboBox templateEngine;
    private ToolCfigurationData configuration;

    public CreateServiceImplFram(ToolCfigurationData configuration, ProjectPluginConfig pluginEditorInfo, String entityPackage, String entityName) {
        this.configuration = configuration;
        this.pluginrInfo = pluginEditorInfo;
        this.entityPackage = entityPackage;
        this.entityName = entityName;
        this.serviceFileName = entityName + "Service";
        this.serviceImplFileName = entityName + "ServiceImpl";
        this.actionFileName = entityName + "Action";
        this.servicePackage = entityPackage.replace("entity", "service");
        this.serviceImplPackage = entityPackage.replace("entity", "service") + ".impl";
        this.actionPackage = entityPackage.replace("entity", "module");
        this.htmlPaths = "/" + entityName + "/";
        int w = 500, h = 400;
        int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (w / 2));
        int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (h / 2));
        setContentPane(contentPane);
        setTitle("代码生成");
        setModal(true);
        setBounds(x, y, w, h);
        getRootPane().setDefaultButton(buttonOK);
        servicePackageText.setText(this.servicePackage + "." + serviceFileName);
        serviceImplPackageText.setText(this.serviceImplPackage + "." + serviceImplFileName);
        actionPackageText.setText(this.actionPackage + "." + actionFileName);
        htmlPathText.setText(this.htmlPaths);
        templateEngine.addItem("beetl");
        templateEngine.addItem("apache velocity");
        buttonOK.addActionListener((e) -> onOK());
        buttonCancel.addActionListener((e) -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(((e) -> onCancel()), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        basePathText.addActionListener((e -> {
            OpenProjectFileChooserDescriptor descriptor = new OpenProjectFileChooserDescriptor(false, false);
            descriptor.setHideIgnored(true);
            descriptor.setShowFileSystemRoots(false);
            descriptor.setRoots(LocalFileSystem.getInstance().findFileByPath(pluginEditorInfo.getProject().getBasePath()));
            descriptor.setTitle("请选择html生成目录");
            FileChooser.chooseFiles(descriptor, pluginEditorInfo.getProject(), null, virtualFiles -> {
                VirtualFile virtualFile = virtualFiles.get(0);
                String selectPath = virtualFile.getCanonicalPath();
                selectPath = selectPath.replace("\\\\", "/").replace("\\", "/");
                basePathText.setText(selectPath);
            });
        }));
        if (Strings.isNullOrEmpty(configuration.getTemplatePath())) {
            Messages.showErrorDialog("请先设置模板目录", "错误提示");
        } else {
            Path path = Paths.get(configuration.getTemplatePath());
            File[] list = path.toFile().listFiles();
            for (File file : list) {
                if (file.isFile()) {
                    continue;
                }
                templateSelect.addItem(file.getName());
            }
        }
    }

    private void onOK() {
        try {
            if (this.htmlPathCheckBox.isSelected() && this.basePathText.getText().trim().length() == 0) {
                Messages.showErrorDialog("请选择HTML目录", "错误提示");
            } else if (this.funNameText.getText().trim().length() == 0) {
                Messages.showErrorDialog("请输入功能名称", "错误提示");
            } else {
                String moduleBasePath = pluginrInfo.getPsiFile().getVirtualFile().getCanonicalPath();
                String temp = entityPackage.replaceAll("\\.", "/");
                moduleBasePath = moduleBasePath.replace(temp, "");
                moduleBasePath = moduleBasePath.replace("/" + entityName + ".java", "");
                RenderDTO renderDTO = new RenderDTO();
                renderDTO.setServicePath(Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Service.java"));
                renderDTO.setServiceImplPath(Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "ServiceImpl.java"));
                renderDTO.setActionPath(Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Action.java"));
                renderDTO.setIndexPath(Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Index.html"));
                renderDTO.setEditPath(Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Edit.html"));
                renderDTO.setHtmlPath(this.htmlPathCheckBox.isSelected());
                renderDTO.setAction(this.actionCheckBox.isSelected());
                renderDTO.setServiceImpl(this.implCheckBox.isSelected());
                renderDTO.setService(this.serviceCheckBox.isSelected());
                renderDTO.setServicePackageText(this.servicePackageText.getText());
                renderDTO.setServiceImplPackageText(this.serviceImplPackageText.getText());
                renderDTO.setActionPackageText(this.actionPackageText.getText());
                renderDTO.setBasePathText(this.basePathText.getText());
                renderDTO.setHtmlPaths(this.htmlPaths);
                renderDTO.setTemplateEngine(String.valueOf(templateEngine.getSelectedItem()));

                PreviewData dialog = new PreviewData(this, pluginrInfo.getProject(), renderDTO, moduleBasePath, buildData(pluginrInfo.getJavaFields()));
                dialog.pack();
                dialog.setVisible(true);
            }
        } catch (Throwable ex) {
            String errorMsg = ex.getMessage();
            if (errorMsg == null && ex.getCause() != null) {
                errorMsg = ex.getCause().getMessage();
            }
            Messages.showErrorDialog(errorMsg, "错误提示");
            throw ex;
        }
    }

    /**
     * 绑定参数
     *
     * @param javaFields
     * @return
     */
    private HashMap buildData(List<JavaFieldVO> javaFields) {
        JavaBaseVO baseVO = new JavaBaseVO();
        baseVO.setEntityName(this.entityName);
        baseVO.setEntityPackage(this.entityPackage);
        baseVO.setServiceFileName(this.serviceFileName);
        baseVO.setServicePackage(this.servicePackage);
        baseVO.setServiceImplFileName(this.serviceImplFileName);
        baseVO.setServiceImplPackage(this.serviceImplPackage);
        baseVO.setActionFileName(this.actionFileName);
        baseVO.setActionPackage(this.actionPackage);
        baseVO.setFunName(this.funNameText.getText());
        baseVO.setUserName(configuration.getUserName());
        baseVO.setUserMail(configuration.getUserMail());
        Optional optional = javaFields.stream().filter(JavaFieldVO::isPrimaryKey).findFirst();
        if (optional.isPresent()) {
            JavaFieldVO field = (JavaFieldVO) optional.get();
            baseVO.setPrimaryKey(field.getColumnName());
            if ("uuid".equals(field.getColumnName().toLowerCase())) {
                baseVO.setUuid(true);
            }
        }
        baseVO.setRichText(javaFields.stream().filter(javaFieldVO -> javaFieldVO.getText() == 4).findFirst().isPresent());
        baseVO.setAttachment(javaFields.stream().filter(JavaFieldVO::isAttachment).findFirst() != null);
        baseVO.setMultiDict(javaFields.stream().filter(JavaFieldVO::isMultiDict).findFirst() != null);
        baseVO.setOneOneRelation(javaFields.stream().filter(JavaFieldVO::isOneOne).findFirst() != null);
        String templatePath = this.basePathText.getText();
        if (this.htmlPathCheckBox.isSelected()) {
            int start = templatePath.indexOf("WEB-INF");
            baseVO.setTemplatePath(templatePath.substring(start) + htmlPaths);
        }
        HashMap bindData = new HashMap(2);
        bindData.put("base", baseVO);
        bindData.put("fields", pluginrInfo.getJavaFields());
        return bindData;
    }


    private void onCancel() {
        dispose();
    }

}
