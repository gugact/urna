package br.usp.icmc.gustavo.urna_androidapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Cliente {

	private Socket socket_entrada;
    private Socket socket_saida;

	public Cliente() {


	}
	//solicita e recebe o arquivo de candidatos do servidor e faz uma copia no Cliente
	public File receberTransferenciaArquivoCandidatos(final String arqDestino, final File cache_dir) throws IOException {

		final File arquivo_candidatos = new File(cache_dir,arqDestino);
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					socket_entrada = new Socket("cosmos.lasdpc.icmc.usp.br", 40007);
					PrintWriter writer = new PrintWriter(socket_entrada.getOutputStream());

					BufferedReader buffReader = new BufferedReader(new InputStreamReader(socket_entrada.getInputStream(), "UTF-8"));

					BufferedWriter buffWriter = new BufferedWriter(new FileWriter(arquivo_candidatos));
					List<Candidato> listaCandidatos = new ArrayList<>();

					// enviando para o servidor solicitação de lista de candidatos
					writer.write("999" + "\n");
					writer.flush();
					// recebendo do server a lista e gravando em arquivo destino
					String linha;
                    String mensagem_inteira = "";
					boolean fim_da_mensagem = false;
                    buffReader.ready();
                    int count = 0;
                    //recebe o arquivo até que a última mensagem seja recebida ou até 1,5s (timeout)
					while (!fim_da_mensagem && count < 3){

						while((linha = buffReader.readLine()) != null) {

                            Log.d("CLIENTE","receber candidatos: " + linha);
							mensagem_inteira = mensagem_inteira + linha + "\n";


						}
                        fim_da_mensagem = mensagem_inteira.contains("Chun");
                        if (fim_da_mensagem){
                            buffWriter.append(mensagem_inteira);

                        }
                        if(fim_da_mensagem == false){
                            Thread.sleep(500);
                            count++;
                        }

					}





					buffReader.close();
					buffWriter.close();

					socket_entrada.close();




				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

				}
			}
		};
		t.start();

/*
		if (socket == null){
			return null;
		}
        */
        //verifica se arquivo foi escrito ou nao,se nao foi,deleta o arquivo e retorna nulo
        BufferedReader br = new BufferedReader(new FileReader(arquivo_candidatos));
        if(br.readLine()== null) {
            arquivo_candidatos.delete();
            return null;
        }


		return arquivo_candidatos;

	}
	//sinaliza e envia o arquivo de fechamento de urna para o servidor
	public void enviarContagem(String arqFechamento,File cache_dir_fechamento) throws IOException {

        final File arq_fechamento = new File(cache_dir_fechamento,arqFechamento);
        final Thread fechar = new Thread() {
            public void run() {
                try {
                    socket_saida = new Socket("cosmos.lasdpc.icmc.usp.br", 40007);
                    PrintWriter writer = new PrintWriter(socket_saida.getOutputStream());

                    // montando mensagem com votacao para enviar ao servidor
                    String mensagem_para_servidor= "888" + "\n";



                    BufferedReader buffReader = new BufferedReader(new FileReader(arq_fechamento));
                    String linha2;
                    boolean ultima_msg = false;
                    //vai incrementando a mensagem e no final envia a votacao completa
                    while(!ultima_msg){
                        while((linha2 = buffReader.readLine()) != null) {
                            Log.d("CLIENTE","enviarContagem: " + linha2);
                            //writer.write(linha2 + "\n");
                            //writer.flush();
                            mensagem_para_servidor = mensagem_para_servidor + linha2 + "\n";
                            //é necessário bolar um meio melhor de verificar final da mensagem
                            ultima_msg = linha2.contains("Chun");
                            if(ultima_msg){
                                writer.write(mensagem_para_servidor);
                                writer.flush();
                            }
                        }
                    }

                    buffReader.close();
                    writer.close();
                    socket_saida.close();



                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };
        fechar.start();



	}
}

