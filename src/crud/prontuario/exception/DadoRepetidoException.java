package crud.prontuario.exception;

public class DadoRepetidoException extends NegocioException {
    private static final long serialVersionUID = 1L;

	public DadoRepetidoException(String mensagem) {
        super(mensagem);
    }
}