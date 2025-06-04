package src.main.java;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.Stack;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class WebBrowser extends JFrame {
    private JEditorPane editorPane;
    private JTextField urlField;
    private JButton backButton, forwardButton;
    private Stack<String> backStack = new Stack<>();
    private Stack<String> forwardStack = new Stack<>();
    private String currentUrl;

    public WebBrowser() {
        // 初始化界面
        setTitle("Java 网页浏览器");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // 创建菜单栏
        createMenuBar();
        
        // 创建第一个工具栏
        JToolBar toolBar1 = new JToolBar();
        backButton = new JButton("后退");
        forwardButton = new JButton("前进");
        JButton saveButton = new JButton("另存为");
        JButton viewSourceButton = new JButton("查看源代码");
        JButton exitButton = new JButton("退出");
        
        // 添加工具栏按钮事件
        backButton.addActionListener(e -> goBack());
        forwardButton.addActionListener(e -> goForward());
        saveButton.addActionListener(e -> savePage());
        viewSourceButton.addActionListener(e -> showSource());
        exitButton.addActionListener(e -> System.exit(0));
        
        toolBar1.add(saveButton);
        toolBar1.add(backButton);
        toolBar1.add(forwardButton);
        toolBar1.add(viewSourceButton);
        toolBar1.add(exitButton);

        // 创建第二个工具栏
        JToolBar toolBar2 = new JToolBar();
        toolBar2.add(new JLabel("地址:"));
        urlField = new JTextField(40);
        JButton goButton = new JButton("转向");
        
        // 地址栏事件处理
        urlField.addActionListener(e -> loadPage(urlField.getText()));
        goButton.addActionListener(e -> loadPage(urlField.getText()));

        toolBar2.add(urlField);
        toolBar2.add(goButton);

        // 创建带滚动条的编辑器面板
        // Replace the anonymous JEditorPane subclass with:
        editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        StyleSheet styleSheet = htmlKit.getStyleSheet();
        styleSheet.addRule("body { margin: 8px; line-height: 1.5; }");
        styleSheet.addRule("img { max-width: 100%; }");
        styleSheet.addRule("table { border-collapse: collapse; }");
        htmlKit.setStyleSheet(styleSheet);
        editorPane.setEditorKit(htmlKit);
        editorPane.setEditable(false);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true); // 改善HTML渲染
        JScrollPane scrollPane = new JScrollPane(editorPane);

        // 创建顶部容器面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolBar1, BorderLayout.NORTH);
        topPanel.add(toolBar2, BorderLayout.SOUTH);

        // 添加组件到窗口
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 设置滚动面板尺寸
        scrollPane.setPreferredSize(new Dimension(800, 500));
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件(F)");
        JMenuItem saveItem = new JMenuItem("另存为(A)");
        JMenuItem exitItem = new JMenuItem("退出(I)");
        
        // 设置快捷键
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        
        // 菜单项事件
        saveItem.addActionListener(e -> savePage());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        // 编辑菜单
        JMenu editMenu = new JMenu("编辑(E)");
        JMenuItem backItem = new JMenuItem("后退");
        JMenuItem forwardItem = new JMenuItem("前进");
        
        // 修复后退/前进快捷键冲突
        backItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        forwardItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        
        backItem.addActionListener(e -> goBack());
        forwardItem.addActionListener(e -> goForward());
        
        editMenu.add(backItem);
        editMenu.add(forwardItem);

        // 视图菜单
        JMenu viewMenu = new JMenu("视图(V)");
        JMenuItem fullscreenItem = new JMenuItem("全屏");
        JMenuItem viewSourceItem = new JMenuItem("查看源代码(C)");
        JMenuItem refreshItem = new JMenuItem("刷新");
        
        fullscreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));
        viewSourceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        
        fullscreenItem.addActionListener(e -> toggleFullscreen());
        viewSourceItem.addActionListener(e -> showSource());
        refreshItem.addActionListener(e -> refreshPage());
        
        viewMenu.add(fullscreenItem);
        viewMenu.add(viewSourceItem);
        viewMenu.add(refreshItem);

        // 在视图菜单中添加
        JMenuItem zoomInItem = new JMenuItem("放大");
        JMenuItem zoomOutItem = new JMenuItem("缩小");
        zoomInItem.addActionListener(e -> editorPane.setFont(editorPane.getFont().deriveFont(editorPane.getFont().getSize2D() + 2f)));
        zoomOutItem.addActionListener(e -> editorPane.setFont(editorPane.getFont().deriveFont(editorPane.getFont().getSize2D() - 2f)));
        viewMenu.add(zoomInItem);
        viewMenu.add(zoomOutItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }

    private void loadPage(String url) {
        if (url == null || url.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "URL地址不能为空");
            return;
        }
        final String finalUrl = url;
        SwingUtilities.invokeLater(() -> {
            editorPane.setText("加载中...");
            urlField.setEnabled(false);
        });

        new SwingWorker<Void, Void>() {
            private String processedUrl; // 声明为内部类成员变量

            @Override
            protected Void doInBackground() throws Exception {
                processedUrl = finalUrl; // 使用成员变量
                if (!processedUrl.matches("^https?://.*")) {
                    processedUrl = "https://" + processedUrl;  // 优先尝试HTTPS
                }
                // 修改此处使用processedUrl创建URI
                URL target = new URI(processedUrl).toURL();
                try (InputStream is = target.openStream()) {
                    if (is.available() <= 0) {
                        throw new IOException("服务器返回空响应");
                    }
                    // 读取响应头验证状态
                    if (target.openConnection() instanceof HttpURLConnection) {
                        HttpURLConnection conn = (HttpURLConnection) target.openConnection();
                        conn.setConnectTimeout(5000);  // 设置5秒连接超时
                        conn.setReadTimeout(10000);    // 设置10秒读取超时
                        if (conn.getResponseCode() != 200) {
                            throw new IOException("HTTP错误: " + conn.getResponseCode());
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    urlField.setEnabled(true);
                });
                try {
                    // 增加空值检查
                    if (processedUrl == null || processedUrl.isEmpty()) {
                        throw new MalformedURLException("无效的URL地址");
                    }
                    editorPane.setPage(new URI(processedUrl).toURL());
                    editorPane.revalidate();  // 新增重绘逻辑
                    editorPane.repaint();
                    if (currentUrl != null) {
                        backStack.push(currentUrl);
                    }
                    currentUrl = processedUrl;  // 改为使用处理后的URL
                    urlField.setText(processedUrl);  // 显示处理后的URL
                    forwardStack.clear();
                    updateNavigationButtons();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(WebBrowser.this, "加载错误: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void goBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentUrl);
            currentUrl = backStack.pop();
            loadPage(currentUrl);
        }
    }

    private void goForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentUrl);
            currentUrl = forwardStack.pop();
            loadPage(currentUrl);
        }
    }

    private void savePage() {
        JFileChooser fileChooser = new JFileChooser();
        // 添加文件类型过滤器
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("HTML 文件 (*.html)", "html"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        
        File selectedFile = null;
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            String ext = "";
            
            // 获取用户选择的文件类型
            javax.swing.filechooser.FileFilter filter = fileChooser.getFileFilter();
            if (filter instanceof FileNameExtensionFilter) {
                String[] exts = ((FileNameExtensionFilter) filter).getExtensions();
                ext = exts.length > 0 ? exts[0] : "";
            }
            
            // 改进后的扩展名处理逻辑
            String fileName = selectedFile.getName();
            String newFileName = fileName;
            
            // 分离文件名和扩展名
            int dotIndex = fileName.lastIndexOf('.');
            String baseName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
            String existingExt = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
            
            // 仅当用户未指定扩展名或扩展名不匹配时补充
            if (!existingExt.equalsIgnoreCase(ext) || (ext.isEmpty() && !fileName.endsWith(".html"))) {
                if (!ext.isEmpty()) {
                    newFileName = baseName + "." + ext;
                } else {
                    newFileName = baseName + ".html";
                }
            }
            
            selectedFile = new File(selectedFile.getParentFile(), newFileName);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(editorPane.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "保存失败: " + e.getMessage());
            }
        }
    

    private void showSource() {
        JDialog sourceDialog = new JDialog(this, "网页源代码");
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea sourceArea = new JTextArea(editorPane.getText());
        
        // 新增保存按钮
        JButton saveSourceBtn = new JButton("保存");
        saveSourceBtn.addActionListener(e -> saveSourceContent(sourceArea.getText()));
        
        panel.add(new JScrollPane(sourceArea), BorderLayout.CENTER);
        panel.add(saveSourceBtn, BorderLayout.SOUTH);
        
        sourceDialog.add(panel);
        sourceDialog.setSize(600, 400);
        sourceDialog.setVisible(true);
    }

    
// 新增保存源代码方法
    private void saveSourceContent(String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("HTML 文件 (*.html)", "html"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String ext = "";
            
            javax.swing.filechooser.FileFilter filter = fileChooser.getFileFilter();
            if (filter instanceof FileNameExtensionFilter) {
                String[] exts = ((FileNameExtensionFilter) filter).getExtensions();
                ext = exts.length > 0 ? exts[0] : "";
            }
            
            // 使用与网页保存相同的扩展名处理逻辑
            String fileName = selectedFile.getName();
            int dotIndex = fileName.lastIndexOf('.');
            String baseName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
            String existingExt = (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
            
            if (!existingExt.equalsIgnoreCase(ext) || (ext.isEmpty() && !fileName.endsWith(".html"))) {
                if (!ext.isEmpty()) {
                    fileName = baseName + "." + ext;
                } else {
                    fileName = baseName + ".html";
                }
                selectedFile = new File(selectedFile.getParentFile(), fileName);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(content);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
            }
        }
}

    private void toggleFullscreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            boolean isFullscreen = (gd.getFullScreenWindow() == null);
            gd.setFullScreenWindow(isFullscreen ? this : null);
            setTitle(isFullscreen ? "[全屏] Java 网页浏览器" : "Java 网页浏览器");
        }
    }

    private void refreshPage() {
        loadPage(currentUrl);
    }

    private void updateNavigationButtons() {
        backButton.setEnabled(!backStack.isEmpty());
        forwardButton.setEnabled(!forwardStack.isEmpty());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WebBrowser browser = new WebBrowser();
            browser.setVisible(true);
        });
    }
}