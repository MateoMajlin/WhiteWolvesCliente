package winterwolves.props;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import winterwolves.Dañable;

public class Caja extends Sprite implements Dañable {

    private Body body;
    private float ppm;
    private boolean activa = true;
    private boolean marcadaParaDestruir = false;

    private float vida;     // vida actual
    private float vidaMax;  // vida máxima

    private static BitmapFont font = new BitmapFont(); // fuente por defecto
    private static GlyphLayout layout = new GlyphLayout();

    private boolean mostrarVida = false;
    private float tiempoMostrarVida = 0f;
    private final float DURACION_MOSTRAR_VIDA = 2f;
    private final float VELOCIDAD_SUBIDA = 30f;

    public Caja(World world, float x, float y, float ppm, float vida) {
        super(new Texture("caja.png"));
        this.ppm = ppm;
        this.vida = vida;
        this.vidaMax = vida;

        setSize(32, 32);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth()/4f / ppm, getHeight()/4f / ppm);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef).setUserData(this);
        body.setUserData(this);

        shape.dispose();
    }

    @Override
    public void draw(Batch batch) {
        if (!activa || body == null) return;

        Vector2 pos = body.getPosition();
        setPosition(pos.x * ppm - getWidth() / 2, pos.y * ppm - getHeight() / 2);
        super.draw(batch);
    }

    public void actualizar(float delta) {
        if (mostrarVida) {
            tiempoMostrarVida += delta;
            if (tiempoMostrarVida >= DURACION_MOSTRAR_VIDA) {
                mostrarVida = false;
            }
        }
    }

    public void recibirDaño(float cantidad) {
        if (!activa) return;

        vida -= cantidad;
        mostrarVida = true;
        tiempoMostrarVida = 0f;
        if (vida <= 0) {
            vida = 0;
            destruir();
        }
    }

    public void drawVidaTexto(Batch batch) {
        if (!activa || body == null || !mostrarVida) return;

        String texto = (int) vida + " / " + (int) vidaMax;
        layout.setText(font, texto);

        float x = getX() + getWidth() / 2f - layout.width / 2f;

        float y = getY() + getHeight() + VELOCIDAD_SUBIDA * tiempoMostrarVida;

        // Alfa lineal
        float alfa = 1f - (tiempoMostrarVida / DURACION_MOSTRAR_VIDA);

        float porcentaje = vida / vidaMax;
        if (porcentaje > 0.6f) font.setColor(0, 1, 0, alfa);
        else if (porcentaje > 0.3f) font.setColor(1, 1, 0, alfa);
        else font.setColor(1, 0, 0, alfa);

        font.draw(batch, texto, x, y);
        font.setColor(1, 1, 1, 1);
    }



    public void destruir() {
        marcadaParaDestruir = true;
    }

    public boolean isMarcadaParaDestruir() {
        return marcadaParaDestruir;
    }

    public void eliminarDelMundo() {
        if (body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
        activa = false;
    }

    public void dispose() {
        if (getTexture() != null) {
            getTexture().dispose();
        }
    }
}
