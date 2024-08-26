import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.editor.HttpRequestEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainUI extends JPanel  {

    Logging logging;
    private MontoyaApi api;
    private static HttpRequestEditor requestEditor;

    public static JTable payloadTable;
    public static JPanel textEditorFrame;
    public static JTextField textField;

    public MainUI(Logging logging, MontoyaApi api) {

        this.api = api;
        this.logging = logging;
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel payloadTableFrame = new JPanel(new BorderLayout());
        textEditorFrame = new JPanel(new BorderLayout());

        mainPanel.add(payloadTableFrame, BorderLayout.WEST);
        mainPanel.add(textEditorFrame, BorderLayout.CENTER);
        tabbedPane.addTab("One Time Flow Executor", mainPanel);

        // Create buttons variables.
        JPanel payloadTableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removePayloadsBtn = new JButton("Remove Payloads");
        JButton addPayloadsBtn = new JButton("Add Payloads");

        payloadTable = new JTable(new DefaultTableModel(new Object[]{"Payloads"}, 0));
        payloadTable.setDragEnabled(true);
        payloadTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        payloadTableFrame.add(new JScrollPane(payloadTable), BorderLayout.CENTER);
        payloadTableButtonPanel.add(addPayloadsBtn);
        payloadTableButtonPanel.add(removePayloadsBtn);
        payloadTableFrame.add(payloadTableButtonPanel, BorderLayout.SOUTH);

        initializeRequestEditor();
        JButton sendBtn = new JButton("Send");
        JLabel Endpointsss = new JLabel("Endpoint: ");
        textField = new JTextField(20);
        JPanel textEditorButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textEditorButtonPanel.add(Endpointsss);
        textEditorButtonPanel.add(textField);
        textEditorButtonPanel.add(sendBtn);
        textEditorFrame.add(textEditorButtonPanel, BorderLayout.SOUTH);

        this.add(tabbedPane, BorderLayout.CENTER);  // DO NOT REMOVE.

        removePayloadsBtn.addActionListener(e -> handleRemovePayloads());

        addPayloadsBtn.addActionListener(e -> handleAddPayloads());

        sendBtn.addActionListener(e -> handleSend());

    }

    public void initializeRequestEditor() {
        requestEditor = api.userInterface().createHttpRequestEditor();
        textEditorFrame.add(requestEditor.uiComponent());
    }

    public static HttpRequestEditor getRequestEditor() {
        return requestEditor;
    }

    private void handleSend() {
        // TODO: ADD LOGIC TO FORWARD REQUEST INSTEAD OF USING PROXY. REQUEST MUST INCLUDE THE PAYLOAD
        //  + ORIGINAL BODY CONTENT
    }

    private void handleAddPayloads() {
        JTextArea textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        int option = JOptionPane.showConfirmDialog(this, scrollPane,
                "Enter Endpoint (one per line)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            String input = textArea.getText().trim();
            if (!input.isEmpty()) {
                DefaultTableModel model1 = (DefaultTableModel) payloadTable.getModel();
                String[] endpoints = input.split("\\r?\\n");
                for (String endpoint : endpoints) {
                    endpoint = endpoint.trim();
                    if (!endpoint.isEmpty()) {
                        model1.addRow(new Object[]{endpoint});
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Endpoint cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void handleRemovePayloads() {
        DefaultTableModel model1 = (DefaultTableModel) payloadTable.getModel();

        int[] selectedRows = payloadTable.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                model1.removeRow(selectedRows[i]);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No rows selected to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}