package br.usp.icmc.gustavo.urna_androidapp.model;

import java.io.*;
import java.util.*;

public class Arquivo {
	
	private File arquivo;

	public Arquivo(File arquivo) {
		this.arquivo = arquivo;
	}
	//carrega lista de candidatos de um arquivo
	public	List<Candidato> listaCandidatos() throws IOException {

        if(arquivo == null){
            return null;
        }
        List<Candidato> listaCandidatos = new ArrayList<>();
		BufferedReader buffReader =  new BufferedReader(new FileReader(this.arquivo));

        String linha;
        while((linha = buffReader.readLine()) != null) {
                        StringTokenizer st = new StringTokenizer(linha, "|");
                        Candidato candidato = new Candidato();
                        candidato.setCodigo(Integer.parseInt(st.nextToken()));
                        candidato.setNome(st.nextToken());
                        candidato.setPartido(st.nextToken());
                        candidato.setNroVotos(Integer.parseInt(st.nextToken()));
                        listaCandidatos.add(candidato);
        }
		buffReader.close();

                return listaCandidatos;
        }
	//inicia o arquivo de votos com valores iniciais
	public void inicializarVotos(List<Candidato> listaCandidatos) {
	
		try {
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(this.arquivo));
			for(Candidato candidato: listaCandidatos) {
				buffWriter.append(candidato.getCodigo() + " " + 0 + "\n");
			}
			buffWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	//atualiza o arquivo de votos com opção de voto passada
	public void gravarVoto(int opt) {

		List<Voto> listaVotos =  carregarVotos();
		try {
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(this.arquivo));
			for(Voto voto: listaVotos) {
				if(voto.getCodigo() == opt) {
					voto.setNroVotos(voto.getNroVotos() + 1);
					break;
				}
			}
			for(Voto voto: listaVotos) {
				buffWriter.append(voto.getCodigo() + " " + voto.getNroVotos() + "\n");
			}
			buffWriter.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	//gera aquivo com o fechamento da urna que em seguida será enviado para o servidor com a contagem dos votos
	public void gerarFechamento(List<Candidato> listaCandidatos, Arquivo arqVotos) {
		List<Voto> listaVotos = arqVotos.carregarVotos();
		for(Candidato candidato: listaCandidatos) {
			for(Voto voto : listaVotos) {
				if(candidato.getCodigo() == voto.getCodigo()) {
					candidato.setNroVotos(voto.getNroVotos());
					break;
				}
			}
		}
		
		try {
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(this.arquivo));
			for(Candidato candidato: listaCandidatos) {
				buffWriter.append(candidato.getCodigo() + "|" + candidato.getNome() + "|" +
						candidato.getPartido() + "|" + candidato.getNroVotos() + "|" + "\n");
			}
			buffWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//carrega votos do arquivo de votos
	public List<Voto> carregarVotos() {

		List<Voto> listaVotos = new ArrayList<>();
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(this.arquivo));
			String linha;

			while((linha = buffReader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(linha);
				Voto voto = new Voto();
				voto.setCodigo(Integer.parseInt(st.nextToken()));
				voto.setNroVotos(Integer.parseInt(st.nextToken()));
				listaVotos.add(voto);
			}
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listaVotos;
	}

	//deleta o File da classe
	public void deletarArq(){
		this.arquivo.delete();
	}

	//atribui valor ao File da classe
	public void setArquivo(File arquivo_novo){this.arquivo = arquivo_novo;}

}
