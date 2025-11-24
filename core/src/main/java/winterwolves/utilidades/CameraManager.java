package winterwolves.utilidades;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraManager {

    private OrthographicCamera camaraPrincipal;
    private OrthographicCamera camaraBox2D;
    private OrthographicCamera camaraHud;

    private float PPM;

    public CameraManager(float width, float height, float PPM) {
        this.PPM = PPM;

        camaraPrincipal = new OrthographicCamera();
        camaraPrincipal.setToOrtho(false, width, height);
        camaraPrincipal.position.set(width / 2f, height / 2f, 0);
        camaraPrincipal.update();

        camaraBox2D = new OrthographicCamera();
        camaraBox2D.setToOrtho(false, width / PPM, height / PPM);
        camaraBox2D.position.set((width / 2f) / PPM, (height / 2f) / PPM, 0);
        camaraBox2D.update();

        camaraHud = new OrthographicCamera();
        camaraHud.setToOrtho(false, width, height);
        camaraHud.position.set(width / 2f, height / 2f, 0);
        camaraHud.update();
    }

    public void seguir(Vector2 posicionJugador) {
        camaraPrincipal.position.set(posicionJugador.x, posicionJugador.y, 0);
        camaraPrincipal.update();

        camaraBox2D.position.set(posicionJugador.x / PPM, posicionJugador.y / PPM, 0);
        camaraBox2D.update();
    }

    public void resize(int width, int height) {
        camaraPrincipal.viewportWidth = width;
        camaraPrincipal.viewportHeight = height;
        camaraPrincipal.update();

        camaraBox2D.viewportWidth = width / PPM;
        camaraBox2D.viewportHeight = height / PPM;
        camaraBox2D.update();

        camaraHud.viewportWidth = width;
        camaraHud.viewportHeight = height;
        camaraHud.update();
    }

    public OrthographicCamera getPrincipal() {
        return camaraPrincipal;
    }

    public OrthographicCamera getBox2D() {
        return camaraBox2D;
    }

    public OrthographicCamera getHud() {
        return camaraHud;
    }
}
