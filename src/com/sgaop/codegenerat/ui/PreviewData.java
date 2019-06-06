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
import javax.swing.table.TableModel;
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
    private Project project;
    private RenderDTO renderDTO;
    private CreateServiceImplFram createServiceImplFram;

    public PreviewData(CreateServiceImplFram createServiceImplFram, Project project, RenderDTO renderDTO, String moduleBasePath, HashMap bindData) {
        this.createServiceImplFram = createServiceImplFram;
        this.project = project;
        this.moduleBasePath = moduleBasePath;
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
        DefaultTableModel baseModel = new DefaultTableModel(new Object[]{"变量名", "变量值", "变量意义描述"}, 0);
        baseModel.addRow(new Object[]{"entityName", javaBaseVO.getEntityName(), "entityName"});
        baseModel.addRow(new Object[]{"entityPackage", javaBaseVO.getEntityPackage(), "entity Package"});
        baseModel.addRow(new Object[]{"serviceFileName", javaBaseVO.getServiceFileName(), "service File Name"});
        baseModel.addRow(new Object[]{"servicePackage", javaBaseVO.getServicePackage(), "service Package"});
        baseModel.addRow(new Object[]{"serviceImplFileName", javaBaseVO.getServiceImplFileName(), "serviceImpl File Name"});
        baseModel.addRow(new Object[]{"serviceImplPackage", javaBaseVO.getServiceImplPackage(), "serviceImpl Package"});
        baseModel.addRow(new Object[]{"actionFileName", javaBaseVO.getActionFileName(), "action File Name"});
        baseModel.addRow(new Object[]{"actionPackage", javaBaseVO.getActionPackage(), "action package"});
        baseModel.addRow(new Object[]{"funName", javaBaseVO.getFunName(), "当前功能名称"});
        baseModel.addRow(new Object[]{"userName", javaBaseVO.getUserName(), "姓名"});
        baseModel.addRow(new Object[]{"userMail", javaBaseVO.getUserMail(), "用户邮箱"});
        baseModel.addRow(new Object[]{"primaryKey", javaBaseVO.getPrimaryKey(), "主键"});
        baseModel.addRow(new Object[]{"uuid", javaBaseVO.isUuid(), "主键是否是UUID主键"});
        baseModel.addRow(new Object[]{"richText", javaBaseVO.isRichText(), "是否有UE富文本编辑器"});
        baseModel.addRow(new Object[]{"attachment", javaBaseVO.isAttachment(), "是否有附件上传"});
        baseModel.addRow(new Object[]{"multidict", javaBaseVO.isMultiDict(), "是否是NutzFw多选字典"});
        baseModel.addRow(new Object[]{"oneOneRelation", javaBaseVO.isOneOneRelation(), "存在一对一表单关联"});
        baseModel.addRow(new Object[]{"templatePath", javaBaseVO.getTemplatePath(), "HTML模版目录"});
        baseTable.setRowHeight(20);
        baseTable.setModel(baseModel);

        List<JavaFieldVO> javaFieldVOList = (List<JavaFieldVO>) bindData.getOrDefault("fields", Arrays.asList());
        String[] headers = new String[]{
                "JAVA字段名称", "主键", "字段描述", "数据库字段名", "JAVA字段类型", "JAVA字段类型包含引用", "是否日期",
                "是否字典", "字典Code", "是否必填必选字段", "文本类型 输入框2-多行文本框3-百度UE4", "附件类型", "是否是多附件类型", "附件全部是图片",
                "限制附件格式", "提示信息", "文本最大长度", "是单表关联", "表关联字段", "表关联类", "表关联类全路径"
        };
        DefaultTableModel fieldsModel = new DefaultTableModel(headers, 0);
        javaFieldVOList.forEach(vo -> {
            fieldsModel.addRow(new Object[]{
                    vo.getName(), vo.isPrimaryKey(), vo.getComment(), vo.getColumnName(), vo.getType(), vo.getFullType(), vo.isDate(),
                    vo.isDict(), vo.getDictCode(), vo.isRequired(), vo.getText(), vo.isAttachment(), vo.isAttachmentMultiple(), vo.isAttachmentAllIsImg(),
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
            TableModel fieldsModel = fieldTable.getModel();
            int rowCount = fieldsModel.getRowCount();
            List<JavaFieldVO> javaFieldVOS = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                JavaFieldVO vo = new JavaFieldVO();
                vo.setName((String) fieldsModel.getValueAt(i, 0));
                vo.setPrimaryKey(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 1))));
                vo.setComment((String) fieldsModel.getValueAt(i, 2));
                vo.setColumnName((String) fieldsModel.getValueAt(i, 3));
                vo.setType((String) fieldsModel.getValueAt(i, 4));
                vo.setFullType((String) fieldsModel.getValueAt(i, 5));
                vo.setDate(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 6))));
                vo.setDict(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 7))));
                vo.setDictCode((String) fieldsModel.getValueAt(i, 8));
                vo.setRequired(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 9))));
                vo.setText((int) fieldsModel.getValueAt(i, 10));
                vo.setAttachment(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 11))));
                vo.setAttachmentMultiple(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 12))));
                vo.setAttachmentAllIsImg(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 13))));
                vo.setAttachSuffix((String) fieldsModel.getValueAt(i, 14));
                vo.setPlaceholder((String) fieldsModel.getValueAt(i, 15));
                vo.setMaxLength((int) fieldsModel.getValueAt(i, 16));
                vo.setOneOne(Boolean.parseBoolean(String.valueOf(fieldsModel.getValueAt(i, 17))));
                vo.setOneOneField((String) fieldsModel.getValueAt(i, 18));
                vo.setOneOneClassName((String) fieldsModel.getValueAt(i, 19));
                vo.setOneOneClassQualifiedName((String) fieldsModel.getValueAt(i, 20));
                javaFieldVOS.add(vo);
            }
            TableModel baseModel = baseTable.getModel();
            JavaBaseVO javaBaseVO = new JavaBaseVO();
            javaBaseVO.setEntityName((String) baseModel.getValueAt(0, 1));
            javaBaseVO.setEntityPackage((String) baseModel.getValueAt(1, 1));
            javaBaseVO.setServiceFileName((String) baseModel.getValueAt(2, 1));
            javaBaseVO.setServicePackage((String) baseModel.getValueAt(3, 1));
            javaBaseVO.setServiceImplFileName((String) baseModel.getValueAt(4, 1));
            javaBaseVO.setServiceImplPackage((String) baseModel.getValueAt(5, 1));
            javaBaseVO.setActionFileName((String) baseModel.getValueAt(6, 1));
            javaBaseVO.setActionPackage((String) baseModel.getValueAt(7, 1));
            javaBaseVO.setFunName((String) baseModel.getValueAt(8, 1));
            javaBaseVO.setUserName((String) baseModel.getValueAt(9, 1));
            javaBaseVO.setUserMail((String) baseModel.getValueAt(10, 1));
            javaBaseVO.setPrimaryKey((String) baseModel.getValueAt(11, 1));
            javaBaseVO.setUuid(Boolean.parseBoolean(String.valueOf(baseModel.getValueAt(12, 1))));
            javaBaseVO.setRichText(Boolean.parseBoolean(String.valueOf(baseModel.getValueAt(13, 1))));
            javaBaseVO.setAttachment(Boolean.parseBoolean(String.valueOf(baseModel.getValueAt(14, 1))));
            javaBaseVO.setMultiDict(Boolean.parseBoolean(String.valueOf(baseModel.getValueAt(15, 1))));
            javaBaseVO.setOneOneRelation(Boolean.parseBoolean(String.valueOf(baseModel.getValueAt(16, 1))));
            javaBaseVO.setTemplatePath((String) baseModel.getValueAt(17, 1));
            render(javaBaseVO, javaFieldVOS);
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
    private void render(JavaBaseVO baseVO, List<JavaFieldVO> javaFieldVOS) {
        HashMap<String, Object> data = new HashMap<>(2);
        data.put("fields", javaFieldVOS);
        data.put("base", baseVO);
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
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(servicePath.toFile()), data, getPath(moduleBasePath, renderDTO.getServicePackageText()));
            refreshPath(path);
        }
        if (renderDTO.isServiceImpl() && serviceImplPath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(serviceImplPath.toFile()), data, getPath(moduleBasePath, renderDTO.getServiceImplPackageText()));
            refreshPath(path);
        }
        if (renderDTO.isAction() && actionPath.toFile().exists()) {
            Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(actionPath.toFile()), data, getPath(moduleBasePath, renderDTO.getActionPackageText()));
            refreshPath(path);
        }
        if (renderDTO.isHtmlPath() && indexPath.toFile().exists()) {
            if (indexPath.toFile().exists()) {
                Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(indexPath.toFile()), data, Paths.get(renderDTO.getBasePathText(), renderDTO.getHtmlPaths(), "index.html"));
                refreshPath(path);
            }
            if (editPath.toFile().exists()) {
                Path path = renderTemplte.renderToFile(FileUtil.readStringByFile(editPath.toFile()), data, Paths.get(renderDTO.getBasePathText(), renderDTO.getHtmlPaths(), "edit.html"));
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
