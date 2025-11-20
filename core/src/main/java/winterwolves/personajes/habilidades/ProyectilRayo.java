package winterwolves.personajes.habilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import winterwolves.personajes.Personaje;

public class ProyectilRayo {

    private Vector2 posicion;
    private Vector2 dir;
    private float velocidad;
    public int daño;
    private Animation<TextureRegion> animacion;
    private float stateTime = 0f;
    private float tiempoRestante;

    private float ancho = 256f;
    private float alto = 128f;

    public boolean muerto = false;

    private Personaje lanzador;

    private Vector2 offsetHitbox;

    public ProyectilRayo(
        Vector2 pos,
        Vector2 dir,
        float velocidad,
        int daño,
        Animation<TextureRegion> animacion,
        float duracion,
        Personaje lanzador,
        Vector2 offsetHitbox
    ) {
        this.dir = new Vector2(dir).nor();
        this.velocidad = velocidad;
        this.daño = daño;
        this.animacion = animacion;
        this.tiempoRestante = duracion;
        this.lanzador = lanzador;
        this.offsetHitbox = offsetHitbox != null ? offsetHitbox : new Vector2(0,0);

        // ❗ copiar comportamiento de Box2D:
        // Spawn = pos + offsetHitbox
        this.posicion = pos.cpy().add(this.offsetHitbox);
    }

    public void actualizar(float delta) {
        if (muerto) return;

        posicion.x += dir.x * velocidad * delta;
        posicion.y += dir.y * velocidad * delta;

        tiempoRestante -= delta;
        if (tiempoRestante <= 0) muerto = true;
    }

    public void dibujar(Batch batch) {
        if (muerto) return;

        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion frame = animacion.getKeyFrame(stateTime, true);

        float angle = dir.angleDeg();

        batch.draw(
            frame,
            posicion.x - ancho / 2f,
            posicion.y - alto / 2f,
            ancho / 2f,
            alto / 2f,
            ancho,
            alto,
            1f,
            1f,
            angle
        );
    }
}
