import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.http.InterceptedRequest;
import burp.api.montoya.proxy.http.ProxyRequestHandler;
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction;
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Main implements BurpExtension, ProxyRequestHandler {

    MontoyaApi api;
    Logging logging;
    private String requestBodyWithMarker = "^^";
    private int currentPayloadIndex = 0;

    @Override
    public void initialize(MontoyaApi api) {

        this.api = api;
        api.extension().setName("One Time Flow Semi Automate");
        this.logging = api.logging();
        this.logging.logToOutput("Extension loaded!");
        api.proxy().registerRequestHandler(this);
        MainUI mainUI = new MainUI(api.logging(), api);
        api.userInterface().registerSuiteTab("One Time Flow Execute", mainUI);
        this.logging.logToOutput("Panels registered successfully.");
    }

    @Override
    public ProxyRequestReceivedAction handleRequestReceived(InterceptedRequest interceptedRequest) {
        if (interceptedRequest.path().equals(MainUI.textField.getText())) {
           return ProxyRequestReceivedAction.intercept(interceptedRequest);
        }
        return null;
    }

    @Override
    public ProxyRequestToBeSentAction handleRequestToBeSent(InterceptedRequest interceptedRequest) {
        DefaultTableModel model2 = (DefaultTableModel) MainUI.payloadTable.getModel();
        ArrayList<String> payloads = new ArrayList<>();
        for (int i = 0; i < model2.getRowCount(); i++) {
            String Payload = model2.getValueAt(i, 0).toString();
            try {
                String encodedPayload = URLEncoder.encode(Payload, "UTF-8");
                payloads.add(encodedPayload);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        String bodyRequest = interceptedRequest.bodyToString();
        if (bodyRequest.contains(requestBodyWithMarker)) {
            if (currentPayloadIndex < payloads.size()) {
                String currentPayload = payloads.get(currentPayloadIndex);
                bodyRequest = bodyRequest.replace(requestBodyWithMarker, currentPayload);
                HttpRequest modifiedRequest = interceptedRequest.withBody(bodyRequest);
                currentPayloadIndex++;
                checkCount(currentPayloadIndex == payloads.size());
                return ProxyRequestToBeSentAction.continueWith(modifiedRequest);
            }
        }
        return null;
    }

    public void checkCount(boolean condition) {
        if (condition) {
            JOptionPane.showMessageDialog(
                    null,
                    "The last payload has been sent.",
                    "Notification",
                    JOptionPane.INFORMATION_MESSAGE
            );
            currentPayloadIndex = 0;
        }
    }

}
