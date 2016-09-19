package sasad.android.com.whatsperera.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Usuário on 16/09/2016.
 */
public class Preferencias {

    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "whatsap.preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificadorUsuario";

    public Preferencias(Context contextoParametro) {

        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();

    }

    public void salvarDados(String identificador){
        editor.putString(CHAVE_IDENTIFICADOR,identificador);
        editor.commit();
    }

    public String getIdenficiador(){
        return preferences.getString(CHAVE_IDENTIFICADOR,null);
    }


}
