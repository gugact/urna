package br.usp.icmc.gustavo.urna_androidapp.controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import br.usp.icmc.gustavo.urna.R;
import br.usp.icmc.gustavo.urna_androidapp.listeners.onDialogCallback;
import br.usp.icmc.gustavo.urna_androidapp.model.Arquivo;
import br.usp.icmc.gustavo.urna_androidapp.model.Candidato;
import br.usp.icmc.gustavo.urna_androidapp.model.Cliente;



public class MainActivity extends AppCompatActivity implements View.OnClickListener, onDialogCallback {

    private  Button but_obterlist,but_enviarlist;
    private static final String ARQ_VOTOS = "votos.txt";
    private static final String ARQ_CANDIDATOS = "candidatosEmVotacao.txt";
    private static final String ARQ_FECHAMENTO_URNA = "fechamento.txt";
    private static int nroVotos = 0;
    private List<Candidato> listaCandidatos = null;
    private Arquivo arquivoCandidatos = null;
    private Arquivo arquivoVotos = null;
    private ImageView image;
    private EditText codigo;
    private Button selecionar , confirmar , branco , nulo, limpar;
    private TextView nomeTextView,partidoTextView;
    private Candidato ultimo_candidato;
    private Socket socket_entrada;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urna_main_activity);
        context = this;
        initView();
        initListeners();
        //startSocketConnection();
    }

    private void initView() {
        but_obterlist = (Button) findViewById(R.id.button4);
        but_enviarlist = (Button) findViewById(R.id.button5);
        image = (ImageView) findViewById(R.id.image);
        codigo = (EditText) findViewById(R.id.entrada_cod);
        selecionar = (Button) findViewById(R.id.botao_selecionar_candidato);
        confirmar =(Button) findViewById(R.id.button_confirm);
        branco = (Button) findViewById(R.id.button2);
        nulo = (Button) findViewById(R.id.button3);
        limpar = (Button) findViewById(R.id.button_erase);
        nomeTextView = (TextView) findViewById(R.id.nome_candidato);
        partidoTextView = (TextView) findViewById(R.id.nome_partido);
    }

    private void initListeners() {
        //adicionando os botoes e clicklistener para ser tratados no método onClick
        but_obterlist.setOnClickListener(this);
        but_enviarlist.setOnClickListener(this);
        selecionar.setOnClickListener(this);
        confirmar.setOnClickListener(this);
        branco.setOnClickListener(this);
        nulo.setOnClickListener(this);
        limpar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideSoftKeyboard(this);
        switch (v.getId()){
            //caso SELECIONAR,se tivermos a lista de candidatos ele vai pegar o código escrito no EditText
            //e procurar na lista de candidatos para ver se é uma entrada válida, se for entao ele mostra o candidato com seus informacoes
            case R.id.botao_selecionar_candidato:
                if(arquivoCandidatos!= null){
                        if (!"".equals(codigo.getText().toString())){
                            int codigo_candidato = 0;
                            try{
                                codigo_candidato = Integer.parseInt(codigo.getText().toString());
                            } catch (Exception ignored){
                                return;
                            }
                            boolean flag_candidato_invalido = true;
                            for(Candidato candidato: listaCandidatos) {
                                if(candidato.getCodigo() == codigo_candidato) {
                                    ultimo_candidato = candidato;
                                    String nome_candidato = candidato.getNome().toLowerCase().replaceAll("[^\\p{Alpha}]+","");
                                    nomeTextView.setText(candidato.getNome());
                                    partidoTextView.setText(candidato.getPartido());
                                    image.setImageResource(this.getResources().getIdentifier(nome_candidato, "drawable", this.getPackageName()));
                                    flag_candidato_invalido = false;
                                    break;
                                }
                            }
                            if(flag_candidato_invalido) {
                                image.setImageResource(R.drawable.brancos);
                                nomeTextView.setText("invalido");
                                partidoTextView.setText("invalido");
                            }
                        }else{
                            ListaCandidatos dialog = new ListaCandidatos(context, true, null, listaCandidatos, this);
                            dialog.setTitle("Escolha um Candidato");
                            dialog.show();
                        }
                }
                else{
                    Toast.makeText(getApplicationContext(), "É necessário obter a lista!", Toast.LENGTH_SHORT).show();
                }
                break;

            //caso CONFIRMAR_VOTO, aqui pega o último candidato válido selecionado e incrementa o número de votos para este candidato
            //caso nao tenha ele mostra uma mensagem de erro
            case R.id.button_confirm:
                if(arquivoCandidatos!= null){
                    if (!"".equals(codigo.getText().toString())){
                        arquivoVotos.gravarVoto(ultimo_candidato.getCodigo());
                        Toast.makeText(getApplicationContext(), "Voto realizado!", Toast.LENGTH_SHORT).show();
                        nroVotos++;
                        limpar.performClick();
                    }else{
                        Toast.makeText(getApplicationContext(), "É necessário selecionar um candidato válido!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "É necessário obter a lista!", Toast.LENGTH_SHORT).show();
                }
                break;

            //
            //caso VOTAR_BRANCO, registra um voto branco (código 2) se tiver a lista de candidatos,se nao tiver envia mensagem erro
            case R.id.button2:
                if(arquivoCandidatos!= null){
                    nroVotos++;
                    arquivoVotos.gravarVoto(2);
                    Toast.makeText(getApplicationContext(), "Voto realizado!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "É necessário obter a lista!", Toast.LENGTH_SHORT).show();
                }
                break;

            //
            //caso VOTAR_NULO,registra um voto nulo (código 3) se tiver a lista de candidatos,se nao tiver envia mensagem erro
            case R.id.button3:
                if(arquivoCandidatos!= null){
                    nroVotos++;
                    arquivoVotos.gravarVoto(3);
                    Toast.makeText(getApplicationContext(), "Voto realizado!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "É necessário obter a lista!", Toast.LENGTH_SHORT).show();
                }
                break;

            //
            //caso OBTER_LISTA, se nao tiver um arquivo dos candidatos, obtém ele usando um conexao (Cliente) com o servidor java
            //após obter, lê o arquivo e inicializa o arquivo de votos com os votos possíveis e o valor 0
            case R.id.button4:
                 if(arquivoCandidatos== null ){
                     /*if (socket_entrada != null && socket_entrada.isConnected()){
                         try {
                             socket_entrada.close();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }*/
                         Cliente conexao = new Cliente();
                        try {
                            File arquivo_recebido_candidatos = conexao.receberTransferenciaArquivoCandidatos(ARQ_CANDIDATOS, getApplicationContext().getCacheDir());
                            if(arquivo_recebido_candidatos != null){
                                arquivoCandidatos = new Arquivo(arquivo_recebido_candidatos);
                                listaCandidatos = arquivoCandidatos.listaCandidatos();
                                arquivoVotos = new Arquivo(new File(getApplicationContext().getCacheDir(), ARQ_VOTOS));
                                arquivoVotos.inicializarVotos(listaCandidatos);
                                Toast.makeText(getApplicationContext(), "Lista foi obtida!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Connection timeout, tente novamente", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException | InterruptedException err) {
                            err.printStackTrace();
                        }
                     /*}else{
                         Toast.makeText(getApplicationContext(), "Servidor Socket não esta acessivel", Toast.LENGTH_SHORT).show();
                         startSocketConnection();
                     }*/
                 } else{
                    Toast.makeText(getApplicationContext(), "Você já tem a lista!", Toast.LENGTH_SHORT).show();
                }
                break;

            //
            //caso ENVIAR_LISTA, se tiver um numero de votos maior do que 0, entao é feita a contagem dos votos e envio dos votos ao servidor, após isso a aplicacao é fechada
            case R.id.button5:

                hideSoftKeyboard(this);
                if (nroVotos > 0) {
                    Cliente conexao = new Cliente();
                    try {
                        Arquivo arquivoFechamento = contagemVotos(listaCandidatos, arquivoVotos);
                        conexao.enviarContagem(ARQ_FECHAMENTO_URNA,getApplicationContext().getCacheDir());

                        arquivoVotos.setArquivo(new File(getApplicationContext().getCacheDir(), ARQ_VOTOS));
                        arquivoVotos.inicializarVotos(listaCandidatos);
                        nroVotos = 0;
                        Toast.makeText(getApplicationContext(), "Votos foram registrados e a urna foi resetada!", Toast.LENGTH_SHORT).show();
                        limpar.performClick();
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nenhuma votação foi realizada!", Toast.LENGTH_SHORT).show();
                }
                break;

            //limpar seleção
            case R.id.button_erase:
                image.setImageResource(R.drawable.brancos);
                nomeTextView.setText("");
                partidoTextView.setText("");
                codigo.setText("");
                break;
        }
    }

    private void startSocketConnection() {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    socket_entrada = new Socket("cosmos.lasdpc.icmc.usp.br", 40007);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    //método para criar e preencher a urna fechada
    private Arquivo contagemVotos(List<Candidato> listaCandidatos, Arquivo arqVotos) {
        Arquivo arqFechamento = new Arquivo(new File(getApplicationContext().getCacheDir() ,ARQ_FECHAMENTO_URNA));
        arqFechamento.gerarFechamento(listaCandidatos, arqVotos);
        return arqFechamento;
    }

    //método para esconder teclado android
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null){
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onDialogSucess(Candidato candidato) {
        ultimo_candidato = candidato;
        codigo.setText(String.valueOf(candidato.getCodigo()));
        String nome_candidato = candidato.getNome().toLowerCase().replaceAll("[^\\p{Alpha}]+","");
        nomeTextView.setText(candidato.getNome());
        partidoTextView.setText(candidato.getPartido());
        image.setImageResource(this.getResources().getIdentifier(nome_candidato, "drawable", this.getPackageName()));
    }

    @Override
    public void onDialogFail(int numero) {

    }
}

