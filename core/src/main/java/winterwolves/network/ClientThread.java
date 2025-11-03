package winterwolves.network;

import java.io.IOException;
import java.net.*;
import com.badlogic.gdx.Gdx;

public class ClientThread extends Thread {
    private DatagramSocket socket;
    private int serverPort = 5555;
    private String ipServerStr = "255.255.255.255"; // broadcast o IP fija de tu server
    private InetAddress ipServer;
    private boolean end = false;
    private GameController gameController;

    public ClientThread(GameController gameController) {
        try {
            this.gameController = gameController;
            ipServer = InetAddress.getByName(ipServerStr);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(packet);
                processMessage(packet);
            } catch (IOException e) {
                if (!end) e.printStackTrace();
            }
        } while (!end);
    }

    private void processMessage(DatagramPacket packet) {
        String message = (new String(packet.getData())).trim();
        String[] parts = message.split(":");

        System.out.println("Mensaje recibido del servidor: " + message);

        switch (parts[0]) {
            case "AlreadyConnected":
                System.out.println("Ya estás conectado");
                break;

            case "Full":
                System.out.println("Servidor lleno");
                this.end = true;
                break;

            case "Connected":
                this.ipServer = packet.getAddress();
                int playerNum = Integer.parseInt(parts[1]);
                gameController.connect(playerNum); // Asigna numPlayer antes de Start
                break;

            case "Start":
                if (parts.length > 1) {
                    String[] pjs = parts[1].split(",");
                    int[] personajesElegidos = new int[pjs.length];
                    for (int i = 0; i < pjs.length; i++) {
                        personajesElegidos[i] = Integer.parseInt(pjs[i]);
                    }

                    System.out.println("Iniciando partida con personajes: " + parts[1]);

                    // Ejecutar en hilo principal
                    Gdx.app.postRunnable(() -> {
                        gameController.start(personajesElegidos);
                    });
                }
                break;

        }
    }

    public void sendMessage(String message) {
        byte[] byteMessage = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServer, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void terminate() {
        this.end = true;
        socket.close();
        this.interrupt();
    }
}
