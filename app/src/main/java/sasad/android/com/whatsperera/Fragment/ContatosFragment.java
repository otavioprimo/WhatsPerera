package sasad.android.com.whatsperera.Fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sasad.android.com.whatsperera.Activity.ConversaActivity;
import sasad.android.com.whatsperera.Adapter.ContatoAdapter;
import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Preferencias;
import sasad.android.com.whatsperera.model.Contato;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;
    private ValueEventListener valueEventListenerContato;
    DatabaseReference contatosReferencia;


    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        contatosReferencia.addValueEventListener(valueEventListenerContato);
    }

    @Override
    public void onStop() {
        super.onStop();
        contatosReferencia.removeEventListener(valueEventListenerContato);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Instanciar objetos
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Montar listview e adapter
        listView = (ListView) view.findViewById(R.id.lv_contatos);
        /*adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contatos,
                contatos
        );*/
        adapter = new ContatoAdapter(getActivity(),contatos);

        listView.setAdapter(adapter);

        //Recuperar contatos do firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String idenficadorUsuarioLogado = preferencias.getIdenficiador();


        DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
        contatosReferencia = databaseReferencia.child("contatos")
                .child(idenficadorUsuarioLogado);

        //listener para recuperar contatos
        valueEventListenerContato = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Listar contatos para usuario

                contatos.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //adicionar evento click no listview
        contatosReferencia.addValueEventListener(valueEventListenerContato);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ConversaActivity.class);

                //Recupera dados a serem passados
                Contato contato = contatos.get(position);

                //Envia dados para ConversaAcitivty
                intent.putExtra("nome",contato.getNome());
                intent.putExtra("email",contato.getEmail());
                startActivity(intent);
            }
        });


        return view;
    }

}
