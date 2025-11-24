package winterwolves;

import com.badlogic.gdx.graphics.Color;
import winterwolves.elementos.Texto;
import winterwolves.personajes.Personaje;
import winterwolves.utilidades.Config;
import winterwolves.utilidades.Recursos;

public class Partida {

    private Personaje pj1, pj2;
    private String nombrePj1, nombrePj2;

    private float tiempoRestante;
    private boolean partidaFinalizada;
    private boolean pj1YaContado, pj2YaContado;

    private Texto textoGanador;
    private Texto textoJugador1, textoJugador2, textoTiempo;

    public boolean forzarFinal = false;

    public Partida(String nombrePj1, Personaje pj1,
                   String nombrePj2, Personaje pj2,
                   float duracionSegundos) {

        this.nombrePj1 = nombrePj1;
        this.nombrePj2 = nombrePj2;
        this.pj1 = pj1;
        this.pj2 = pj2;
        this.tiempoRestante = duracionSegundos;
        this.partidaFinalizada = false;

        textoJugador1 = new Texto(Recursos.FUENTEMENU, 20, Color.BLUE, true);
        textoJugador2 = new Texto(Recursos.FUENTEMENU, 20, Color.RED, true);
        textoTiempo   = new Texto(Recursos.FUENTEMENU, 20, Color.YELLOW, true);
    }

    public void actualizar(float delta) {
        if (partidaFinalizada) return;

        tiempoRestante -= delta;

        if (pj1.estaMuerto() && !pj1YaContado) {
            pj2.incrementarKill();
            pj1YaContado = true;
        }
        if (pj2.estaMuerto() && !pj2YaContado) {
            pj1.incrementarKill();
            pj2YaContado = true;
        }

        if (!pj1.estaMuerto()) pj1YaContado = false;
        if (!pj2.estaMuerto()) pj2YaContado = false;


        if(forzarFinal) {
            victoriaPorDefault();
        }

        if (tiempoRestante <= 0) {
            finalizarPartida();
        }
    }

    private void finalizarPartida() {
        partidaFinalizada = true;

        String ganador;
        if (pj1.getKills() > pj2.getKills()) ganador = nombrePj1;
        else if (pj2.getKills() > pj1.getKills()) ganador = nombrePj2;
        else ganador = "EMPATE";

        textoGanador = new Texto(Recursos.FUENTEMENU, 50, Color.RED, true);
        textoGanador.setTexto("Ganador: " + ganador);
        textoGanador.centrar();
    }

    public void dibujarHUD() {
        textoJugador1.setTexto(nombrePj1 + ": " + pj1.getKills());
        textoJugador1.setPosition(20, Config.HEIGTH - 150);
        textoJugador1.dibujar();

        textoJugador2.setTexto(nombrePj2 + ": " + pj2.getKills());
        textoJugador2.setPosition(Config.WIDTH - 150, Config.HEIGTH - 150);
        textoJugador2.dibujar();

        int min = (int) (tiempoRestante / 60);
        int sec = (int) (tiempoRestante % 60);
        textoTiempo.setTexto(String.format("%02d:%02d", min, sec));
        textoTiempo.setPosition(Config.WIDTH / 2f - textoTiempo.getAncho() / 2f, Config.HEIGTH - 20);
        textoTiempo.dibujar();

        if (partidaFinalizada && textoGanador != null) {
            textoGanador.dibujar();
        }
    }

    public boolean isPartidaFinalizada() {
        return partidaFinalizada;
    }

    public Texto getTextoGanador() {
        return textoGanador;
    }

    public float getTiempoRestante() {
        return tiempoRestante;
    }

    public void victoriaPorDefault() {
        textoGanador = new Texto(Recursos.FUENTEMENU, 50, Color.RED, true);
        textoGanador.setTexto("Ganaste por Default");
        textoGanador.centrar();
        partidaFinalizada = true;
        forzarFinal = false;
    }
}
