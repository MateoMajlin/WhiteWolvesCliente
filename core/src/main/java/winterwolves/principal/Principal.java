package winterwolves.principal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import winterwolves.Jugador;
import winterwolves.pantallas.Menu;
import winterwolves.utilidades.Recursos;
import winterwolves.utilidades.Render;
import winterwolves.network.ClientThread;


public class Principal extends Game {
    private SpriteBatch batch;
    private Texture image;
    private ClientThread clientThread;

    //Setters y Getters
    public SpriteBatch getBatch() {
        return batch;
    }

    public Texture getImage() {
        return image;
    }

    public ClientThread getClientThread() {
        return clientThread;
    }

    public void setBatch(SpriteBatch batch) {
        this.batch = batch;
    }

    public void setImage(Texture image) {
        this.image = image;
    }

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }
    // Setters y Getters

    @Override
    public void create() {
        Render.app = this;
        Render.batch = new SpriteBatch();
        // setearMusica();
        this.setScreen(new Menu());
    }

    @Override
    public void render() {
        super.render();
    }

    private void update(){

    }

    @Override
    public void dispose() {
        //Mando mensaje de cierre, para lograr desconexion.
        // clientThread.sendMessage("Disconnect:"+ new String(1));
        Render.batch.dispose();

    }

    private void setearMusica() {
        Recursos.musica.play();
        Recursos.musica.setLooping(true);
        Recursos.musica.setVolume(0.3f);
    }
}
