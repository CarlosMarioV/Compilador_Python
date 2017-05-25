package AnalizadorContextual;

import java.util.ArrayList;

/**
 * Created by esteb on 06/05/2017.
 */
public class Funcion {

    private String idFuncion;

    public ArrayList<Integer> getTiposParametros() {
        return tiposParametros;
    }

    private ArrayList<Integer> tiposParametros;
    private int tipoRetorno;

    public int getTipoRetorno() {
        return tipoRetorno;
    }

    public void setTiposParametros(ArrayList<Integer> tiposParametros) {
        this.tiposParametros = tiposParametros;
    }

    public void setTipoRetorno(int tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public Funcion(String id){
        this.idFuncion = id;
        this.tiposParametros = new ArrayList<>();
        this.tipoRetorno = 0;
    }

    public Funcion(String id, ArrayList<Integer> tiposParametros){
        this.idFuncion = id;
        this.tiposParametros = tiposParametros;
    }

    public String getIdFuncion() {
        return idFuncion;
    }
}
