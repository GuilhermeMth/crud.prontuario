package crud.prontuario.exception;

public class CampoObrigatorioVazioException extends NegocioException {
    private static final long serialVersionUID = 1L;

	public CampoObrigatorioVazioException(String mensagem) {
        super(mensagem);
    }
}
