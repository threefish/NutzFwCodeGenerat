package com.sgaop.codegenerat.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.sgaop.codegenerat.templte.BeetlTemplteEngine;
import com.sgaop.codegenerat.templte.ITemplteEngine;
import com.sgaop.codegenerat.templte.VelocityTemplateEngine;
import com.sgaop.codegenerat.util.FileUtil;
import com.sgaop.codegenerat.vo.JavaBaseVO;
import com.sgaop.codegenerat.vo.JavaFieldVO;
import com.sgaop.codegenerat.vo.RenderDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PreviewData extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable baseTable;
    private JTable fieldTable;

    private String moduleBasePath;
    private HashMap bindData;
    private Project project;
    private RenderDTO renderDTO;
    private CreateServiceImplFram createServiceImplFram;

    public PreviewData(CreateServiceImplFram createServiceImplFram, Project project, RenderDTO renderDTO, String moduleBasePath, HashMap bindData) {
        this.createServiceImplFram = createServiceImplFram;
        this.project = project;
        this.moduleBasePath = moduleBasePath;
        this.bindData = bindData;
        this.renderDTO = renderDTO;
        int w = 1600, h = 800;
        int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (w / 2));
        setContentPane(contentPane);
        setModal(true);
        setTitle("代码生成");
        setBounds(x, 10, w, h);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        JavaBaseVO javaBaseVO = (JavaBaseVO) bindData.getOrDefault("base", new JavaBaseVO());
        DefaultTableModel baseModel = new DefaultTableModel(new String[]{"变量名", "变量值", "变量意义描述"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        baseModel.addRow(new String[]{"entityName", javaBaseVO.getEntityName(), "entityName"});
        baseModel.addRow(new String[]{"entityPackage", javaBaseVO.getEntityPackage(), "entity Package"});
        baseModel.addRow(new String[]{"serviceFileName", javaBaseVO.getServiceFileName(), "service File Name"});
        baseModel.addRow(new String[]{"servicePackage", javaBaseVO.getServicePackage(), "service Package"});
        baseModel.addRow(new String[]{"serviceImplFileName", javaBaseVO.getServiceImplFileName(), "serviceImpl File Name"});
        baseModel.addRow(new String[]{"serviceImplPackage", javaBaseVO.getServiceImplPackage(), "serviceImpl Package"});
        baseModel.addRow(new String[]{"actionFileName", javaBaseVO.getActionFileName(), "action File Name"});
        baseModel.addRow(new String[]{"actionPackage", javaBaseVO.getActionPackage(), "action package"});
        baseModel.addRow(new String[]{"funName", javaBaseVO.getFunName(), "当前功能名称"});
        baseModel.addRow(new String[]{"templatePath", javaBaseVO.getTemplatePath(), "HTML模版目录"});
        baseModel.addRow(new String[]{"userName", javaBaseVO.getUserName(), "姓名"});
        baseModel.addRow(new String[]{"userMail", javaBaseVO.getUserMail(), "用户邮箱"});
        baseModel.addRow(new String[]{"primaryKey", javaBaseVO.getPrimaryKey(), "主键"});
        baseModel.addRow(new String[]{"uuid", String.valueOf(javaBaseVO.isUuid()), "主键是否是UUID主键"});
        baseModel.addRow(new String[]{"richText", String.valueOf(javaBaseVO.isRichText()), "是否有UE富文本编辑器"});
        baseModel.addRow(new String[]{"attachment", String.valueOf(javaBaseVO.isAttachment()), "是否有附件上传"});
        baseModel.addRow(new String[]{"multidict", String.valueOf(javaBaseVO.isMultiDict()), "是否是NutzFw多选字典"});
        baseModel.addRow(new String[]{"oneOneRelation", String.valueOf(javaBaseVO.isOneOneRelation()), "存在一对一表单关联"});
        baseTable.setRowHeight(20);
        baseTable.setModel(baseModel);

        List<JavaFieldVO> javaFieldVOList = (List<JavaFieldVO>) bindData.getOrDefault("fields", Arrays.asList());
        String[] headers = new String[]{
                "JAVA字段名称", "主键", "字段描述", "数据库字段名", "JAVA字段类型", "JAVA字段类型包含引用", "是否日期",
                "是否字典", "字典Code", "是否必填必选字段", "文本类型 输入框2-多行文本框3-百度UE4", "附件类型", "是否是多附件类型", "附件全部是图片",
                "限制附件格式", "提示信息", "文本最大长度", "是单表关联", "表关联字段", "表关联类", "表关联类全路径"
        };
        DefaultTableModel fieldsModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        javaFieldVOList.forEach(vo -> {
            fieldsModel.addRow(new Object[]{
                    vo.getName(), vo.isPrimaryKey(), vo.getComment(), vo.getColumnName(), vo.getType(), vo.getFullType(), vo.isDate(),
                    vo.isDict(), vo.getDictCode(), vo.isRequired(), vo.getText(), vo.isAttachment(), vo.isAttachmentMultiple()
                    , vo.isAttachmentAllIsImg(),
                    vo.getAttachSuffix(), vo.getPlaceholder(), vo.getMaxLength(), vo.isOneOne(),
                    vo.getOneOneField(), vo.getOneOneClassName(), vo.getOneOneClassQualifiedName()
            });
        });

        fieldTable.setRowHeight(20);
        fieldTable.setModel(fieldsModel);
        for (int i = 0; i < headers.length; i++) {
            TableColumn tableColumn = fieldTable.getColumnModel().getColumn(i);
            Object value = fieldsModel.getValueAt(0, i);
            if (value instanceof Boolean) {
                tableColumn.setPreferredWidth(50);
            } else if (value instanceof Integer) {
                tableColumn.setPreferredWidth(20);
            } else {
                tableColumn.setPreferredWidth(100);
            }
        }
    }

    private void onOK() {
        try {
            render();
            createServiceImplFram.dispose();
            this.dispose();
            Messages.showInfoMessage(project, "代码生成完成！", "生成完成");
        } catch (Throwable ex) {
            String errorMsg = ex.getMessage();
            if (errorMsg == null && ex.getCause() != null) {
                errorMsg = ex.getCause().getMessage();
            }
            Messages.showErrorDialog(errorMsg, "错误提示");
            throw ex;
        }
    }


    private void onCancel() {
        dispose();
    }

    /**
     * 生成文件
     */
    private void render() {
        Path servicePath = renderDTO.getServicePath();
        Path serviceImplPath = renderDTO.getServiceImplPath();
        Path actionPath = renderDTO.getActionPath();
        Path indexPath = renderDTO.getIndexPath();
        Path editPath = renderDTO.getEditPath();
        ITemplteEngine renderTemplte;
        if ("beetl".equals(renderDTO.getTemplateEngine())) {
            renderTemplte = new BeetlTemplteEngine();
        } else {
            renderTemplte = new VelocityTemplateEngine();
        }
        if (renderDTO.isService() && servicePath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(servicePath.toFile()), bindData, getPath(moduleBasePath, renderDTO.getServicePackageText()));
            refreshPath(path);
        }
        if (renderDTO.isServiceImpl() && serviceImplPath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(serviceImplPath.toFile()), bindData, getPath(moduleBasePath, renderDTO.getServiceImplPackageText()));
            refreshPath(path);
        }
        if (renderDTO.isAction() && actionPath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(actionPath.toFile()), bindData, getPath(moduleBasePath, renderDTO.getActionPackageText()));
            refreshPath(path);
        }
        if (renderDTO.isHtmlPath() && indexPath.toFile().exists()) {
            if (indexPath.toFile().exists()) {
                Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(indexPath.toFile()), bindData, Paths.get(renderDTO.getBasePathText(), renderDTO.getHtmlPaths(), "index.html"));
                refreshPath(path);
            }
            if (editPath.toFile().exists()) {
                Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(editPath.toFile()), bindData, Paths.get(renderDTO.getBasePathText(), renderDTO.getHtmlPaths(), "edit.html"));
                refreshPath(path);
            }
        }

    }


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

}
