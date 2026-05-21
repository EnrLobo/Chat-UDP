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

    // Definição da paleta de cores em tons de azul
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192);   // Azul Escuro (Botões e Títulos)
    private static final Color BLUE_LIGHT = new Color(227, 242, 253);    // Azul Claro (Fundos secundários)
    private static final Color BLUE_ACCENT = new Color(33, 150, 243);    // Azul Vibrante (Destaques)
    private static final Color WHITE = Color.WHITE;
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Componentes da interface
    private JTextField txtLocalPort;
    private JTextField txtRemoteIP;
    private JTextField txtRemotePort;
    private JTextField txtUsername; // Novo campo para o nome do usuário

    private JTextArea areaMessages;
    private JTextField txtMessage;
    private JButton btnSend;
    private JButton btnConnect;

    private Sender sender;

    public ChatGUI() {
        setTitle("Chat UDP - Interface Refinada");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(550, 400));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BLUE_LIGHT);
        setContentPane(mainPanel);

        // Configurações de Conexão e Usuário ---
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

        userPanel.add(createLabel("Seu Nome/Apelido:"));
        txtUsername = createTextField("Usuário", 15);
        userPanel.add(txtUsername);

        btnConnect = createStyledButton("Conectar", BLUE_PRIMARY);
        userPanel.add(btnConnect);

        topPanel.add(networkPanel);
        topPanel.add(userPanel);

        TitledBorder configBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BLUE_PRIMARY, 1), "Configurações do Chat");
        configBorder.setTitleFont(MAIN_FONT.deriveFont(Font.BOLD));
        configBorder.setTitleColor(BLUE_PRIMARY);
        topPanel.setBorder(BorderFactory.createCompoundBorder(configBorder, new EmptyBorder(5, 5, 5, 5)));

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Área de Mensagens ---
        areaMessages = new JTextArea();
        areaMessages.setEditable(false);
        areaMessages.setFont(MAIN_FONT);
        areaMessages.setLineWrap(true);
        areaMessages.setWrapStyleWord(true);
        areaMessages.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(areaMessages);
        scrollPane.setBorder(BorderFactory.createLineBorder(BLUE_PRIMARY, 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Envio de Mensagens ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        txtMessage = createTextField("", 0);
        txtMessage.setEnabled(false);

        btnSend = createStyledButton("Enviar", BLUE_ACCENT);
        btnSend.setEnabled(false);

        bottomPanel.add(txtMessage, BorderLayout.CENTER);
        bottomPanel.add(btnSend, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- Configuração das Ações ---
        btnConnect.addActionListener(e -> conectar());
        btnSend.addActionListener(e -> enviarMensagem());

        // Evento de teclar 'enter' no campo de escrita
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensagem();
                }
            }
        });

        setLocationRelativeTo(null); // Centraliza a janela na tela
    }

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
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return textField;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        button.setBackground(bg);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        return button;
    }

    private void conectar() {
        try {
            int localPort = Integer.parseInt(txtLocalPort.getText().trim());
            int remotePort = Integer.parseInt(txtRemotePort.getText().trim());
            String remoteIP = txtRemoteIP.getText().trim();
            String username = txtUsername.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, defina um nome antes de conectar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            sender = ChatFactory.build(remoteIP, remotePort, localPort, this);

            areaMessages.append("--- Sistema: Conectado como [" + username + "]. Escutando na porta " + localPort + " ---\n");

            txtLocalPort.setEnabled(false);
            txtRemoteIP.setEnabled(false);
            txtRemotePort.setEnabled(false);
            txtUsername.setEnabled(false);
            btnConnect.setEnabled(false);

            txtMessage.setEnabled(true);
            btnSend.setEnabled(true);
            txtMessage.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "As portas devem ser números inteiros.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (ChatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarMensagem() {
        String msg = txtMessage.getText().trim();
        String from = txtUsername.getText().trim();

        if (!msg.isEmpty() && sender != null) {
            try {
                String formattedMsg = msg + MessageContainer.FROM + from;

                sender.send(formattedMsg);

                areaMessages.append("Você: " + msg + "\n");
                txtMessage.setText("");

            } catch (ChatException ex) {
                JOptionPane.showMessageDialog(this, "Erro no envio: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void newMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message != null && !message.isEmpty()) {
                // O trim() limpa os caracteres nulos (\u0000) que sobram no buffer de 1000 bytes
                String cleanMessage = message.trim();

                String[] parts = cleanMessage.split(MessageContainer.FROM);
                if (parts.length >= 2) {
                    // Aplica o trim() também nas partes separadas para garantir
                    areaMessages.append(parts[1].trim() + "> " + parts[0].trim() + "\n");
                } else {
                    areaMessages.append("Remoto: " + cleanMessage + "\n");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatGUI().setVisible(true);
        });
    }
}