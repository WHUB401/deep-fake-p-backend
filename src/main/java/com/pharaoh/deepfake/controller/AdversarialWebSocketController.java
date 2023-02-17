package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.TailfLogThread;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.InputStream;

import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;

@ServerEndpoint("/AdversarialDetectionLog")
@RestController
public class AdversarialWebSocketController {

    private Process process;
    private InputStream inputStream;

    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(Session session) {
        try {
            String commond[] = {"tail", "-f", "storage/pycommond/adversarial_detection.temp"};
            process = Runtime.getRuntime().exec(commond);
            inputStream = process.getInputStream();
            TailfLogThread thread = new TailfLogThread(inputStream, session);
            thread.start();
        } catch (IOException e) {
            myLogger.error("AdversarialDetection Error!",e);
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {
        try {
            if (inputStream != null)
                inputStream.close();
        } catch (Exception e) {
            myLogger.error("AdversarialDetection Error!",e);
        }
        if (process != null)
            process.destroy();
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }
}
