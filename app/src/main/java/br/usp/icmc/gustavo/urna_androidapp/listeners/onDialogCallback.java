package br.usp.icmc.gustavo.urna_androidapp.listeners;

import br.usp.icmc.gustavo.urna_androidapp.model.Candidato;

public interface OnDialogCallback {

    void onDialogSucess(Candidato candidato);

    void onDialogFail(int numero);
}
