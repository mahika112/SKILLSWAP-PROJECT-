package app;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import javax.swing.*;
import model.Message;
import service.MessageService;

public class ChatClient extends JFrame implements ActionListener {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;

    JTextArea chatArea;
    JTextField inputField;
    JButton sendButton;

    String username;
    String receiver = "OtherUser"; // Temporary - You can dynamically set this

    public ChatClient(String username, String receiver) {
        this.username = username;
        this.receiver = receiver;

        setTitle("Chat - " + username);
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        connectToServer();
        new Thread(this::listenForMessages).start();

        setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 6002);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(username); // Send name to server
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Server connection failed!");
            System.exit(0);
        }
    }

    private void listenForMessages() {
        String msg;
        try {
            while ((msg = dis.readUTF()) != null) {
                chatArea.append(msg + "\n");
            }
        } catch (IOException e) {
            chatArea.append("Connection closed.\n");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String formatted = username + ": " + text;
            try {
                dos.writeUTF(formatted);
                dos.flush();
                chatArea.append(formatted + "\n");

                // Save message to DB
                MessageService msgService = new MessageService();
                Message messageObj = new Message(username, receiver, text, LocalDateTime.now());
                msgService.saveMessage(messageObj);
            } catch (IOException ex) {
                chatArea.append("Failed to send message.\n");
            }
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        String user;
        if (args.length > 0) {
            user = args[0];
        } else {
            user = JOptionPane.showInputDialog("Enter your name:");
        }
        if (user != null && !user.trim().isEmpty()) {
            new ChatClient(user.trim(), "OtherUser");
        }
    }

}
