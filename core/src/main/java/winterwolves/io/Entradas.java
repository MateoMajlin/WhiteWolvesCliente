package winterwolves.io;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import winterwolves.pantallas.Menu;
import winterwolves.pantallas.PantallaTutorial;

public class Entradas implements InputProcessor {

    private boolean abajo = false, arriba = false;
    private boolean enter = false;

    public void setAbajo(boolean abajo) {
        this.abajo = abajo;
    }

    public void setArriba(boolean arriba) {
        this.arriba = arriba;
    }

    public void setEnter(boolean enter) {
        this.enter = enter;
    }

    public Menu getMenu() {
        return menu;
    }

    public PantallaTutorial getPantallaTutorial() {
        return pantallaTutorial;
    }

    Menu menu;
    PantallaTutorial pantallaTutorial;

    public Entradas(Menu menu){
        this.menu = menu;
    }

    public Entradas(PantallaTutorial pantallaTutorial){
        this.pantallaTutorial = pantallaTutorial;
    }

    public boolean isEnter() {
        return enter;
    }

    public boolean isAbajo() {
        return abajo;
    }

    public boolean isArriba() {
        return arriba;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.DOWN){
            abajo = true;
        }
        if(keycode == Input.Keys.UP){
            arriba = true;
        }

        if(keycode == Input.Keys.ENTER){
            enter = true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.DOWN){
            abajo = false;
        }
        if(keycode == Input.Keys.UP){
            arriba = false;
        }
        if(keycode == Input.Keys.ENTER){
            enter = false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }
}
