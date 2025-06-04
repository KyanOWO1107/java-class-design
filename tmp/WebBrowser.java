import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.Stack;

public class WebBrowser extends JFrame {
    // 组件声明
    private JEditorPane editorPane;
    private JTextField addressBar;
    private Stack<String> backStack = new Stack<>();
    private Stack<String> forwardStack = new Stack<>();
    private String currentUrl;

    public WebBrowser() {
        initUI();
    }

    private void initUI() {
        setTitle("Java网页浏览器");
        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // 创建菜单栏
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // 创建工具栏
        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
        
        // 初始化编辑器面板
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                loadPage(e.getURL().toString());
            }
        });
        add(new JScrollPane(editorPane), BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 文件菜单
        JMenu fileMenu = new JMenu("文件(F)");
        fileMenu.setMnemonic('F');
        
        JMenuItem saveItem = new JMenuItem("另存为(A)");
        saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveItem.addActionListener(e -> savePage());
        
        JMenuItem exitItem = new JMenuItem("退出(I)");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 编辑菜单
        JMenu editMenu = new JMenu("编辑(E)");
        editMenu.setMnemonic('E');
        
        JMenuItem backItem = new JMenuItem("后退(Z)");
        backItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Z"));
        backItem.addActionListener(e -> goBack());
        
        JMenuItem forwardItem = new JMenuItem("前进(D)");
        forwardItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        forwardItem.addActionListener(e -> goForward());
        
        editMenu.add(backItem);
        editMenu.add(forwardItem);
        
        // 视图菜单
        JMenu viewMenu = new JMenu("视图(V)");
        viewMenu.setMnemonic('V');
        
        JMenuItem fullscreenItem = new JMenuItem("全屏(U)");
        fullscreenItem.setAccelerator(KeyStroke.getKeyStroke("ctrl U"));
        fullscreenItem.addActionListener(e -> toggleFullscreen());
        
        JMenuItem sourceItem = new JMenuItem("查看源代码(C)");
        sourceItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
        sourceItem.addActionListener(e -> showSourceCode());
        
        JMenuItem refreshItem = new JMenuItem("刷新(R)");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        refreshItem.addActionListener(e -> refreshPage());
        
        viewMenu.add(fullscreenItem);
        viewMenu.addSeparator();
        viewMenu.add(sourceItem);
        viewMenu.add(refreshItem);
        
        // 组装菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        
        // 功能按钮
        JButton saveButton = new JButton("另存为");
        saveButton.addActionListener(e -> savePage());
        
        JButton backButton = new JButton("后退");
        backButton.addActionListener(e -> goBack());
        
        JButton forwardButton = new JButton("前进");
        forwardButton.addActionListener(e -> goForward());
        
        JButton sourceButton = new JButton("源代码");
        sourceButton.addActionListener(e -> showSourceCode());
        
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> System.exit(0));
        
        // 地址栏
        addressBar = new JTextField(30);
        addressBar.addActionListener(e -> loadPage(addressBar.getText()));
        
        JButton goButton = new JButton("转到");
        goButton.addActionListener(e -> loadPage(addressBar.getText()));
        
        // 组装工具栏
        toolBar.add(saveButton);
        toolBar.addSeparator();
        toolBar.add(backButton);
        toolBar.add(forwardButton);
        toolBar.addSeparator();
        toolBar.add(sourceButton);
        toolBar.addSeparator();
        toolBar.add(exitButton);
        toolBar.addSeparator();
        toolBar.add(new JLabel("地址："));
        toolBar.add(addressBar);
        toolBar.add(goButton);
        
        return toolBar;
    }

    private void loadPage(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            
            backStack.push(currentUrl);
            forwardStack.clear();
            
            editorPane.setPage(url);
            currentUrl = url;
            addressBar.setText(url);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "无法加载页面：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        if (!backStack.isEmpty()) {
            forwardStack.push(currentUrl);
            String url = backStack.pop();
            loadUrlSilently(url);
        }
    }

    private void goForward() {
        if (!forwardStack.isEmpty()) {
            backStack.push(currentUrl);
            String url = forwardStack.pop();
            loadUrlSilently(url);
        }
    }

    private void loadUrlSilently(String url) {
        try {
            editorPane.setPage(url);
            currentUrl = url;
            addressBar.setText(url);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "无法加载页面：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void savePage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存网页");
        fileChooser.setSelectedFile(new File("page.html"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(editorPane.getText());
                JOptionPane.showMessageDialog(this, "保存成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败：" + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSourceCode() {
        JDialog dialog = new JDialog(this, "网页源代码", true);
        dialog.setSize(800, 600);
        
        JTextArea sourceArea = new JTextArea(editorPane.getText());
        sourceArea.setEditable(false);
        sourceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(sourceArea);
        dialog.add(scrollPane);
        
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveSourceCode(sourceArea.getText()));
        buttonPanel.add(saveButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void saveSourceCode(String code) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存源代码");
        fileChooser.setSelectedFile(new File("source.html"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(code);
                JOptionPane.showMessageDialog(this, "保存成功！");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "保存失败：" + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshPage() {
        if (currentUrl != null) {
            loadUrlSilently(currentUrl);
        }
    }

    private void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        
        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(device.isFullScreenSupported() ? this : null);
        } else {
            JOptionPane.showMessageDialog(this, "当前设备不支持全屏模式");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            
            WebBrowser browser = new WebBrowser();
            browser.setVisible(true);
        });
    }
}