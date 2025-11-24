package winterwolves.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import winterwolves.elementos.Imagen;
import winterwolves.elementos.Texto;
import winterwolves.io.Entradas;
import winterwolves.utilidades.Config;
import winterwolves.utilidades.Recursos;
import winterwolves.utilidades.Render;

public class Menu implements Screen {

    Imagen fondo,mashoop;
    SpriteBatch b;

    Texto titulo;
    Texto opciones[] = new Texto[2];
    String textosOpc[] = {"Jugar en LAN","Salir"};

    Entradas entradas = new Entradas(this);

    int opc = 1;
    public float tiempo = 0;

    @Override
    public void show() {
        fondo = new Imagen(Recursos.FONDO);
        fondo.escalar(Config.WIDTH,Config.HEIGTH);
        mashoop = new Imagen(Recursos.MASHOOP);
        mashoop.setPosition(-50,0);
        b = Render.batch;
        cargarOpciones();
        Gdx.input.setInputProcessor(entradas);
    }

    @Override
    public void render(float delta) {
        actualizarTiempo(delta);
        manejarEntradas();
        actualizarColoresOpciones();
        dibujar();
    }

    private void actualizarTiempo(float delta) {
        tiempo += delta;
    }

    private void manejarEntradas() {

        if (entradas.isAbajo() && tiempo > 0.1f) {
            tiempo = 0;
            opc++;
            if (opc > opciones.length) {
                opc = 1;
            }
        }

        if (entradas.isArriba() && tiempo > 0.1f) {
            tiempo = 0;
            opc--;
            if (opc < 1) {
                opc = opciones.length;
            }
        }

        if (entradas.isEnter()) {
            ejecutarOpcion();
        }
    }

    private void actualizarColoresOpciones() {
        for (int i = 0; i < opciones.length; i++) {
            if (i == (opc - 1)) {
                opciones[i].setColor(Color.GREEN);
            } else {
                opciones[i].setColor(Color.WHITE);
            }
        }
    }

    private void ejecutarOpcion() {
        switch (opc) {
            case 1:
                Recursos.musica.stop();
                Recursos.musica.dispose();
                Render.app.setScreen(new PantallaSeleccion());
                break;
            case 2:
                Gdx.app.exit();
                break;
        }
    }

    private void dibujar() {
        b.begin();
        fondo.dibujar();
        mashoop.dibujar();
        titulo.dibujar();
        for (Texto t : opciones) {
            t.dibujar();
        }
        b.end();
    }

    private void cargarOpciones() {
        int avance = 80;

        titulo = new Texto(Recursos.FUENTEMENU,150,Color.BLACK,true);
        titulo.setTexto("WHITE WOLVES");
        titulo.setPosition((Config.WIDTH/2)-(titulo.getAncho()/2),
            (Config.HEIGTH/2)+(titulo.getAlto()/2)+200);

        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = new Texto(Recursos.FUENTEMENU,80,Color.WHITE,true);
            opciones[i].setTexto(textosOpc[i]);
            opciones[i].setPosition((Config.WIDTH/2)-(opciones[i].getAncho()/2),
                (Config.HEIGTH/2)+(opciones[i].getAlto()-(avance*i)));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        if (fondo != null) fondo.dispose();
        for (Texto t : opciones) {
            if (t != null) t.dispose();
        }
        if (titulo != null) titulo.dispose();
    }

}
