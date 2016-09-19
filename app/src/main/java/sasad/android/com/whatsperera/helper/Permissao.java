package sasad.android.com.whatsperera.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuário on 16/09/2016.
 */
public class Permissao {

    public static boolean validaPermissoes(int requesteCode,Activity activity,String[] permissoes){

        List<String> listaPermissoes = new ArrayList<String>();

        //Percorre as permissoes passadas verificando qual ja tem permissão
        if (Build.VERSION.SDK_INT >= 23){
            for (String permissao : permissoes){
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;

                if (!validaPermissao){
                    listaPermissoes.add(permissao);
                }
            }

            //Caso a lista esteja vazia, não é necessário verificar permissão
            if (listaPermissoes.isEmpty()) return true;

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //solicita permissao
            ActivityCompat.requestPermissions(activity,novasPermissoes,requesteCode);
        }

        return true;
    }
}
