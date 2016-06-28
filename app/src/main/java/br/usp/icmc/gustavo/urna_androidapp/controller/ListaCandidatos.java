package br.usp.icmc.gustavo.urna_androidapp.controller;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import br.usp.icmc.gustavo.urna.R;
import br.usp.icmc.gustavo.urna_androidapp.listeners.OnDialogCallback;
import br.usp.icmc.gustavo.urna_androidapp.model.Candidato;

public class ListaCandidatos extends Dialog {

    List<Candidato> candidatoList;
    private ListView listView;
    private Context context;
    private OnDialogCallback listener;

    protected ListaCandidatos(Context context, boolean cancelable,
                              OnCancelListener cancelListener,
                              List<Candidato> candidatoList,
                              OnDialogCallback onDialogCallback) {
        super(context, cancelable, cancelListener);
        this.candidatoList = candidatoList;
        this.context = context;
        this.listener = onDialogCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_lista_candidatos);

        initView();
        initAdapter();
        initListeners();
    }

    private void initListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onDialogSucess(candidatoList.get(position));
                dismiss();
            }
        });
    }

    private void initAdapter() {
        ListaCandidatosAdapter adapter = new ListaCandidatosAdapter(context, 0, candidatoList);
        listView.setAdapter(adapter);

    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lista_candidatos);
    }
}
