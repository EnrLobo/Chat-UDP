package br.edu.ifsuldeminas.sd.chat.client;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChatGUI extends JFrame implements MessageContainer {

    // Paleta de cores em tons de azul e estilo balão
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192);   // Azul Escuro (Bordas e Títulos)
    private static final Color BLUE_LIGHT = new Color(227, 242, 253);    // Azul Claro (Fundo das Configurações)
    private static final Color BUBBLE_ME = new Color(0, 132, 255);       // Azul do Balão "Eu" (Estilo Instagram)
    private static final Color BUBBLE_OTHER = new Color(240, 240, 240);  // Cinza do Balão "Outro"
    private static final Color TEXT_ME = Color.WHITE;
    private static final Color TEXT_OTHER = Color.BLACK;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Componentes da interface
    private JTextField txtLocalPort;
    private JTextField txtRemoteIP;
    private JTextField txtRemotePort;
    private JTextField txtUsername;

    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField txtMessage;
    private JButton btnSend;
    private JButton btnConnect;

    private Sender sender;

    public ChatGUI() {
        setTitle("Chat UDP - Balões com Painel Estilizado");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(550, 500));

        // Painel principal com espaçamento externo
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // --- PAINEL SUPERIOR: Configurações Estilizadas (Layout Clássico Azul) ---
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBackground(BLUE_LIGHT);

        // Linha 1: Dados de Rede
        JPanel networkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        networkPanel.setOpaque(false);
        networkPanel.add(createLabel("Porta Local:"));
        txtLocalPort = createTextField("8000", 5);
        networkPanel.add(txtLocalPort);

        networkPanel.add(createLabel("IP Remoto:"));
        txtRemoteIP = createTextField("localhost", 10);
        networkPanel.add(txtRemoteIP);

        networkPanel.add(createLabel("Porta Remota:"));
        txtRemotePort = createTextField("8001", 5);
        networkPanel.add(txtRemotePort);

        // Linha 2: Nome do Usuário e Botão Conectar
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        userPanel.setOpaque(false);
        userPanel.add(createLabel("Seu Nome:"));
        txtUsername = createTextField("Usuário", 12);
        userPanel.add(txtUsername);

        btnConnect = createStyledButton("Conectar", BLUE_PRIMARY);
        userPanel.add(btnConnect);

        topPanel.add(networkPanel);
        topPanel.add(userPanel);

        // Borda com título estilizado em azul
        TitledBorder configBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BLUE_PRIMARY, 1), "Configurações do Chat");
        configBorder.setTitleFont(MAIN_FONT.deriveFont(Font.BOLD));
        configBorder.setTitleColor(BLUE_PRIMARY);
        topPanel.setBorder(BorderFactory.createCompoundBorder(configBorder, new EmptyBorder(5, 5, 5, 5)));

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- CENTRO: Área de Mensagens em Balão ---
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);
        chatPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- PAINEL INFERIOR: Envio Moderno de Mensagens ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        txtMessage = new JTextField();
        txtMessage.setFont(MAIN_FONT);
        txtMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(219, 219, 219), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtMessage.setEnabled(false);

        btnSend = new JButton("Enviar");
        btnSend.setBackground(Color.WHITE);
        btnSend.setForeground(BUBBLE_ME);
        btnSend.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        btnSend.setBorder(null);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.setEnabled(false);

        bottomPanel.add(txtMessage, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Configuração das Ações ---
        btnConnect.addActionListener(e -> conectar());
        btnSend.addActionListener(e -> enviarMensagem());
        txtMessage.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) enviarMensagem();
            }
        });

        setLocationRelativeTo(null);
    }

    // Métodos auxiliares de estilização do topo
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        label.setForeground(BLUE_PRIMARY);
        return label;
    }

    private JTextField createTextField(String text, int columns) {
        JTextField textField = new JTextField(text, columns);
        textField.setFont(MAIN_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 4, 3, 4)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void conectar() {
        try {
            int localPort = Integer.parseInt(txtLocalPort.getText().trim());
            int remotePort = Integer.parseInt(txtRemotePort.getText().trim());
            String remoteIP = txtRemoteIP.getText().trim();
            String username = txtUsername.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Insira um nome válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            sender = ChatFactory.build(remoteIP, remotePort, localPort, this);

            // Desabilita campos de configuração
            txtLocalPort.setEnabled(false); txtRemoteIP.setEnabled(false);
            txtRemotePort.setEnabled(false); txtUsername.setEnabled(false);
            btnConnect.setEnabled(false);

            // Libera chat
            txtMessage.setEnabled(true); btnSend.setEnabled(true);
            txtMessage.requestFocus();

            addSystemMessage("Conectado com sucesso. Escutando a porta: " + localPort);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage());
        }
    }

    private void enviarMensagem() {
        String msg = txtMessage.getText().trim();
        String from = txtUsername.getText().trim();

        if (!msg.isEmpty() && sender != null) {
            try {
                sender.send(msg + MessageContainer.FROM + from);
                addBubble(msg, true, ""); // Adiciona o seu balão na direita
                txtMessage.setText("");
            } catch (ChatException ex) {
                JOptionPane.showMessageDialog(this, "Erro no envio: " + ex.getMessage());
            }
        }
    }

    @Override
    public void newMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message != null && !message.trim().isEmpty()) {
                // Limpeza estrita contra caracteres fantasmas (\u0000) oriundos do buffer fixo
                String cleanMessage = message.trim();
                String[] parts = cleanMessage.split(MessageContainer.FROM);

                if (parts.length >= 2) {
                    addBubble(parts[0].trim(), false, parts[1].trim()); // Balão remoto na esquerda
                } else {
                    addSystemMessage(cleanMessage);
                }
            }
        });
    }

    // --- RENDERIZAÇÃO DOS BALÕES E MENSAGENS DO SISTEMA ---

    private void addSystemMessage(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setForeground(Color.GRAY);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(Color.WHITE);
        wrapper.add(lbl);

        appendPanel(wrapper);
    }

    private void addBubble(String text, boolean isMe, String senderName) {
        JPanel wrapper = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT));
        wrapper.setBackground(Color.WHITE);

        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(isMe ? BUBBLE_ME : BUBBLE_OTHER);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2d.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(8, 14, 8, 14));

        // Formatação HTML estruturada para limitar largura máxima do texto do balão
        String htmlText = "<html><body style='width: max-content; max-width: 250px;'>";
        if (!isMe && !senderName.isEmpty()) {
            htmlText += "<b style='color: #777777; font-size: 9px;'>" + senderName + "</b><br>";
        }
        htmlText += text.replace("\n", "<br>") + "</body></html>";

        JLabel lblText = new JLabel(htmlText);
        lblText.setFont(MAIN_FONT);
        lblText.setForeground(isMe ? TEXT_ME : TEXT_OTHER);

        bubble.add(lblText, BorderLayout.CENTER);
        wrapper.add(bubble);
        appendPanel(wrapper);
    }

    private void appendPanel(JPanel panel) {
        chatPanel.add(panel);
        chatPanel.add(Box.createRigidArea(new Dimension(0, 4))); // Pequeno espaço vertical constante entre balões
        chatPanel.revalidate();
        chatPanel.repaint();

        // Move a barra de rolagem automaticamente para acompanhar as últimas mensagens
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new ChatGUI().setVisible(true));
    }
}