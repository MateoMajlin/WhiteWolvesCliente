package winterwolves.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import winterwolves.Partida;
import winterwolves.elementos.Texto;
import winterwolves.items.AmuletoCuracion;
import winterwolves.items.EspadaItem;
import winterwolves.items.GemaElectrica;
import winterwolves.network.ClientThread;
import winterwolves.network.GameController;
import winterwolves.props.Caja;
import winterwolves.props.Cofre;
import winterwolves.props.CofreHud;
import winterwolves.utilidades.CameraManager;
import winterwolves.utilidades.Config;
import winterwolves.utilidades.PlayerManager;
import winterwolves.utilidades.Render;
import winterwolves.utilidades.Recursos;
import winterwolves.utilidades.Box2DColisiones;
import winterwolves.utilidades.CollisionListener;

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
    private int[] personajesElegidosIdx;

    public ClientThread clientThread;
    public int numPlayer = 0;
    public final int NUM_PLAYERS = 2;

    public MapaNieve(int[] personajesElegidosIdx) {
        this.personajesElegidosIdx = personajesElegidosIdx;
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

        cameraManager = new CameraManager(Config.WIDTH, Config.HEIGTH, PPM);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionListener());
        Box2DColisiones.crearCuerposColisiones(mapa, world, "Colisiones", PPM, 2f, 2f);
        debugRenderer = new Box2DDebugRenderer();

        clientThread = new ClientThread(this);
        clientThread.start();
        clientThread.sendMessage("Connect");

        playerManager = new PlayerManager(world, personajesElegidosIdx, PPM, cameraManager.getHud());
        for (int i = 0; i < NUM_PLAYERS; i++) {
            playerManager.getJugador(i).getPersonaje().entradas = playerManager.getJugador(i).getEntradas();
        }

        playerManager.getJugador(0).getPersonaje().setVida(50);

        partida = new Partida(
            playerManager.getJugador(0).getNombre(), playerManager.getJugador(0).getPersonaje(),
            playerManager.getJugador(1).getNombre(), playerManager.getJugador(1).getPersonaje(),
            120f
        );

        Gdx.input.setInputProcessor(playerManager.getJugador(0).getEntradas());

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
        ganaste.setPosition(centroMapaX - ganaste.getAncho() / 2f,
            centroMapaY + ganaste.getAlto() / 2f);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(1, 1, 1);
        world.step(delta, 6, 2);

        // Actualizar cajas
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

        cameraManager.seguir(playerManager.getPosicionJugador(0));

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

        // Interacciones
        if (!partida.isPartidaFinalizada()) {
            // Intercambiar items
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                playerManager.getJugador(0).getPersonaje().intercambiarItems(new AmuletoCuracion(), 1);
            }

            // Abrir cofre
            if (cofre.estaCerca(playerManager.getPosicionJugador(0), 50)
                && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                if (hudCofre == null)
                    hudCofre = new CofreHud(cofre.getInventario(), playerManager.getJugador(0).getPersonaje(), cameraManager.getHud());
                hudCofre.toggle();
            }

            // Abrir inventario
            if (Gdx.input.isKeyJustPressed(Input.Keys.I) && (hudCofre == null || !hudCofre.isVisible())) {
                playerManager.getJugador(0).toggleInventario();
            }
        }

        // Dibujar HUD
        if (hudCofre != null && hudCofre.isVisible()) {
            hudCofre.actualizar();
            hudCofre.dibujar(Render.batch);
        } else {
            playerManager.drawHud(Render.batch);
        }

        // HUD partida
        Render.batch.setProjectionMatrix(cameraManager.getHud().combined);
        Render.batch.begin();
        partida.dibujarHUD();
        Render.batch.end();

        debugRenderer.render(world, cameraManager.getBox2D().combined);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Render.app.setScreen(new Menu());
            musica.stop();
            Recursos.musica.play();
            Recursos.musica.setVolume(0.3f);
            Recursos.musica.setLooping(true);
            dispose();
        }

        if (partida.isPartidaFinalizada()) {
            playerManager.setPuedeMoverse(false);
        }
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        mapa.dispose();
        renderer.dispose();
        playerManager.dispose();
        world.dispose();
        debugRenderer.dispose();
        for (Caja c : cajas) {
            c.dispose();
        }
    }

    @Override
    public void connect(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    @Override
    public void start() {

    }
}
