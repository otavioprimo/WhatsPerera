package sasad.android.com.whatsperera.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.model.Contato;

/**
 * Created by Usuário on 20/09/2016.
 */
public class ContatoAdapter extends ArrayAdapter<Contato> {

    private Context context;
    private ArrayList<Contato> contatos;

    public ContatoAdapter(Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.context = c;
        this.contatos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //Verifica se a lista esta vazia
        if (contatos != null) {
            //inicializa o objeto para montagem do layout
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta a view a partir do xml
            view = layoutInflater.inflate(R.layout.lista_contatos, parent, false);

            //Recupera elementos para exibição
            TextView textView = (TextView) view.findViewById(R.id.tv_nome);
            Contato contato = contatos.get(position);

            textView.setText(contato.getNome());
        }
        return view;
    }
}
