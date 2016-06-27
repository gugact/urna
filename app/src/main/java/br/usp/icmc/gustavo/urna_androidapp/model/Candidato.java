package br.usp.icmc.gustavo.urna_androidapp.model;

import java.io.Serializable;

public class Candidato implements Serializable {

	private int codigo;
	private String nome;
	private String partido;
	private int nroVotos;


	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getPartido() {
		return partido;
	}

	public void setPartido(String partido) {
		this.partido = partido;
	}

	public int getNroVotos() {
		return nroVotos;
	}

	public void setNroVotos(int nroVotos) {
		this.nroVotos = nroVotos;
	}
}

