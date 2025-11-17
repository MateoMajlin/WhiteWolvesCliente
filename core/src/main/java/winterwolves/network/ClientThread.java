package winterwolves.network;

import java.io.IOException;
import java.net.*;
import com.badlogic.gdx.Gdx;
import winterwolves.Jugador;
import winterwolves.personajes.Personaje;

public class ClientThread extends Thread {

    private DatagramSocket socket;
    private int serverPort = 5555;
    private String ipServerStr = "255.255.255.255";
    private InetAddress ipServer;
    private boolean end = false;
    private GameController gameController;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getIpServerStr() {
        return ipServerStr;
    }

    public void setIpServerStr(String ipServerStr) {
        this.ipServerStr = ipServerStr;
    }

    public InetAddress getIpServer() {
        return ipServer;
    }

    public void setIpServer(InetAddress ipServer) {
        this.ipServer = ipServer;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public ClientThread(GameController gameController) {
        try {
            this.gameController = gameController;
            ipServer = InetAddress.getByName(ipServerStr);
            socket = new DatagramSocket();
            socket.setSoTimeout(0); // Sin timeout, escucha indefinidamente
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException("Error al iniciar el cliente UDP", e);
        }
    }

    @Override //Sobre escribe el metodo run de la clase abstracta
    public void run() {
        System.out.println("[Cliente] Escuchando mensajes UDP...");
        do {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(packet);
                processMessage(packet);
            } catch (SocketException e) {
                if (!end) e.printStackTrace();
            } catch (IOException e) {
                if (!end) e.printStackTrace();
            }

        } while (!end);
    }

    private void processMessage(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength()).trim();
        String[] parts = message.split(":");

        System.out.println("[Cliente] Mensaje recibido: " + message);

        switch (parts[0]) {

            case "AlreadyConnected":
                System.out.println("[Cliente] Ya estás conectado al servidor.");
                break;

            case "Full":
                System.out.println("[Cliente] El servidor está lleno. Cerrando cliente.");
                this.end = true;
                break;

            case "Connected":
                this.ipServer = packet.getAddress();
                int playerNum = Integer.parseInt(parts[1]);
                Gdx.app.postRunnable(() -> gameController.connect(playerNum));
                break;

            case "Start":

                if (parts.length > 1) {
                    String[] pjs = parts[1].split(",");
                    int[] personajesElegidos = new int[pjs.length];
                    for (int i = 0; i < pjs.length; i++) {
                        personajesElegidos[i] = Integer.parseInt(pjs[i]);
                    }

                    System.out.println("[Cliente] Iniciando partida con personajes: " + parts[1]);
                    Gdx.app.postRunnable(() -> gameController.start(personajesElegidos));
                }
                break;

            case "MOVE":
//                if (parts.length < 4) return;
//
//                String jugador = parts[1];
//                float x = Float.parseFloat(parts[2]);
//                float y = Float.parseFloat(parts[3]);
//
//                System.out.println("Moviendo " + jugador + " a (" + x + ", " + y + ")");
//
//                Gdx.app.postRunnable(() -> {
//                    Jugador j = gameController.getPlayerManager().getJugador(Integer.parseInt(jugador));
//                    if (j != null && j.getPersonaje() != null) {
////                        j.getPersonaje().moverVisualSegunServidor(x, y);
//                    } else {
//                        System.out.println("[Cliente] No se encontró personaje para " + jugador);
//                    }
//                });
                break;

        }
    }

    public void sendMessage(String message) {
        if (end) return;
        try {
            byte[] byteMessage = message.getBytes();
            DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length, ipServer, serverPort);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("[Cliente] Error al enviar mensaje: " + message);
            e.printStackTrace();
        }
    }

    public void terminate() {
        this.end = true;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.interrupt();
        System.out.println("[Cliente] Hilo de cliente terminado.");
    }
}
