package winterwolves.principal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import winterwolves.Jugador;
import winterwolves.pantallas.Menu;
import winterwolves.pantallas.PantallaCarga;
import winterwolves.utilidades.Recursos;
import winterwolves.utilidades.Render;
import winterwolves.network.ClientThread;


public class Principal extends Game {
    private SpriteBatch batch;

    public SpriteBatch getBatch() {
        return batch;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void create() {
        Render.app = this;
        Render.batch = new SpriteBatch();
        // setearMusica();
        this.setScreen(new PantallaCarga());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (getScreen() != null) {
            getScreen().dispose();
        }

        Render.batch.dispose();
    }

    private void setearMusica() {
        Recursos.musica.play();
        Recursos.musica.setLooping(true);
        Recursos.musica.setVolume(0.3f);
    }
}
