package winterwolves.personajes.habilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.*;

import winterwolves.personajes.Personaje;

public class RayoElectrico extends Habilidad {

    private final float velocidad = 300f;
    private final int daño;
    private final List<ProyectilRayo> proyectiles = new ArrayList<>();
    private final Texture textura;
    private final Animation<TextureRegion> animacionRayo;

    private static final Map<DireccionUtil.Direccion, Vector2> startOffsets = new HashMap<>();
    static {
        startOffsets.put(DireccionUtil.Direccion.UP,         new Vector2(0, 40));
        startOffsets.put(DireccionUtil.Direccion.DOWN,       new Vector2(0, -40));
        startOffsets.put(DireccionUtil.Direccion.LEFT,       new Vector2(-40, 0));
        startOffsets.put(DireccionUtil.Direccion.RIGHT,      new Vector2(40, 0));

        startOffsets.put(DireccionUtil.Direccion.UP_LEFT,    new Vector2(-28, 28));
        startOffsets.put(DireccionUtil.Direccion.UP_RIGHT,   new Vector2(28, 28));
        startOffsets.put(DireccionUtil.Direccion.DOWN_LEFT,  new Vector2(-28, -28));
        startOffsets.put(DireccionUtil.Direccion.DOWN_RIGHT, new Vector2(28, -28));
    }

    private static final Map<DireccionUtil.Direccion, Vector2> hitboxOffsets = new HashMap<>();
    static {
        hitboxOffsets.put(DireccionUtil.Direccion.UP, new Vector2(0, 70));
        hitboxOffsets.put(DireccionUtil.Direccion.DOWN, new Vector2(0, -70));
        hitboxOffsets.put(DireccionUtil.Direccion.LEFT, new Vector2(-70, 0));
        hitboxOffsets.put(DireccionUtil.Direccion.RIGHT, new Vector2(70, 0));
        hitboxOffsets.put(DireccionUtil.Direccion.UP_LEFT, new Vector2(-50, 50));
        hitboxOffsets.put(DireccionUtil.Direccion.UP_RIGHT, new Vector2(50, 50));
        hitboxOffsets.put(DireccionUtil.Direccion.DOWN_LEFT, new Vector2(-50, -50));
        hitboxOffsets.put(DireccionUtil.Direccion.DOWN_RIGHT, new Vector2(50, -50));
    }


    public RayoElectrico(float duracion, float cooldown, int daño) {
        super(duracion, cooldown);
        this.daño = daño;

        textura = new Texture("rayo.png"); // 1024x128
        int anchoFrame = 256;
        int altoFrame = 128;

        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new TextureRegion(textura, i * anchoFrame, 0, anchoFrame, altoFrame);
        }

        animacionRayo = new Animation<>(0.06f, frames);
    }

    @Override
    protected void iniciarEfecto() {

        // Inmovilizar personaje por estética
        personaje.setPuedeMoverse(false);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                personaje.setPuedeMoverse(true);
            }
        }, 0.5f);

        Vector2 centro = new Vector2(
            personaje.getX() + personaje.getWidth() / 2f,
            personaje.getY() + personaje.getHeight() / 2f
        );

        Vector2 dir = new Vector2(personaje.direccionMirando).nor();
        DireccionUtil.Direccion direccionEnum = DireccionUtil.vectorADireccion(dir);

        Vector2 offsetVisual = startOffsets.getOrDefault(direccionEnum, new Vector2(0, 0));
        Vector2 offsetHitbox = hitboxOffsets.getOrDefault(direccionEnum, new Vector2(0, 0));

        Vector2 pos = centro.cpy().add(offsetVisual);

        proyectiles.add(new ProyectilRayo(
            pos,
            dir,
            velocidad,
            daño,
            animacionRayo,
            duracion,
            personaje,
            offsetHitbox
        ));
    }

    @Override
    protected void finalizarEfecto() {}

    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);

        Iterator<ProyectilRayo> it = proyectiles.iterator();
        while (it.hasNext()) {
            ProyectilRayo p = it.next();
            p.actualizar(delta);
//            if (p.estaMuerto()) {
//                it.remove();
//            }
        }
    }


    @Override
    public void dibujar(Batch batch, float x, float y, float width, float height) {
        for (ProyectilRayo p : proyectiles) {
            p.dibujar(batch);
        }
    }

    @Override
    public void dispose() {
        textura.dispose();
    }
}
