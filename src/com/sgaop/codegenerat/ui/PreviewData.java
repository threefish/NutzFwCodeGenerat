package com.sgaop.codegenerat.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.sgaop.codegenerat.project.ToolCfigurationData;
import com.sgaop.codegenerat.templte.BeetlTemplteEngine;
import com.sgaop.codegenerat.templte.ITemplteEngine;
import com.sgaop.codegenerat.util.FileUtil;
import com.sgaop.codegenerat.vo.RenderDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class PreviewData extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

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
        int w = 500, h = 400;
        int x = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (w / 2));
        int y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (h / 2));
        setContentPane(contentPane);
        setModal(true);
        setTitle("代码生成");
        setBounds(x, y, w, h);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
        ITemplteEngine renderTemplte = new BeetlTemplteEngine();
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
