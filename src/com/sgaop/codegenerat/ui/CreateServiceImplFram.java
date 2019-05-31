package com.sgaop.codegenerat.ui;

import com.intellij.ide.actions.OpenProjectFileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.sgaop.codegenerat.idea.ProjectPluginConfig;
import com.sgaop.codegenerat.project.ToolCfigurationData;
import com.sgaop.codegenerat.templte.BeetlTemplteEngine;
import com.sgaop.codegenerat.templte.ITemplteEngine;
import com.sgaop.codegenerat.util.FileUtil;
import com.sgaop.codegenerat.vo.JavaBaseVO;
import com.sgaop.codegenerat.vo.JavaFieldVO;

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
    ProjectPluginConfig pluginrInfo;
    String entityPackage;
    String entityName;
    String serviceFileName;
    String serviceImplFileName;
    String servicePackage;
    String serviceImplPackage;
    String actionPackage;
    String actionFileName;
    String htmlPaths;
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
            descriptor.setRoots(pluginEditorInfo.getProject().getBaseDir());
            descriptor.setTitle("请选择WEB-INF下的目录");
            FileChooser.chooseFiles(descriptor, pluginEditorInfo.getProject(), null, virtualFiles -> {
                VirtualFile virtualFile = virtualFiles.get(0);
                String selectPath = virtualFile.getCanonicalPath();
                int start = selectPath.indexOf("WEB-INF");
                if (start == -1) {
                    Messages.showErrorDialog("请选择WEB-INF下的目录", "错误提示");
                } else {
                    selectPath = selectPath.replace("\\\\", "/").replace("\\", "/");
                    basePathText.setText(selectPath);
                }
            });
        }));
        Path path = Paths.get(configuration.getTemplatePath());
        File[] list = path.toFile().listFiles();
        for (File file : list) {
            if (file.isFile()) {
                continue;
            }
            templateSelect.addItem(file.getName());
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
                render(moduleBasePath, buildData(pluginrInfo.getJavaFields()));
                dispose();
                Messages.showInfoMessage(pluginrInfo.getProject(), "代码生成完成！", "生成完成");
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

    /**
     * 生成文件
     *
     * @param moduleBasePath
     * @param bindData
     */
    private void render(String moduleBasePath, HashMap bindData) {
        Path servicePath = Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Service.java");
        Path serviceImplPath = Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "ServiceImpl.java");
        Path actionPath = Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Action.java");
        Path indexPath = Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Index.html");
        Path editPath = Paths.get(configuration.getTemplatePath(), templateSelect.getSelectedItem().toString(), "Edit.html");
        ITemplteEngine renderTemplte = new BeetlTemplteEngine();
        if (this.serviceCheckBox.isSelected() && servicePath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(servicePath.toFile()), bindData, getPath(moduleBasePath, this.servicePackageText.getText()));
            refreshPath(path);
        }
        if (this.implCheckBox.isSelected() && serviceImplPath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(serviceImplPath.toFile()), bindData, getPath(moduleBasePath, this.serviceImplPackageText.getText()));
            refreshPath(path);
        }
        if (this.actionCheckBox.isSelected() && actionPath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(actionPath.toFile()), bindData, getPath(moduleBasePath, this.actionPackageText.getText()));
            refreshPath(path);
        }
        if (this.htmlPathCheckBox.isSelected() && indexPath.toFile().exists()) {
            if (indexPath.toFile().exists()) {
                Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(indexPath.toFile()), bindData, Paths.get(this.basePathText.getText(), this.htmlPaths, "index.html"));
                refreshPath(path);
            }
            if (editPath.toFile().exists()) {
                Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(editPath.toFile()), bindData, Paths.get(this.basePathText.getText(), this.htmlPaths, "edit.html"));
                refreshPath(path);
            }
        }

    }
    /**
     * 生成文件
     *
     * @param moduleBasePath
     * @param bindData
     */
    /*private void renderBak(String moduleBasePath, HashMap bindData) {
        ITemplteEngine renderTemplte = new BeetlTemplteEngine();
        FileTemplateManager fileTemplateManager = FileTemplateManager.getInstance(pluginrInfo.getProject());
        if (this.serviceCheckBox.isSelected()) {
            FileTemplate service = fileTemplateManager.getJ2eeTemplate("Service");
            Path path = renderTemplte.renderToFile(service.getText(), bindData, getPath(moduleBasePath, this.servicePackageText.getText()));
            refreshPath(path);
        }
        if (this.implCheckBox.isSelected()) {
            FileTemplate serviceImpl = fileTemplateManager.getJ2eeTemplate("ServiceImpl");
            Path path = renderTemplte.renderToFile(serviceImpl.getText(), bindData, getPath(moduleBasePath, this.serviceImplPackageText.getText()));
            refreshPath(path);
        }
        if (this.actionCheckBox.isSelected()) {
            FileTemplate actionImpl = fileTemplateManager.getJ2eeTemplate("Action");
            Path path = renderTemplte.renderToFile(actionImpl.getText(), bindData, getPath(moduleBasePath, this.actionPackageText.getText()));
            refreshPath(path);
        }
        if (this.htmlPathCheckBox.isSelected()) {
            FileTemplate indexHtml = fileTemplateManager.getJ2eeTemplate("Index");
            FileTemplate editHtml = fileTemplateManager.getJ2eeTemplate("Edit");
            renderTemplte.renderToFile(indexHtml.getText(), bindData, Paths.get(this.basePathText.getText(), this.htmlPaths, "index.html"));
            Path path = renderTemplte.renderToFile(editHtml.getText(), bindData, Paths.get(this.basePathText.getText(), this.htmlPaths, "edit.html"));
            refreshPath(path);
        }
    }*/

    /**
     * 刷新目录
     *
     * @param path
     */
    private void refreshPath(Path path) {
        VirtualFile value = VirtualFileManager.getInstance().findFileByUrl(path.toUri().toString());
        if (value != null) {
            value.refresh(true, true);
        }
    }


    private Path getPath(String basePath, String packages) {
        String[] s1 = packages.split("\\.");
        ArrayList<String> list = new ArrayList<>();
        int last = s1.length - 1;
        for (int i = 0; i < s1.length; i++) {
            if (i == last) {
                list.add(s1[i] + ".java");
            } else {
                list.add(s1[i]);
            }
        }
        return Paths.get(basePath, list.toArray(new String[0]));
    }

    private void onCancel() {
        dispose();
    }

}
