package br.usp.icmc.gustavo.urna_androidapp.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.usp.icmc.gustavo.urna.R;
import br.usp.icmc.gustavo.urna_androidapp.model.Candidato;

public class ListaCandidatosAdapter extends ArrayAdapter<Candidato> {


    private ViewHolder holder;
    private LayoutInflater inflater;
    private Context context;

    public ListaCandidatosAdapter(Context context, int resource, List<Candidato> objects) {
        super(context, 0, objects);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_lista_candidatos, parent, false);

            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.item_imagem);
            holder.numero = (TextView) convertView.findViewById(R.id.item_numero);
            holder.pais = (TextView) convertView.findViewById(R.id.item_pais);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        Candidato candidato = getItem(position);

        String nome_candidato = candidato.getNome().toLowerCase().replaceAll("[^\\p{Alpha}]+","");
        holder.numero.setText("N° " + candidato.getCodigo());
        holder.pais.setText("Nome: " + candidato.getNome() + " - " + candidato.getPartido());
        holder.avatar.setImageResource(context.getResources().getIdentifier(nome_candidato, "drawable", context.getPackageName()));

        return convertView;
    }


    private class ViewHolder{

        private ImageView avatar;
        private TextView numero;
        private TextView pais;
//        private ImageView moreImageButton;
    }
}
