package winterwolves.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import winterwolves.Jugador;
import winterwolves.Partida;
import winterwolves.elementos.Texto;
import winterwolves.items.*;
import winterwolves.network.ClientThread;
import winterwolves.network.GameController;
import winterwolves.props.*;
import winterwolves.utilidades.*;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class MapaNieve implements Screen, GameController {

    private TiledMap mapa;
    private OrthogonalTiledMapRenderer renderer;
    private CameraManager cameraManager;
    private PlayerManager playerManager;
    private Music musica = Recursos.musicaBatalla;

    private int[] capasFondo = {0, 1};
    private int[] capasDelanteras = {3};

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Array<Caja> cajas;
    private Cofre cofre;
    private CofreHud hudCofre;

    private final float PPM = 100f;
    private int contCajasDestruidas = 0;
    private int totalCajas;
    private Texto ganaste;

    private Partida partida;
    private int personaje;

    public ClientThread clientThread;
    public int numPlayer = -1;
    public final int NUM_PLAYERS = 2;

    private Jugador otroJugador;

    public MapaNieve(int personaje) {
        this.personaje = personaje;
    }

    @Override
    public void show() {
        TmxMapLoader loader = new TmxMapLoader();
        mapa = loader.load("mapas/mapaNieve.tmx");

        int mapWidth = mapa.getProperties().get("width", Integer.class)
            * mapa.getProperties().get("tilewidth", Integer.class);
        int mapHeight = mapa.getProperties().get("height", Integer.class)
            * mapa.getProperties().get("tileheight", Integer.class);
        float centroMapaX = mapWidth / 2f;
        float centroMapaY = mapHeight / 2f;

        renderer = new OrthogonalTiledMapRenderer(mapa, 1f);
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        Box2DColisiones.crearCuerposColisiones(mapa, world, "Colisiones", PPM, 2f, 2f);
        debugRenderer = new Box2DDebugRenderer();

        cameraManager = new CameraManager(Config.WIDTH, Config.HEIGTH, PPM);

        clientThread = new ClientThread(this);
        clientThread.start();
        clientThread.sendMessage("Connect:" + personaje);

        cajas = new Array<>();
        cajas.add(new Caja(world, 500 / PPM, 700 / PPM, PPM, 100));
        cajas.add(new Caja(world, 800 / PPM, 600 / PPM, PPM, 100));
        cajas.add(new Caja(world, 1000 / PPM, 500 / PPM, PPM, 125));
        cajas.add(new Caja(world, 1200 / PPM, 400 / PPM, PPM, 60));
        totalCajas = cajas.size;

        cofre = new Cofre(world, 500 / PPM, 500 / PPM, PPM);
        cofre.getInventario().agregarItem(new EspadaItem());
        cofre.getInventario().agregarItem(new AmuletoCuracion());
        cofre.getInventario().agregarItem(new GemaElectrica());

        ganaste = new Texto(Recursos.FUENTEMENU, 150, Color.BLACK, true);
        ganaste.setTexto("Ganaste");
        ganaste.setPosition(centroMapaX - ganaste.getAncho() / 2f, centroMapaY + ganaste.getAlto() / 2f);
    }

    @Override
    public void connect(int numPlayer) {
        this.numPlayer = numPlayer;
        System.out.println("Jugador local asignado: " + numPlayer);

        if (playerManager != null) {
            Jugador jugadorLocal = playerManager.getJugador(numPlayer);
            if (jugadorLocal != null) {
                Gdx.input.setInputProcessor(jugadorLocal.getEntradas());
            }
        }
    }

    @Override
    public void start(int[] personajesElegidos) {
        System.out.println("Creando PlayerManager con personajes: " + personajesElegidos[0] + ", " + personajesElegidos[1]);

        playerManager = new PlayerManager(world, personajesElegidos, PPM, cameraManager.getHud());

        if (numPlayer >= 1) {
            Jugador jugadorLocal = playerManager.getJugador(numPlayer);
            if (jugadorLocal != null) {
                jugadorLocal.getPersonaje().entradas = jugadorLocal.getEntradas();
                Gdx.input.setInputProcessor(jugadorLocal.getEntradas());
                jugadorLocal.setClientThread(clientThread);
                jugadorLocal.setEsLocal(true);
            }
        }

        int otroIdx = (numPlayer == 1) ? 2 : 1;
        otroJugador = playerManager.getJugador(otroIdx);

        partida = new Partida(
            playerManager.getJugador(1).getNombre(),
            playerManager.getJugador(1).getPersonaje(),
            playerManager.getJugador(2).getNombre(),
            playerManager.getJugador(2).getPersonaje(),
            120f
        );
    }

    @Override
    public PlayerManager getPlayerManager() {
        return playerManager;
    }


    @Override
    public void render(float delta) {
        if (playerManager == null) return;

        Render.limpiarPantalla(1, 1, 1);
        world.step(delta, 6, 2);

        update();
        for (int i = cajas.size - 1; i >= 0; i--) {
            Caja c = cajas.get(i);
            if (c.isMarcadaParaDestruir()) {
                contCajasDestruidas++;
                c.eliminarDelMundo();
                cajas.removeIndex(i);
            }
        }

        partida.actualizar(delta);
        playerManager.actualizar(delta);
        cameraManager.seguir(playerManager.getPosicionJugador(numPlayer));

        renderer.setView(cameraManager.getPrincipal());
        renderer.render(capasFondo);

        Render.batch.setProjectionMatrix(cameraManager.getPrincipal().combined);
        Render.batch.begin();
        playerManager.draw(Render.batch);
        for (Caja c : cajas) {
            c.actualizar(delta);
            c.draw(Render.batch);
            c.drawVidaTexto(Render.batch);
        }
        if (contCajasDestruidas == totalCajas) ganaste.dibujar();
        cofre.draw(Render.batch);
        Render.batch.end();

        renderer.render(capasDelanteras);

        // HUD
        if (hudCofre != null && hudCofre.isVisible()) {
            hudCofre.actualizar();
            hudCofre.dibujar(Render.batch);
        } else {
            playerManager.drawHud(Render.batch);
        }

        Render.batch.setProjectionMatrix(cameraManager.getHud().combined);
        Render.batch.begin();
        partida.dibujarHUD();
        Render.batch.end();

        debugRenderer.render(world, cameraManager.getBox2D().combined);

    }

    public void update() {
        Jugador jugadorLocal = playerManager.getJugador(numPlayer);
        String dir = " ";

        if (jugadorLocal.getEntradas().isArriba()) dir = "ARRIBA";
        else if (jugadorLocal.getEntradas().isAbajo()) dir = "ABAJO";
        else if (jugadorLocal.getEntradas().isIzquierda()) dir = "IZQUIERDA";
        else if (jugadorLocal.getEntradas().isDerecha()) dir = "DERECHA";
        else {dir = "QUIETO";}

        if (!dir.isBlank()) {
            String message = "MOVE:" + dir + ":" + numPlayer;
            clientThread.sendMessage(message);
        }
    }

    public void moverVisualSegunServidor(float x, float y) {
        playerManager.getJugador(1).getPersonaje().setPosition(x, y);
        System.out.println("Sprite de red movido a: (" + x + ", " + y + ")");
    }

    @Override
    public void resize(int width, int height) { cameraManager.resize(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        mapa.dispose();
        renderer.dispose();
        if (playerManager != null) playerManager.dispose();
        world.dispose();
        debugRenderer.dispose();
        for (Caja c : cajas) c.dispose();
        if (clientThread != null) clientThread.terminate();
    }
}
